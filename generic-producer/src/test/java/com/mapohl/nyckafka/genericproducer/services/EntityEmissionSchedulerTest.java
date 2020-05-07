package com.mapohl.nyckafka.genericproducer.services;

import com.google.common.collect.Lists;
import com.mapohl.nyckafka.genericproducer.domain.EntityMapper;
import com.mapohl.nyckafka.genericproducer.services.sources.BasicEntityQueue;
import com.mapohl.nyckafka.genericproducer.services.sources.EntitySource;
import com.mapohl.nyckafka.genericproducer.services.sources.IteratorSource;
import com.mapohl.nyckafka.test.domain.TestEntity;
import com.mapohl.nyckafka.test.domain.TestEntityMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Duration;

import static com.mapohl.nyckafka.test.utils.TestUtils.*;
import static org.mockito.Mockito.*;

class EntityEmissionSchedulerTest {

    private static final String DOWNSTREAM_TEST_TOPIC = "downstream-" + TEST_TOPIC;

    private KafkaTemplate<Integer, TestEntity> initialKafkaTemplate;
    private InitialEntityEmissionService initialEmissionService;

    private KafkaTemplate<Integer, TestEntity> downstreamKafkaTemplate;
    private DownstreamEntityEmissionService downstreamEmissionService;

    public void initializeTest(String... inputLines) {
        EntityMapper<String, TestEntity> entityMapper = new TestEntityMapper();
        BasicEntityQueue<String, TestEntity> downstreamQueue = new BasicEntityQueue<>(entityMapper);
        EntitySource<TestEntity> initialSource = new IteratorSource<>(Lists.newArrayList(inputLines).iterator(), 1, entityMapper, downstreamQueue);

        initialKafkaTemplate = Mockito.mock(KafkaTemplate.class);
        downstreamKafkaTemplate = Mockito.mock(KafkaTemplate.class);

        initialEmissionService = new InitialEntityEmissionService<>(initialSource, TEST_TOPIC, initialKafkaTemplate);
        downstreamEmissionService = new DownstreamEntityEmissionService(downstreamQueue, DOWNSTREAM_TEST_TOPIC, downstreamKafkaTemplate);
    }

    @Test
    public void testEmissionWithoutLimit() throws Exception {
        initializeTest("0", "1", "23");
        EntityEmissionScheduler testInstance = new EntityEmissionScheduler(initialEmissionService, downstreamEmissionService);

        // no initial emissions are happening
        verifyNoInteractions(initialKafkaTemplate);
        verifyNoInteractions(downstreamKafkaTemplate);

        testInstance.emit(createTimePeriod(0, 30, 1, 30), Duration.ofMillis(50));

        // the first entity was never emitted due to it being too old
        verify(initialKafkaTemplate, times(0)).send(TEST_TOPIC, createEntity(0));
        verify(downstreamKafkaTemplate, times(0)).send(DOWNSTREAM_TEST_TOPIC, createEntity(0));

        // check all other elements
        verify(initialKafkaTemplate, times(1)).send(TEST_TOPIC, createEntity(1));
        verify(downstreamKafkaTemplate, times(1)).send(DOWNSTREAM_TEST_TOPIC, createEntity(1));

        verify(initialKafkaTemplate, times(1)).send(TEST_TOPIC, createEntity(2));
        verify(downstreamKafkaTemplate, times(1)).send(DOWNSTREAM_TEST_TOPIC, createEntity(2));

        verify(initialKafkaTemplate, times(1)).send(TEST_TOPIC, createEntity(3));
        verify(downstreamKafkaTemplate, times(1)).send(DOWNSTREAM_TEST_TOPIC, createEntity(3));

        // check whether the right total amount of emissions happened
        verify(initialKafkaTemplate, times(3)).send(eq(TEST_TOPIC), any());
        verify(downstreamKafkaTemplate, times(3)).send(eq(DOWNSTREAM_TEST_TOPIC), any());
    }

    @Test
    public void testEmissionWithLimit() throws Exception {
        initializeTest("0", "1", "23");
        EntityEmissionScheduler testInstance = new EntityEmissionScheduler(initialEmissionService, 2, downstreamEmissionService);

        // no initial emissions are happening
        verifyNoInteractions(initialKafkaTemplate);
        verifyNoInteractions(downstreamKafkaTemplate);

        testInstance.emit(createTimePeriod(0, 30, 1, 30), Duration.ofMillis(50));

        // the first entity was never emitted due to it being too old
        verify(initialKafkaTemplate, times(0)).send(TEST_TOPIC, createEntity(0));
        verify(downstreamKafkaTemplate, times(0)).send(DOWNSTREAM_TEST_TOPIC, createEntity(0));

        // check all other elements
        verify(initialKafkaTemplate, times(1)).send(TEST_TOPIC, createEntity(1));
        verify(downstreamKafkaTemplate, times(1)).send(DOWNSTREAM_TEST_TOPIC, createEntity(1));

        verify(initialKafkaTemplate, times(1)).send(TEST_TOPIC, createEntity(2));
        verify(downstreamKafkaTemplate, times(1)).send(DOWNSTREAM_TEST_TOPIC, createEntity(2));

        // the 4th entity was never emitted due to the entity limit
        verify(initialKafkaTemplate, times(0)).send(TEST_TOPIC, createEntity(3));
        verify(downstreamKafkaTemplate, times(0)).send(DOWNSTREAM_TEST_TOPIC, createEntity(3));

        // check whether the right amount of emissions happened
        verify(initialKafkaTemplate, times(2)).send(eq(TEST_TOPIC), any());
        verify(downstreamKafkaTemplate, times(2)).send(eq(DOWNSTREAM_TEST_TOPIC), any());
    }

    @Test
    public void testLatePropagation() throws Exception {
        initializeTest("1234", "123", "234");
        EntityEmissionScheduler testInstance = new EntityEmissionScheduler(initialEmissionService, downstreamEmissionService);

        // no initial emissions are happening
        verifyNoInteractions(initialKafkaTemplate);
        verifyNoInteractions(downstreamKafkaTemplate);

        testInstance.emit(createTimePeriod(0, 30, 1, 30), Duration.ofMillis(50));

        // check all other elements
        verify(initialKafkaTemplate, times(2)).send(TEST_TOPIC, createEntity(1));
        verify(downstreamKafkaTemplate, times(2)).send(DOWNSTREAM_TEST_TOPIC, createEntity(1));

        verify(initialKafkaTemplate, times(3)).send(TEST_TOPIC, createEntity(2));
        verify(downstreamKafkaTemplate, times(3)).send(DOWNSTREAM_TEST_TOPIC, createEntity(2));

        verify(initialKafkaTemplate, times(3)).send(TEST_TOPIC, createEntity(3));
        verify(downstreamKafkaTemplate, times(3)).send(DOWNSTREAM_TEST_TOPIC, createEntity(3));

        verify(initialKafkaTemplate, times(2)).send(TEST_TOPIC, createEntity(4));
        verify(downstreamKafkaTemplate, times(2)).send(DOWNSTREAM_TEST_TOPIC, createEntity(4));

        // check whether the right amount of emissions happened
        verify(initialKafkaTemplate, times(10)).send(eq(TEST_TOPIC), any());
        verify(downstreamKafkaTemplate, times(10)).send(eq(DOWNSTREAM_TEST_TOPIC), any());
    }

}