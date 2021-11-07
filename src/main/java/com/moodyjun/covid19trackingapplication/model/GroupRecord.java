package com.moodyjun.covid19trackingapplication.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class GroupRecord {
    private Location location;
    private Map<LocalDate, List<Record>> dateMap;
    private Map<LocalDate, Integer> casesMap;

    public GroupRecord(Location location, Map<LocalDate, List<Record>> dateMap) {
        this.location = location;
        this.dateMap = dateMap;
    }

    public Map<LocalDate, Integer> getCasesMap() {
        return casesMap;
    }

    public void setCasesMap(Map<LocalDate, Integer> casesMap) {
        this.casesMap = casesMap;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Map<LocalDate, List<Record>> getDateMap() {
        return dateMap;
    }

    public void setDateMap(Map<LocalDate, List<Record>> dateMap) {
        this.dateMap = dateMap;
    }
}
