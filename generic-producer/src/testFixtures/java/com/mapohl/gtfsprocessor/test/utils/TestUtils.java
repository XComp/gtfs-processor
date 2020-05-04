package com.mapohl.gtfsprocessor.test.utils;

import com.mapohl.gtfsprocessor.genericproducer.utils.TimePeriod;
import com.mapohl.gtfsprocessor.test.domain.TestEntity;

import java.time.Instant;

public class TestUtils {

    public static final String TEST_TOPIC = "test-topic";

    public static final InstantBuilder INSTANT_BUILDER = new InstantBuilder();

    public static Instant createInstant(int hour) {
        return createInstant(hour, 0);
    }

    public static Instant createInstant(int hour, int minute) {
        return createInstant(hour, minute, 0);
    }

    public static Instant createInstant(int hour, int minute, int second) {
        return INSTANT_BUILDER.hour(hour).minute(minute).second(second).build();
    }

    public static String createInstantStr(int hour) {
        return createInstantStr(hour, 0);
    }

    public static String createInstantStr(int hour, int minute) {
        return createInstantStr(hour, minute, 0);
    }

    public static String createInstantStr(int hour, int minute, int second) {
        return INSTANT_BUILDER.hour(hour).minute(minute).second(second).toString();
    }

    public static TestEntity createEntity(int hour) {
        return createEntity(hour, 0);
    }

    public static TestEntity createEntity(int hour, int minute) {
        return createEntity(hour * 100 + minute, hour, minute);
    }

    public static TestEntity createEntity(int id, int hour, int minute) {
        return new TestEntity(id, createInstant(hour, minute), id);
    }

    public static TimePeriod createTimePeriod(int startHour, int startMinute, int endHour, int endMinute) {
        return new TimePeriod(createInstant(startHour, startMinute), createInstant(endHour, endMinute));
    }
}
