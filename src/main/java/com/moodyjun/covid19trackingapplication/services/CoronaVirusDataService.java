package com.moodyjun.covid19trackingapplication.services;

import com.moodyjun.covid19trackingapplication.model.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.moodyjun.covid19trackingapplication.model.DataType.*;

@Service
public class CoronaVirusDataService {

    private static final String CONFIRMED_CASES_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    private static final String DEATH_CASES_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_global.csv";
    private static final String RECOVERED_CASES_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_recovered_global.csv";
    private final Map<String, TemporalAdjuster> adjusters = new HashMap<>();
    private final List<LocationStatus> confirmedCovidData = new ArrayList<>();
    private final List<LocationStatus> deathCovidData = new ArrayList<>();
    private final List<LocationStatus> recoveredCovidData = new ArrayList<>();
    private List<OverallLocationStatus> summaryData;

    public CoronaVirusDataService() {
        adjusters.put("week", TemporalAdjusters.previousOrSame(DayOfWeek.of(1)));
        adjusters.put("month", TemporalAdjusters.firstDayOfMonth());
    }

    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void updateLatestCases() throws IOException, InterruptedException {
        Iterable<CSVRecord> confirmedRecords= fetchLatestData(CONFIRMED_CASES_URL);
        Iterable<CSVRecord> deathRecords = fetchLatestData(DEATH_CASES_URL);
        Iterable<CSVRecord> recoveredRecords = fetchLatestData(RECOVERED_CASES_URL);

        clearAllData();
        this.confirmedCovidData.addAll(translateCovidData(confirmedRecords, CONFIRMED));
        this.deathCovidData.addAll(translateCovidData(deathRecords, DEATH));
        this.recoveredCovidData.addAll(translateCovidData(recoveredRecords, RECOVERED));
        groupAllCovidDataPerState();
    }

    public void clearAllData(){
        this.confirmedCovidData.clear();
        this.deathCovidData.clear();
        this.recoveredCovidData.clear();
    }

    public Iterable<CSVRecord> fetchLatestData(String url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        StringReader csvBodyReader = new StringReader(httpResponse.body());
        return CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
    }

    public List<LocationStatus> translateCovidData(Iterable<CSVRecord> records, DataType dataType) {
        return StreamSupport
                .stream(records.spliterator(), false)
                .map((CSVRecord csvRecord) -> convertToLocationRecord(csvRecord, dataType))
                .collect(Collectors.toList());
    }

    public LocationStatus convertToLocationRecord(final CSVRecord csvRecord, DataType dataType){
        List<String> headers = csvRecord.getParser().getHeaderNames();
        List<String> dateHeaders = headers.subList(4,headers.size());

        List<Record> recordList = dateHeaders.stream().map(header -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yy");
            LocalDate date = LocalDate.parse(header, formatter);
            return new Record(date, Integer.parseInt(csvRecord.get(header)));
        }).collect(Collectors.toList());

        Map<LocalDate, List<Record>> week = groupConfirmedCases(recordList, "week");
        Map<LocalDate, List<Record>> month = groupConfirmedCases(recordList, "month");

        List<Integer> intCases = recordList.stream()
                .mapToInt(Record::getCases)
                .boxed()
                .collect(Collectors.toList());

        int totalCases = dataType.equals(RECOVERED) ? extractMaximumCases(csvRecord).orElse(0)
                : Integer.parseInt(csvRecord.get(csvRecord.size()-1));

        return new LocationStatus(
                new Location(
                        csvRecord.get(0),
                        csvRecord.get(1),
                        csvRecord.get(2),
                        csvRecord.get(3)),
                dataType,
                totalCases,
                maximumCasesPerDay(recordList.size(), intCases),
                minimumCasesPerDay(recordList.size(), intCases),
                recordList,
                calculateGroupRecordSumCases(week),
                calculateGroupRecordSumCases(month)
                );
    }

    private OptionalInt extractMaximumCases(CSVRecord record) {
        List<Integer> actualList = new ArrayList<>();
        record.iterator().forEachRemaining(data -> {
            try {
                Integer cases = Integer.valueOf(data);
                actualList.add(cases);
            }catch(Exception ignored){ }
        });
        return actualList.stream().mapToInt(Integer::valueOf).max();
    }

    public Map<LocalDate, List<Record>> groupConfirmedCases(List<Record> recordList, final String groupBy){
        return recordList.stream()
                .collect(Collectors.groupingBy(record -> record.getDate()
                        .with(adjusters.get(groupBy))));
    }

    public List<Map.Entry<LocalDate, Integer>> calculateGroupRecordSumCases(Map<LocalDate, List<Record>> entryStream) {
        return entryStream.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> {
            List<Record> recordList = entry.getValue();
            recordList.sort(Comparator.comparing(Record::getDate));
            return recordList.get(recordList.size() - 1).getCases()
                    - recordList.get(0).getCases();
        })).entrySet().stream().sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toList());
    }

    public Integer maximumCasesPerDay(int size, List<Integer> casesList) {
       if (size == 1) {
           return casesList.get(0);
       }

       return Math.max(casesList.get(size-1) - casesList.get(size-2),
               maximumCasesPerDay(size - 1, casesList));

    }

    public Integer minimumCasesPerDay(int size, List<Integer> casesList) {
        if (size == 1) {
            return casesList.get(0);
        }

        return Math.min(casesList.get(size-1) - casesList.get(size-2),
                minimumCasesPerDay(size - 1, casesList));

    }

    public void groupAllCovidDataPerState(){
        List<Location> locations = confirmedCovidData.stream().map(LocationStatus::getLocation).collect(Collectors.toList());
        this.summaryData = locations.stream().map(location -> (
                        new OverallLocationStatus(location,
                                getLocationStatusFromList(location, confirmedCovidData),
                                getLocationStatusFromList(location, deathCovidData),
                                getLocationStatusFromList(location, recoveredCovidData))))
                .collect(Collectors.toList());
    }

    public LocationStatus getLocationStatusFromList(Location location, List<LocationStatus> locationStatuses){
        return locationStatuses.stream().filter(locationStatus -> locationStatus.getLocation().equals(location)).findFirst().orElse(null);
    }

    public List<LocationStatus> getConfirmedCovidData() {
        return confirmedCovidData;
    }

    public List<LocationStatus> getDeathCovidData() {
        return deathCovidData;
    }

    public List<LocationStatus> getRecoveredCovidData() {
        return recoveredCovidData;
    }

    public List<OverallLocationStatus> getSummaryData() {
        return summaryData;
    }
}