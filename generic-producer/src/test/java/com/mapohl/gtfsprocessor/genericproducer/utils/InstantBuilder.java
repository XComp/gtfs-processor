package com.mapohl.gtfsprocessor.genericproducer.utils;

import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.Instant;

@Accessors(fluent = true)
@Setter
public class InstantBuilder {

    private final int defaultYear;
    private final int defaultMonth;
    private final int defaultDay;
    private final int defaultHour;
    private final int defaultMinute;
    private final int defaultSecond;

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;

    public InstantBuilder(int defaultYear, int defaultMonth, int defaultDay, int defaultHour, int defaultMinute, int defaultSecond) {
        this.defaultYear = defaultYear;
        this.defaultMonth = defaultMonth;
        this.defaultDay = defaultDay;
        this.defaultHour = defaultHour;
        this.defaultMinute = defaultMinute;
        this.defaultSecond = defaultSecond;

        this.initialize();
    }

    private void initialize() {
        this.year = this.defaultYear;
        this.month = this.defaultMonth;
        this.day = this.defaultDay;
        this.hour = this.defaultHour;
        this.minute = this.defaultMinute;
        this.second = this.defaultSecond;
    }

    public Instant build() {
        Instant instant = Instant.parse(String.format("%04d-%02d-%02dT%02d:%02d:%02dZ", year, month, day, hour, minute, second));
        this.initialize();

        return instant;
    }
}
