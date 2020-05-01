package com.mapohl.gtfsprocessor.genericproducer.services;

import com.google.common.collect.Lists;
import com.mapohl.gtfsprocessor.genericproducer.domain.IdentityMapper;
import com.mapohl.gtfsprocessor.genericproducer.domain.TestEntity;
import com.mapohl.gtfsprocessor.genericproducer.services.sources.BasicEntityQueue;
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

import static org.mockito.Mockito.*;

class EntityEmissionSchedulerTest {

    private InstantBuilder instantBuilder = new InstantBuilder();

    private String initialTopic = "initial-topic";
    private KafkaTemplate<Integer, TestEntity> initialKafkaTemplate;
    private InitialEntityEmissionService initialEmissionService;

    private String downstreamTopic = "downstream-topic";
    private KafkaTemplate<Integer, TestEntity> downstreamKafkaTemplate;
    private DownstreamEntityEmissionService downstreamEmissionService;

    private List<TestEntity> testData;

    @BeforeEach
    public void initializeTest() {
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

        BasicEntityQueue<TestEntity, TestEntity> downstreamQueue = new BasicEntityQueue<>(new IdentityMapper<>());
        EntitySource<TestEntity> initialSource = new IteratorSource<>(testData.iterator(), new IdentityMapper<>(), downstreamQueue);

        initialKafkaTemplate = Mockito.mock(KafkaTemplate.class);
        downstreamKafkaTemplate = Mockito.mock(KafkaTemplate.class);

        initialEmissionService = new InitialEntityEmissionService<>(initialSource, initialTopic, initialKafkaTemplate);
        downstreamEmissionService = new DownstreamEntityEmissionService(downstreamQueue, downstreamTopic, downstreamKafkaTemplate);
    }

    @Test
    public void testEmissionWithoutLimit() throws Exception {
        EntityEmissionScheduler testInstance = new EntityEmissionScheduler(initialEmissionService, downstreamEmissionService);

        // no initial emissions are happening
        verifyNoInteractions(initialKafkaTemplate);
        verifyNoInteractions(downstreamKafkaTemplate);

        Instant start = instantBuilder.hour(0).minute(15).build();
        Instant end = instantBuilder.hour(1).minute(45).build();
        testInstance.emit(new TimePeriod(start, end), Duration.ofMillis(50));

        // the first entity was never emitted due to it being too old
        verify(initialKafkaTemplate, times(0)).send(initialTopic, testData.get(0));
        verify(downstreamKafkaTemplate, times(0)).send(downstreamTopic, testData.get(0));

        // check all other elements
        for (int i = 1; i < testData.size(); i++) {
            verify(initialKafkaTemplate, times(1)).send(initialTopic, testData.get(i));
            verify(downstreamKafkaTemplate, times(1)).send(downstreamTopic, testData.get(i));
        }

        // check whether the right amount of emissions happened
        verify(initialKafkaTemplate, times(testData.size() - 1)).send(eq(initialTopic), any());
        verify(downstreamKafkaTemplate, times(testData.size() - 1)).send(eq(downstreamTopic), any());
    }

    @Test
    public void testEmissionWithLimit() throws Exception {
        int entityLimit = 4;
        EntityEmissionScheduler testInstance = new EntityEmissionScheduler(initialEmissionService, entityLimit, downstreamEmissionService);

        // no initial emissions are happening
        verifyNoInteractions(initialKafkaTemplate);
        verifyNoInteractions(downstreamKafkaTemplate);

        Instant start = instantBuilder.hour(0).minute(15).build();
        Instant end = instantBuilder.hour(1).minute(45).build();
        testInstance.emit(new TimePeriod(start, end), Duration.ofMillis(50));

        // the first entity was never emitted due to it being too old
        verify(initialKafkaTemplate, times(0)).send(initialTopic, testData.get(0));
        verify(downstreamKafkaTemplate, times(0)).send(downstreamTopic, testData.get(0));

        // check all other elements
        for (int i = 1; i <= entityLimit; i++) {
            verify(initialKafkaTemplate, times(1)).send(initialTopic, testData.get(i));
            verify(downstreamKafkaTemplate, times(1)).send(downstreamTopic, testData.get(i));
        }

        // check whether the right amount of emissions happened
        verify(initialKafkaTemplate, times(entityLimit)).send(eq(initialTopic), any());
        verify(downstreamKafkaTemplate, times(entityLimit)).send(eq(downstreamTopic), any());
    }

}