package com.mapohl.gtfsprocessor.genericproducer.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TimePeriodTest {

    private static Instant INSTANT_0_00 = Instant.parse("2020-01-02T00:00:00Z");
    private static Instant INSTANT_0_30 = Instant.parse("2020-01-02T00:30:00Z");
    private static Instant INSTANT_1_00 = Instant.parse("2020-01-02T01:00:00Z");
    private static Instant INSTANT_1_30 = Instant.parse("2020-01-02T01:30:00Z");
    private static Instant INSTANT_2_00 = Instant.parse("2020-01-02T02:00:00Z");

    private TimePeriod testInstance;

    @BeforeEach
    public void setup() {
        testInstance = new TimePeriod(INSTANT_0_30, INSTANT_1_30);
    }

    @Test
    public void testIsBefore() {
        assertTrue(testInstance.timeIsBeforeTimePeriod(INSTANT_0_00));
        assertFalse(testInstance.timeIsBeforeTimePeriod(INSTANT_0_30));
        assertFalse(testInstance.timeIsBeforeTimePeriod(INSTANT_1_00));
        assertFalse(testInstance.timeIsBeforeTimePeriod(INSTANT_1_30));
        assertFalse(testInstance.timeIsBeforeTimePeriod(INSTANT_2_00));
    }

    @Test
    public void testIsAfter() {
        assertFalse(testInstance.timeIsAfterTimePeriod(INSTANT_0_00));
        assertFalse(testInstance.timeIsAfterTimePeriod(INSTANT_0_30));
        assertFalse(testInstance.timeIsAfterTimePeriod(INSTANT_1_00));
        assertTrue(testInstance.timeIsAfterTimePeriod(INSTANT_1_30));
        assertTrue(testInstance.timeIsAfterTimePeriod(INSTANT_2_00));
    }

    @Test
    public void testLength() {
        assertEquals(Duration.ofHours(1), new TimePeriod(INSTANT_0_00, INSTANT_1_00).getLength());
        assertEquals(Duration.ofMinutes(90), new TimePeriod(INSTANT_0_00, INSTANT_1_30).getLength());
        assertEquals(Duration.ofHours(2), new TimePeriod(INSTANT_0_30, Duration.ofHours(2)).getLength());
    }

    @Test
    public void testNext() {
        assertEquals(new TimePeriod(INSTANT_1_00, INSTANT_2_00), new TimePeriod(INSTANT_0_00, INSTANT_1_00).next());
    }

    @Test
    public void testIllegalArguments() {
        assertThrows(IllegalArgumentException.class, () -> new TimePeriod(INSTANT_0_00, INSTANT_0_00));
        assertThrows(IllegalArgumentException.class, () -> new TimePeriod(INSTANT_1_00, INSTANT_0_00));
    }

}