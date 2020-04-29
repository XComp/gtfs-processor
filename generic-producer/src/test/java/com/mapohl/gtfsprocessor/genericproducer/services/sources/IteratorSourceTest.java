package com.mapohl.gtfsprocessor.genericproducer.services.sources;

import com.google.common.collect.Lists;
import com.mapohl.gtfsprocessor.genericproducer.domain.IdentityMapper;
import com.mapohl.gtfsprocessor.genericproducer.domain.TestEntity;
import com.mapohl.gtfsprocessor.genericproducer.utils.InstantBuilder;
import com.mapohl.gtfsprocessor.genericproducer.utils.TimePeriod;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class IteratorSourceTest {

    private static InstantBuilder instantBuilder = new InstantBuilder(2019, 1, 2, 1, 0, 0);

    private static String topic = "test-topic";

    private static TimePeriod initialTimePeriod;
    private static List<TestEntity> testData;

    private IteratorSource<TestEntity, TestEntity> testInstance;

    @BeforeAll
    public static void setupData() {
        Instant start = instantBuilder.hour(0).minute(15).build();
        Instant end = instantBuilder.hour(1).minute(45).build();
        initialTimePeriod = new TimePeriod(start, end);

        testData = Lists.newArrayList(
                new TestEntity(0, instantBuilder.hour(0).minute(0).build(), 0),
                new TestEntity(1, instantBuilder.hour(0).minute(30).build(), 1),
                new TestEntity(2, instantBuilder.hour(1).minute(0).build(), 2),
                new TestEntity(3, instantBuilder.hour(1).minute(30).build(), 3),
                new TestEntity(4, instantBuilder.hour(2).minute(0).build(), 4),
                new TestEntity(5, instantBuilder.hour(2).minute(30).build(), 5)
        );

    }

    @BeforeEach
    public void setup() {
        this.testInstance = new IteratorSource(
                topic, testData.iterator(), new IdentityMapper(), 4);
    }

    @Test
    public void testTopic() {
        assertEquals(topic, testInstance.getTopic());
    }

    @Test
    public void testInvalidTimePeriod() {
        assertThrows(NullPointerException.class, () -> this.testInstance.take(null));
    }

    @Test
    public void testNoSuchElementException() {
        TimePeriod afterDataTimePeriod = initialTimePeriod.next().next().next();
        assertThrows(NoSuchElementException.class, () -> testInstance.take(afterDataTimePeriod));
    }

    @Test
    public void testIteration() {
        TimePeriod timePeriod = this.initialTimePeriod;

        assertTrue(testInstance.hasNext());
        assertEquals(testData.get(1), testInstance.take(timePeriod));

        assertTrue(testInstance.hasNext());
        assertEquals(testData.get(2), testInstance.take(timePeriod));

        assertTrue(testInstance.hasNext());
        assertEquals(testData.get(3), testInstance.take(timePeriod));

        assertTrue(testInstance.hasNext());
        assertNull(testInstance.take(timePeriod));

        timePeriod = timePeriod.next();

        assertTrue(testInstance.hasNext());
        assertEquals(testData.get(4), testInstance.take(timePeriod));

        assertFalse(testInstance.hasNext());
        try {
            testInstance.take(timePeriod);
            fail();
        } catch (NoSuchElementException e) {
            // expected failure
        }
    }

    @Test
    public void testDownstreamPropagation() {
        BasicEntityQueue<TestEntity, TestEntity> downstreamEntityQueue = Mockito.mock(BasicEntityQueue.class);
        testInstance = new IteratorSource(topic, testData.iterator(), new IdentityMapper(), 4, downstreamEntityQueue);

        verify(downstreamEntityQueue, times(0)).add(testData.get(0));
        verify(downstreamEntityQueue, times(0)).add(testData.get(1));
        verify(downstreamEntityQueue, times(0)).add(testData.get(2));
        verify(downstreamEntityQueue, times(0)).add(testData.get(3));
        verify(downstreamEntityQueue, times(0)).add(testData.get(4));
        verify(downstreamEntityQueue, times(0)).add(any());

        verify(downstreamEntityQueue, times(0)).endOfData();

        assertEquals(testData.get(1), testInstance.take(initialTimePeriod));
        verify(downstreamEntityQueue, times(1)).add(testData.get(1));
        verify(downstreamEntityQueue, times(1)).add(any());

        assertEquals(testData.get(2), testInstance.take(initialTimePeriod));
        verify(downstreamEntityQueue, times(1)).add(testData.get(2));
        verify(downstreamEntityQueue, times(2)).add(any());

        assertEquals(testData.get(3), testInstance.take(initialTimePeriod));
        verify(downstreamEntityQueue, times(1)).add(testData.get(3));
        verify(downstreamEntityQueue, times(3)).add(any());

        assertNull(testInstance.take(initialTimePeriod));
        verify(downstreamEntityQueue, times(3)).add(any());
        verify(downstreamEntityQueue, times(0)).endOfData();

        assertEquals(testData.get(4), testInstance.take(initialTimePeriod.next()));
        verify(downstreamEntityQueue, times(1)).add(testData.get(4));
        verify(downstreamEntityQueue, times(4)).add(any());
        verify(downstreamEntityQueue, times(1)).endOfData();
    }

}