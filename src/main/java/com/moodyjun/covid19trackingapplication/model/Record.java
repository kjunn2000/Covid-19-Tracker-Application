package com.moodyjun.covid19trackingapplication.model;

import java.time.LocalDate;

public class Record {
    private LocalDate date;
    private int cases;

    public Record(LocalDate date, int cases) {
        this.date = date;
        this.cases = cases;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getCases() {
        return cases;
    }

    public void setCases(int cases) {
        this.cases = cases;
    }
}
