package com.mapohl.gtfsprocessor.genericproducer.services;

import com.google.common.collect.Lists;
import com.mapohl.gtfsprocessor.genericproducer.domain.IdentityMapper;
import com.mapohl.gtfsprocessor.genericproducer.domain.TestEntity;
import com.mapohl.gtfsprocessor.genericproducer.services.sources.EntitySource;
import com.mapohl.gtfsprocessor.genericproducer.services.sources.IteratorSource;
import com.mapohl.gtfsprocessor.genericproducer.utils.InstantBuilder;
import com.mapohl.gtfsprocessor.genericproducer.utils.TimePeriod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class EntityEmissionSchedulerTest {

    private InstantBuilder instantBuilder = new InstantBuilder(2019, 1, 2, 1, 0, 0);

    private String topic = "test-topic";
    private KafkaTemplate<Integer, TestEntity> kafkaTemplate;
    private List<TestEntity> testData;
    private EntitySource<TestEntity> entitySource;

    @BeforeEach
    public void mockKafkaTemplate() {
        kafkaTemplate = Mockito.mock(KafkaTemplate.class);
    }

    @BeforeEach
    public void initializeTestData() {
        testData = Lists.newArrayList(
                new TestEntity(0, instantBuilder.hour(0).minute(0).build(), 0),
                new TestEntity(1, instantBuilder.hour(0).minute(30).build(), 1),
                new TestEntity(2, instantBuilder.hour(1).minute(0).build(), 2),
                new TestEntity(3, instantBuilder.hour(1).minute(30).build(), 3),
                new TestEntity(4, instantBuilder.hour(2).minute(0).build(), 4),
                new TestEntity(5, instantBuilder.hour(2).minute(30).build(), 5),
                new TestEntity(6, instantBuilder.hour(3).minute(0).build(), 6),
                new TestEntity(7, instantBuilder.hour(3).minute(30).build(), 7)
        );

        entitySource = new IteratorSource<>(topic, testData.iterator(), new IdentityMapper<>());
    }

    @Test
    public void testEmission() throws Exception {
        EntityEmissionScheduler<Integer, TestEntity> testInstance = new EntityEmissionScheduler(kafkaTemplate, entitySource);

        Instant start = instantBuilder.hour(0).minute(15).build();
        Instant end = instantBuilder.hour(1).minute(45).build();
        testInstance.emit(new TimePeriod(start, end), Duration.ofMillis(50));

        verify(kafkaTemplate, times(0)).send(topic, testData.get(0));
        for (int i = 1; i < testData.size(); i++) {
            verify(kafkaTemplate, times(1)).send(topic, testData.get(i));
        }
    }

}