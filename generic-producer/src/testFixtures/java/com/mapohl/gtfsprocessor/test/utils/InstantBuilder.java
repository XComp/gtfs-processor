package com.mapohl.gtfsprocessor.test.utils;

import com.google.common.base.Preconditions;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;

public class InstantBuilder {

    private static final ZoneOffset DEFAULT_TIME_ZONE = ZoneOffset.UTC;

    private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .append(ISO_LOCAL_TIME)
            .toFormatter()
            .withZone(DEFAULT_TIME_ZONE);

    private final DateTimeFormatter dateTimeFormatter;

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

    public InstantBuilder() {
        this(1970, 1, 1, 0, 0, 0);
    }

    public InstantBuilder(int defaultYear, int defaultMonth, int defaultDay, int defaultHour, int defaultMinute, int defaultSecond) {
        this(DEFAULT_DATE_TIME_FORMATTER, defaultYear, defaultMonth, defaultDay, defaultHour, defaultMinute, defaultSecond);
    }

    public InstantBuilder(DateTimeFormatter dateTimeFormatter, int defaultYear, int defaultMonth, int defaultDay, int defaultHour, int defaultMinute, int defaultSecond) {
        this.dateTimeFormatter = dateTimeFormatter;

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
        Instant instant = LocalDateTime.of(year, month, day, hour, minute, second).toInstant(DEFAULT_TIME_ZONE);
        this.initialize();

        return instant;
    }

    public String toString() {
        return dateTimeFormatter.format(build());
    }

    public InstantBuilder year(int year) {
        this.year = year;
        return this;
    }

    public InstantBuilder month(int month) {
        Preconditions.checkArgument(month > 0 && month <= 12);

        this.month = month;
        return this;
    }

    public InstantBuilder day(int day) {
        Preconditions.checkArgument(day > 0 && day <= 31);

        this.day = day;
        return this;
    }

    public InstantBuilder hour(int hour) {
        Preconditions.checkArgument(hour >= 0 && hour < 24);

        this.hour = hour;
        return this;
    }

    public InstantBuilder minute(int minute) {
        Preconditions.checkArgument(minute >= 0 && minute < 60);

        this.minute = minute;
        return this;
    }

    public InstantBuilder second(int second) {
        Preconditions.checkArgument(second >= 0 && second < 60);

        this.second = second;
        return this;
    }
}
