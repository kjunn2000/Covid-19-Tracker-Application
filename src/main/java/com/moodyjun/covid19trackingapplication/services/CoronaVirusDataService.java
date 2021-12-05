package com.moodyjun.covid19trackingapplication.services;

import com.moodyjun.covid19trackingapplication.model.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.moodyjun.covid19trackingapplication.model.DataType.*;

@Service
public class CoronaVirusDataService {

    @Autowired
    private PrologService prologService;

    private static final String CONFIRMED_CASES_URL = "https://raw.githubusercontent.com/CSSEGISandData/" +
            "COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    private static final String DEATH_CASES_URL = "https://raw.githubusercontent.com/CSSEGISandData/" +
            "COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_global.csv";
    private static final String RECOVERED_CASES_URL = "https://raw.githubusercontent.com/CSSEGISandData/" +
            "COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_recovered_global.csv";

    private List<OverallLocationStatus> summaryData;

    private final Map<String, TemporalAdjuster> adjusters = new HashMap<>();

    public CoronaVirusDataService() {
        adjusters.put("week", TemporalAdjusters.previousOrSame(DayOfWeek.of(1)));
        adjusters.put("month", TemporalAdjusters.firstDayOfMonth());
    }

    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    private void updateLatestCases() throws IOException, InterruptedException {
        Iterable<CSVRecord> confirmedRecords= fetchLatestData(CONFIRMED_CASES_URL);
        Iterable<CSVRecord> deathRecords = fetchLatestData(DEATH_CASES_URL);
        Iterable<CSVRecord> recoveredRecords = fetchLatestData(RECOVERED_CASES_URL);

        List<LocationStatus> confirmedCovidData = new ArrayList<>(translateCovidData(confirmedRecords, CONFIRMED));
        List<LocationStatus> deathCovidData = new ArrayList<>(translateCovidData(deathRecords, DEATH));
        List<LocationStatus> recoveredCovidData = new ArrayList<>(translateCovidData(recoveredRecords, RECOVERED));

        groupAllCovidDataByState(confirmedCovidData, deathCovidData, recoveredCovidData);

        prologService.writeToPrologFile(confirmedCovidData);
    }

    private Iterable<CSVRecord> fetchLatestData(String url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        StringReader csvBodyReader = new StringReader(httpResponse.body());
        return CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
    }

    private List<LocationStatus> translateCovidData(Iterable<CSVRecord> records, DataType dataType) {
        return StreamSupport
                .stream(records.spliterator(), false)
                .map((CSVRecord csvRecord) -> convertToLocationRecord(csvRecord, dataType))
                .collect(Collectors.toList());
    }

    private final Function<List<Integer> ,Integer> extractMaximumCases = list -> list.stream()
            .mapToInt(Integer::valueOf).max().orElse(0);

    private LocationStatus convertToLocationRecord(final CSVRecord csvRecord, DataType dataType){
        List<Record> recordList = getDateHeaders(csvRecord).stream().map(header -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yy");
            LocalDate date = LocalDate.parse(header, formatter);
            return new Record(date, Integer.parseInt(csvRecord.get(header)));
        }).collect(Collectors.toList());

        List<Integer> intCases = recordList.stream()
                .mapToInt(Record::getCases)
                .boxed()
                .collect(Collectors.toList());

        int totalCases = dataType.equals(RECOVERED) ? extractMaximumCases.apply(intCases)
                : Integer.parseInt(csvRecord.get(csvRecord.size()-1));

        Map<LocalDate, List<Record>> week = groupConfirmedCases(recordList, "week");
        Map<LocalDate, List<Record>> month = groupConfirmedCases(recordList, "month");

        return new LocationStatus(new Location( csvRecord.get(0), csvRecord.get(1), csvRecord.get(2), csvRecord.get(3)),
                dataType,
                totalCases,
                maximumCasesPerDay(recordList.size(), intCases),
                minimumCasesPerDay(recordList.size(), intCases),
                recordList,
                calculateGroupRecordSumCases(week),
                calculateGroupRecordSumCases(month)
                );
    }

    private List<String> getDateHeaders(CSVRecord csvRecord) {
        List<String> headers = csvRecord.getParser().getHeaderNames();
        return headers.subList(4,headers.size());
    }

    private Map<LocalDate, List<Record>> groupConfirmedCases(List<Record> recordList, final String groupBy){
        return recordList.stream()
                .collect(Collectors.groupingBy(record -> record.getDate()
                        .with(adjusters.get(groupBy))));
    }

    private final Function<Integer, Function<Integer, Integer>> max = num1 -> num2 -> num1 > num2 ? num1 : num2;
    private final Function<Integer, Function<Integer, Integer>> min = num1 -> num2 -> num1 < num2 ? num1 : num2;

    private Integer maximumCasesPerDay(int size, List<Integer> casesList) {
        if (size == 1) {
            return casesList.get(0);
        }

        return max.apply(max.apply(casesList.get(size-1) - casesList.get(size-2)).apply(0)).apply(
                maximumCasesPerDay(size - 1, casesList));

    }

    private Integer minimumCasesPerDay(int size, List<Integer> casesList) {
        if (size == 1) {
            return casesList.get(0);
        }

        return min.apply(max.apply(casesList.get(size-1) - casesList.get(size-2)).apply(0)).apply(
                minimumCasesPerDay(size - 1, casesList));
    }

    private List<Map.Entry<LocalDate, Integer>> calculateGroupRecordSumCases(Map<LocalDate, List<Record>> entryStream) {
        return entryStream.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> {
            List<Record> recordList = entry.getValue();
            recordList.sort(Comparator.comparing(Record::getDate));
            return recordList.get(recordList.size() - 1).getCases()
                    - recordList.get(0).getCases();
        })).entrySet().stream().sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toList());
    }

    BiFunction<Location, List<LocationStatus>, LocationStatus> getLocationStatusFromList = (location, locationStatuses) ->
            locationStatuses.stream()
                    .filter(locationStatus -> locationStatus.getLocation().equals(location))
                    .findFirst()
                    .orElse(null);

    private void groupAllCovidDataByState(List<LocationStatus> confirmedCovidData, List<LocationStatus> deathCovidData, List<LocationStatus> recoveredCovidData){
        List<Location> locations = confirmedCovidData.stream().map(LocationStatus::getLocation).collect(Collectors.toList());
        this.summaryData = locations.stream().map(location -> (
                        new OverallLocationStatus(location,
                                getLocationStatusFromList.apply(location, confirmedCovidData),
                                getLocationStatusFromList.apply(location, deathCovidData),
                                getLocationStatusFromList.apply(location, recoveredCovidData))))
                .collect(Collectors.toList());
    }

    public List<OverallLocationStatus> getSummaryData() {
        return summaryData;
    }

    public List<OverallLocationStatus> getSummaryDataByCountry(String country) {
        return summaryData.stream()
                .filter(overallLocationStatus -> overallLocationStatus
                        .getLocation().getCountry().toLowerCase().contains(country.toLowerCase()))
                .collect(Collectors.toList());
    }
}