package com.mapohl.nyckafka.taxiride.domain.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;

public class NYCTaxiRideUtils {

    private static DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .append(ISO_LOCAL_TIME)
            .toFormatter();

    public static Instant parse(String timeStr) {
        return LocalDateTime.parse(timeStr, DATE_TIME_FORMATTER).toInstant(ZoneOffset.UTC);
    }

    public static String format(Instant time) {
        return DATE_TIME_FORMATTER.format(time);
    }
}
