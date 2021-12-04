package com.moodyjun.covid19trackingapplication.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class LocationStatus {
    private Location location;
    private DataType dataType;
    private Integer totalCases;
    private Integer highestIncreaseCases;
    private Integer lowestIncreaseCases;
    private List<Record> recordList;
    private List<Map.Entry<LocalDate, Integer>> weeklyCases;
    private List<Map.Entry<LocalDate, Integer>> monthlyCases;

    public LocationStatus(Location location, DataType dataType, Integer totalCases, Integer highestIncreaseCases, Integer lowestIncreaseCases, List<Record> recordList, List<Map.Entry<LocalDate, Integer>> weeklyCases, List<Map.Entry<LocalDate, Integer>> monthlyCases) {
        this.location = location;
        this.dataType = dataType;
        this.totalCases = totalCases;
        this.highestIncreaseCases = highestIncreaseCases;
        this.lowestIncreaseCases = lowestIncreaseCases;
        this.recordList = recordList;
        this.weeklyCases = weeklyCases;
        this.monthlyCases = monthlyCases;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
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

    public List<Map.Entry<LocalDate, Integer>> getWeeklyCases() {
        return weeklyCases;
    }

    public void setWeeklyCases(List<Map.Entry<LocalDate, Integer>> weeklyCases) {
        this.weeklyCases = weeklyCases;
    }

    public List<Map.Entry<LocalDate, Integer>> getMonthlyCases() {
        return monthlyCases;
    }

    public void setMonthlyCases(List<Map.Entry<LocalDate, Integer>> monthlyCases) {
        this.monthlyCases = monthlyCases;
    }
}
