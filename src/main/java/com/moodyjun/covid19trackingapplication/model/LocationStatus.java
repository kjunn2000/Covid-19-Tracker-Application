package com.moodyjun.covid19trackingapplication.model;

public class LocationStatus {


        private String state;
        private String country;
        private int latestTotalCases;
        private int numCompareYesterday;

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

        public int getLatestTotalCases() {
            return latestTotalCases;
        }

        public void setLatestTotalCases(int latestTotalCases) {
            this.latestTotalCases = latestTotalCases;
        }

        public int getNumCompareYesterday() {
            return numCompareYesterday;
        }

        public void setNumCompareYesterday(int numCompareYesterday) {
            this.numCompareYesterday = numCompareYesterday;
        }

    @Override
    public String toString() {
        return "LocationStatus{" +
                "state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", latestTotalCases=" + latestTotalCases +
                ", numCompareYesterday=" + numCompareYesterday +
                '}';
    }
}