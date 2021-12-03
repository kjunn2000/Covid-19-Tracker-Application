package com.moodyjun.covid19trackingapplication.model;

import java.util.Objects;

public class Location {
    private String state;
    private String country;
    private String Lat;
    private String Long;

    public Location(String state, String country, String lat, String aLong) {
        this.state = state;
        this.country = country;
        Lat = lat;
        Long = aLong;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLat() {
        return Lat;
    }

    public void setLat(String lat) {
        Lat = lat;
    }

    public String getLong() {
        return Long;
    }

    public void setLong(String aLong) {
        Long = aLong;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(state, location.state) && Objects.equals(country, location.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, country);
    }
}
