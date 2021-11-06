package com.moodyjun.covid19trackingapplication.services;

import com.moodyjun.covid19trackingapplication.model.Location;
import com.moodyjun.covid19trackingapplication.model.LocationStatus;
import com.moodyjun.covid19trackingapplication.model.Record;
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

@Service
public class CoronaVirusDataService {

    private static final String CONFIRMED_CASES_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    private static final String DEATH_CASES_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_global.csv";
    private static final String RECOVERED_CASES_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_recovered_global.csv";
    private final Map<String, TemporalAdjuster> adjusters = new HashMap<>();
    private final List<LocationStatus> confirmedCovidData = new ArrayList<>();
    private final List<LocationStatus> deathCovidData = new ArrayList<>();
    private final List<LocationStatus> recoveredCovidData = new ArrayList<>();

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
        this.confirmedCovidData.addAll(translateCovidData(confirmedRecords));
        this.confirmedCovidData.addAll(translateCovidData(deathRecords));
        this.confirmedCovidData.addAll(translateCovidData(recoveredRecords));
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

    public List<LocationStatus> translateCovidData(Iterable<CSVRecord> records) {
        return StreamSupport
                .stream(records.spliterator(), false)
                .map(this::convertToLocationRecord)
                .collect(Collectors.toList());
    }

    public LocationStatus convertToLocationRecord(final CSVRecord csvRecord){
        List<String> headers = csvRecord.getParser().getHeaderNames();
        List<String> dateHeaders = headers.subList(4,headers.size());

        List<Record> recordList = dateHeaders.stream().map(header -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yy");
            LocalDate date = LocalDate.parse(header, formatter);
            return new Record(date, Integer.parseInt(csvRecord.get(header)));
        }).collect(Collectors.toList());

        Map<LocalDate, List<Record>> week = groupConfirmedCases(recordList, "week");
        Map<LocalDate, List<Record>> month= groupConfirmedCases(recordList, "month");

        List<Integer> intCases = recordList.stream()
                .mapToInt(Record::getCases)
                .boxed()
                .collect(Collectors.toList());


        return new LocationStatus(
                new Location(
                        csvRecord.get(0),
                        csvRecord.get(1)),
                Integer.parseInt(csvRecord.get(csvRecord.size()-1)),
                maximumCases(recordList.size(), intCases),
                minimumCases(recordList.size(), intCases),
                recordList, week, month,
                calculateGroupRecordSumCases(week),
                calculateGroupRecordSumCases(month)
                );
    }

    public Map<LocalDate, List<Record>> groupConfirmedCases(List<Record> recordList, final String groupBy){
        return recordList.stream()
                .collect(Collectors.groupingBy(record -> record.getDate()
                        .with(adjusters.get(groupBy))));
    }

    public Map<LocalDate, Integer> calculateGroupRecordSumCases(Map<LocalDate, List<Record>> recordMap) {
        return recordMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> {
            List<Record> recordList = entry.getValue();
            recordList.sort(Comparator.comparing(Record::getDate));
            return recordList.get(recordList.size() - 1).getCases()
                    - recordList.get(0).getCases();
        }));
    }

    public Integer maximumCases(int size, List<Integer> casesList) {
       if (size == 1) {
           return casesList.get(0);
       }

       return Math.max(casesList.get(size-1) - casesList.get(size-2),
               maximumCases(size - 1, casesList));

    }

    public Integer minimumCases(int size, List<Integer> casesList) {
        if (size == 1) {
            return casesList.get(0);
        }

        return Math.min(casesList.get(size-1) - casesList.get(size-2),
                minimumCases(size - 1, casesList));

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
}