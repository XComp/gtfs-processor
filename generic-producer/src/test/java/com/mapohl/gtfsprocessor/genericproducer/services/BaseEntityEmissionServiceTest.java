package com.mapohl.gtfsprocessor.genericproducer.services;

import com.google.common.base.Preconditions;
import com.mapohl.gtfsprocessor.genericproducer.domain.IdentityMapper;
import com.mapohl.gtfsprocessor.genericproducer.domain.TestEntity;
import com.mapohl.gtfsprocessor.genericproducer.services.sources.EntitySource;
import com.mapohl.gtfsprocessor.genericproducer.services.sources.IteratorSource;
import com.mapohl.gtfsprocessor.genericproducer.utils.InstantBuilder;
import com.mapohl.gtfsprocessor.genericproducer.utils.TimePeriod;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BaseEntityEmissionServiceTest {

    private static InstantBuilder INSTANT_BUILDER = new InstantBuilder();

    private String topic = "test-topic";
    private KafkaTemplate<Integer, TestEntity> kafkaTemplate;
    private List<TestEntity> data;

    private BaseEntityEmissionService<Integer, TestEntity, IteratorSource<TestEntity, TestEntity>> testInstance;

    private static TestEntity createEntity(int id, int hour, int minute) {
        Preconditions.checkArgument(hour >= 0 && hour < 24);
        Preconditions.checkArgument(minute >= 0 && minute < 60);

        return new TestEntity(id, INSTANT_BUILDER.hour(hour).minute(minute).build(), hour);
    }

    private static TimePeriod createTimePeriod(int startHour, int startMinute, int endHour, int endMinute) {
        return new TimePeriod(
                INSTANT_BUILDER.hour(startHour).minute(startMinute).build(),
                INSTANT_BUILDER.hour(endHour).minute(endMinute).build());
    }

    @BeforeEach
    public void initializeTest() {
        kafkaTemplate = Mockito.mock(KafkaTemplate.class);

        data = Lists.newArrayList(
                createEntity(0, 0, 30),
                createEntity(1, 0, 30),
                createEntity(2, 1, 0),
                createEntity(3, 1, 30),
                createEntity(4, 2, 0)
        );

        EntitySource<TestEntity> source = new IteratorSource<>(data.iterator(), new IdentityMapper<>());
        testInstance = new BaseEntityEmissionService(source, topic, kafkaTemplate);
    }

    @Test
    public void testNoResponse() {
        verifyNoInteractions(kafkaTemplate);
        assertEquals(0, testInstance.emit(createTimePeriod(0, 0, 0, 20)));

        assertTrue(testInstance.hasNext());
        assertEquals(INSTANT_BUILDER.hour(0).minute(30).build(), testInstance.peekNextEventTime());
    }

    @Test
    public void testOneEmission() {
        assertEquals(4, testInstance.emit(createTimePeriod(0, 15, 1, 45)));

        for (int i = 0; i < 4; i++) {
            verify(kafkaTemplate, times(1)).send(topic, data.get(i));
        }
        verify(kafkaTemplate, times(4)).send(eq(topic), any());

        assertTrue(testInstance.hasNext());
    }

    @Test
    public void testMultipleEmissions() {
        TimePeriod initialTimePeriod = createTimePeriod(0, 15, 1, 45);
        assertEquals(4, testInstance.emit(initialTimePeriod));
        assertEquals(1, testInstance.emit(initialTimePeriod.next()));

        for (int i = 0; i < 5; i++) {
            verify(kafkaTemplate, times(1)).send(topic, data.get(i));
        }
        verify(kafkaTemplate, times(5)).send(eq(topic), any());

        assertFalse(testInstance.hasNext());
    }

}