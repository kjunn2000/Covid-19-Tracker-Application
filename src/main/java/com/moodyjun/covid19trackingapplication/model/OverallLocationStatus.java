package com.moodyjun.covid19trackingapplication.model;

public class OverallLocationStatus {
    private Location location;
    private LocationStatus confirmedCases;
    private LocationStatus deathCases;
    private LocationStatus recoveredCases;

    public OverallLocationStatus(Location location, LocationStatus confirmedCases, LocationStatus deathCases, LocationStatus recoveredCases) {
        this.location = location;
        this.confirmedCases = confirmedCases;
        this.deathCases = deathCases;
        this.recoveredCases = recoveredCases;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public LocationStatus getConfirmedCases() {
        return confirmedCases;
    }

    public void setConfirmedCases(LocationStatus confirmedCases) {
        this.confirmedCases = confirmedCases;
    }

    public LocationStatus getDeathCases() {
        return deathCases;
    }

    public void setDeathCases(LocationStatus deathCases) {
        this.deathCases = deathCases;
    }

    public LocationStatus getRecoveredCases() {
        return recoveredCases;
    }

    public void setRecoveredCases(LocationStatus recoveredCases) {
        this.recoveredCases = recoveredCases;
    }
}
