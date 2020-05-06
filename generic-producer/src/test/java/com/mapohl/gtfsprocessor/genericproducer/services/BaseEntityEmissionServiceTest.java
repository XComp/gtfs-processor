package com.mapohl.gtfsprocessor.genericproducer.services;

import com.mapohl.gtfsprocessor.genericproducer.services.sources.IteratorSource;
import com.mapohl.gtfsprocessor.genericproducer.utils.TimePeriod;
import com.mapohl.gtfsprocessor.test.domain.TestEntity;
import com.mapohl.gtfsprocessor.test.domain.TestEntityMapper;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;

import static com.mapohl.gtfsprocessor.test.utils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BaseEntityEmissionServiceTest {

    private KafkaTemplate<Integer, TestEntity> kafkaTemplate;

    public BaseEntityEmissionService<Integer, TestEntity, IteratorSource<String, TestEntity>> initializeTestInstance(String... inputLines) {
        kafkaTemplate = Mockito.mock(KafkaTemplate.class);

        return new BaseEntityEmissionService(
                new IteratorSource<>(
                        Lists.newArrayList(inputLines).iterator(),
                        new TestEntityMapper()),
                TEST_TOPIC,
                kafkaTemplate);
    }

    @Test
    public void testNoResponse() {
        BaseEntityEmissionService<Integer, TestEntity, ?> testInstance = initializeTestInstance("1", "23", "45");

        verifyNoInteractions(kafkaTemplate);
        assertEquals(0, testInstance.emit(createTimePeriod(0, 0, 0, 20)));

        assertTrue(testInstance.hasNext());
        assertEquals(createInstant(1), testInstance.peekNextEventTime());
    }

    @Test
    public void testOneEmission() {
        BaseEntityEmissionService<Integer, TestEntity, ?> testInstance = initializeTestInstance("1", "23", "45");

        assertEquals(3, testInstance.emit(createTimePeriod(0, 30, 3, 30)));

        for (int i = 1; i <= 3; i++) {
            verify(kafkaTemplate, times(1)).send(TEST_TOPIC, createEntity(i));
        }
        verify(kafkaTemplate, times(3)).send(eq(TEST_TOPIC), any());

        assertTrue(testInstance.hasNext());
    }

    @Test
    public void testMultipleEmissions() {
        BaseEntityEmissionService<Integer, TestEntity, ?> testInstance = initializeTestInstance("1", "23", "45");

        TimePeriod initialTimePeriod = createTimePeriod(0, 30, 3, 30);
        assertEquals(3, testInstance.emit(initialTimePeriod));
        assertEquals(2, testInstance.emit(initialTimePeriod.next()));

        for (int i = 1; i <= 5; i++) {
            verify(kafkaTemplate, times(1)).send(TEST_TOPIC, createEntity(i));
        }
        verify(kafkaTemplate, times(5)).send(eq(TEST_TOPIC), any());

        assertFalse(testInstance.hasNext());
    }

    @Test
    public void testOrderOfMultiEmissions() {
        BaseEntityEmissionService<Integer, TestEntity, ?> testInstance = initializeTestInstance("1", "23", "12");

        TimePeriod initialTimePeriod = createTimePeriod(0, 30, 3, 30);
        assertEquals(5, testInstance.emit(initialTimePeriod));

        verify(kafkaTemplate, times(2)).send(TEST_TOPIC, createEntity(1));
        verify(kafkaTemplate, times(2)).send(TEST_TOPIC, createEntity(2));
        verify(kafkaTemplate, times(1)).send(TEST_TOPIC, createEntity(3));
        verify(kafkaTemplate, times(5)).send(eq(TEST_TOPIC), any());

        assertFalse(testInstance.hasNext());
    }


}