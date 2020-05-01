package com.mapohl.gtfsprocessor.genericproducer.services;

import com.google.common.collect.Lists;
import com.mapohl.gtfsprocessor.genericproducer.domain.IdentityMapper;
import com.mapohl.gtfsprocessor.genericproducer.domain.TestEntity;
import com.mapohl.gtfsprocessor.genericproducer.services.sources.BasicEntityQueue;
import com.mapohl.gtfsprocessor.genericproducer.services.sources.EntitySource;
import com.mapohl.gtfsprocessor.genericproducer.services.sources.IteratorSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Duration;
import java.util.List;

import static com.mapohl.gtfsprocessor.genericproducer.testutils.TestUtils.*;
import static org.mockito.Mockito.*;

class EntityEmissionSchedulerTest {

    private static final String DOWNSTREAM_TEST_TOPIC = "downstream-" + TEST_TOPIC;

    private KafkaTemplate<Integer, TestEntity> initialKafkaTemplate;
    private InitialEntityEmissionService initialEmissionService;

    private KafkaTemplate<Integer, TestEntity> downstreamKafkaTemplate;
    private DownstreamEntityEmissionService downstreamEmissionService;

    private List<TestEntity> testData;

    @BeforeEach
    public void initializeTest() {
        testData = Lists.newArrayList(
                createEntity(0, 0, 0),

                createEntity(1, 0, 30),
                createEntity(2, 1, 0),
                createEntity(3, 1, 30),

                createEntity(4, 2, 0),
                createEntity(5, 2, 30),
                createEntity(6, 3, 0),

                createEntity(7, 3, 30)
        );

        BasicEntityQueue<TestEntity, TestEntity> downstreamQueue = new BasicEntityQueue<>(new IdentityMapper<>());
        EntitySource<TestEntity> initialSource = new IteratorSource<>(testData.iterator(), new IdentityMapper<>(), downstreamQueue);

        initialKafkaTemplate = Mockito.mock(KafkaTemplate.class);
        downstreamKafkaTemplate = Mockito.mock(KafkaTemplate.class);

        initialEmissionService = new InitialEntityEmissionService<>(initialSource, TEST_TOPIC, initialKafkaTemplate);
        downstreamEmissionService = new DownstreamEntityEmissionService(downstreamQueue, DOWNSTREAM_TEST_TOPIC, downstreamKafkaTemplate);
    }

    @Test
    public void testEmissionWithoutLimit() throws Exception {
        EntityEmissionScheduler testInstance = new EntityEmissionScheduler(initialEmissionService, downstreamEmissionService);

        // no initial emissions are happening
        verifyNoInteractions(initialKafkaTemplate);
        verifyNoInteractions(downstreamKafkaTemplate);

        testInstance.emit(createTimePeriod(0, 15, 1, 45), Duration.ofMillis(50));

        // the first entity was never emitted due to it being too old
        verify(initialKafkaTemplate, times(0)).send(TEST_TOPIC, testData.get(0));
        verify(downstreamKafkaTemplate, times(0)).send(DOWNSTREAM_TEST_TOPIC, testData.get(0));

        // check all other elements
        for (int i = 1; i < testData.size(); i++) {
            verify(initialKafkaTemplate, times(1)).send(TEST_TOPIC, testData.get(i));
            verify(downstreamKafkaTemplate, times(1)).send(DOWNSTREAM_TEST_TOPIC, testData.get(i));
        }

        // check whether the right amount of emissions happened
        verify(initialKafkaTemplate, times(testData.size() - 1)).send(eq(TEST_TOPIC), any());
        verify(downstreamKafkaTemplate, times(testData.size() - 1)).send(eq(DOWNSTREAM_TEST_TOPIC), any());
    }

    @Test
    public void testEmissionWithLimit() throws Exception {
        int entityLimit = 4;
        EntityEmissionScheduler testInstance = new EntityEmissionScheduler(initialEmissionService, entityLimit, downstreamEmissionService);

        // no initial emissions are happening
        verifyNoInteractions(initialKafkaTemplate);
        verifyNoInteractions(downstreamKafkaTemplate);

        testInstance.emit(createTimePeriod(0, 15, 1, 45), Duration.ofMillis(50));

        // the first entity was never emitted due to it being too old
        verify(initialKafkaTemplate, times(0)).send(TEST_TOPIC, testData.get(0));
        verify(downstreamKafkaTemplate, times(0)).send(DOWNSTREAM_TEST_TOPIC, testData.get(0));

        // check all other elements
        for (int i = 1; i <= entityLimit; i++) {
            verify(initialKafkaTemplate, times(1)).send(TEST_TOPIC, testData.get(i));
            verify(downstreamKafkaTemplate, times(1)).send(DOWNSTREAM_TEST_TOPIC, testData.get(i));
        }

        // check whether the right amount of emissions happened
        verify(initialKafkaTemplate, times(entityLimit)).send(eq(TEST_TOPIC), any());
        verify(downstreamKafkaTemplate, times(entityLimit)).send(eq(DOWNSTREAM_TEST_TOPIC), any());
    }

}