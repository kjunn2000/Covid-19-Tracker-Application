package com.moodyjun.covid19trackingapplication.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class LocationStatus {
    private Location location;
    private Integer totalCases;
    private Integer highestIncreaseCases;
    private Integer lowestIncreaseCases;
    private List<Record> recordList;
    private Map<LocalDate, List<Record>> weeklyRecord;
    private Map<LocalDate, List<Record>> monthlyRecord;
    private Map<LocalDate, Integer> weeklyCases;
    private Map<LocalDate, Integer> monthlyCases;

    public LocationStatus(Location location, Integer totalCases, Integer highestIncreaseCases, Integer lowestIncreaseCases, List<Record> recordList, Map<LocalDate, List<Record>> weeklyRecord, Map<LocalDate, List<Record>> monthlyRecord, Map<LocalDate, Integer> weeklyCases, Map<LocalDate, Integer> monthlyCases) {
        this.location = location;
        this.totalCases = totalCases;
        this.highestIncreaseCases = highestIncreaseCases;
        this.lowestIncreaseCases = lowestIncreaseCases;
        this.recordList = recordList;
        this.weeklyRecord = weeklyRecord;
        this.monthlyRecord = monthlyRecord;
        this.weeklyCases = weeklyCases;
        this.monthlyCases = monthlyCases;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Integer getTotalCases() {
        return totalCases;
    }

    public void setTotalCases(Integer totalCases) {
        this.totalCases = totalCases;
    }

    public Integer getHighestIncreaseCases() {
        return highestIncreaseCases;
    }

    public void setHighestIncreaseCases(Integer highestIncreaseCases) {
        this.highestIncreaseCases = highestIncreaseCases;
    }

    public Integer getLowestIncreaseCases() {
        return lowestIncreaseCases;
    }

    public void setLowestIncreaseCases(Integer lowestIncreaseCases) {
        this.lowestIncreaseCases = lowestIncreaseCases;
    }

    public List<Record> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<Record> recordList) {
        this.recordList = recordList;
    }

    public Map<LocalDate, List<Record>> getWeeklyRecord() {
        return weeklyRecord;
    }

    public void setWeeklyRecord(Map<LocalDate, List<Record>> weeklyRecord) {
        this.weeklyRecord = weeklyRecord;
    }

    public Map<LocalDate, List<Record>> getMonthlyRecord() {
        return monthlyRecord;
    }

    public void setMonthlyRecord(Map<LocalDate, List<Record>> monthlyRecord) {
        this.monthlyRecord = monthlyRecord;
    }

    public Map<LocalDate, Integer> getWeeklyCases() {
        return weeklyCases;
    }

    public void setWeeklyCases(Map<LocalDate, Integer> weeklyCases) {
        this.weeklyCases = weeklyCases;
    }

    public Map<LocalDate, Integer> getMonthlyCases() {
        return monthlyCases;
    }

    public void setMonthlyCases(Map<LocalDate, Integer> monthlyCases) {
        this.monthlyCases = monthlyCases;
    }
}
