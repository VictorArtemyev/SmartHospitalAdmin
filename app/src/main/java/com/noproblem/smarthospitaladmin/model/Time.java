package com.noproblem.smarthospitaladmin.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Victor Artemyev on 25/11/2016.
 */

@IgnoreExtraProperties
public class Time {
    private String from;
    private String to;

    public Time() {
    }

    public Time(String from, String to) {
        this.from = from;
        this.to = to;
    }

    @Override public String toString() {
        return "Time{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                '}';
    }
}
