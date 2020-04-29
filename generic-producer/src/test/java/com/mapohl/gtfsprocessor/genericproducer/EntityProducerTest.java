package com.mapohl.gtfsprocessor.genericproducer;

import com.google.common.collect.Lists;
import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import com.mapohl.gtfsprocessor.genericproducer.domain.TestEntity;
import com.mapohl.gtfsprocessor.genericproducer.utils.InstantBuilder;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singleton;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {TestEntityConfiguration.class})
@DirtiesContext
@EmbeddedKafka
public class EntityProducerTest {

    private InstantBuilder instantBuilder = new InstantBuilder(2019, 1, 2, 1, 0, 0);

    @Value(value = "${kafka.topic}")
    private String testTopic;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private KafkaTemplate<Integer, TestEntity> kafkaTemplate;

    @Autowired
    private EntityMapper<String, TestEntity> entityMapper;

    private EntityProducer<Integer, TestEntity> testInstance;

    private Consumer<Integer, TestEntity> consumer;

    @BeforeEach
    public void setUp() {
        this.testInstance = new EntityProducer<>(this.kafkaTemplate, this.testTopic, this.entityMapper);

        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("consumer", "false", embeddedKafkaBroker));
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        this.consumer = new DefaultKafkaConsumerFactory<>(configs, new IntegerDeserializer(), new JsonDeserializer<>(TestEntity.class)).createConsumer();
        this.consumer.subscribe(singleton(this.testTopic));
    }

    @AfterEach
    public void tearDown() {
        this.consumer.close();
    }

    @Test
    public void testEmittingDataFromCsv() {
        List<TestEntity> expectedEntities = Lists.newArrayList(
                // new TestEntity(0, instantBuilder.hour(0).minute(0).buildInstant(), 0),
                // new TestEntity(1, instantBuilder.hour(0).minute(30).buildInstant(), 1),
                new TestEntity(2, instantBuilder.hour(1).minute(0).build(), 2),
                new TestEntity(3, instantBuilder.hour(1).minute(30).build(), 3),
                new TestEntity(4, instantBuilder.hour(2).minute(0).build(), 4),
                new TestEntity(5, instantBuilder.hour(2).minute(30).build(), 5),
                new TestEntity(6, instantBuilder.hour(3).minute(0).build(), 6),
                new TestEntity(7, instantBuilder.hour(3).minute(30).build(), 7)
        );

        String startTime = DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneOffset.UTC).format(instantBuilder.build());
        this.testInstance.run("--csv", "src/test/resources/test.csv",
                "--header-lines", "1",
                "--start-time", startTime,
                "--time-slot-length", "90",
                "--time-slot-time-unit", "MINUTES",
                "--real-time-slot-length", "50",
                "--real-time-slot-time-unit", "MILLIS");

        // check that the message was received
        ConsumerRecords<Integer, TestEntity> actualConsumerRecords = KafkaTestUtils.getRecords(this.consumer, 3000);
        assertEquals(expectedEntities.size(), actualConsumerRecords.count());

        Iterator<TestEntity> expectedEntitiesIterator = expectedEntities.iterator();
        for (ConsumerRecord<Integer, TestEntity> actualConsumerRecord : actualConsumerRecords) {
            assertTrue(expectedEntitiesIterator.hasNext());
            assertEquals(expectedEntitiesIterator.next(), actualConsumerRecord.value());
        }

        assertFalse(expectedEntitiesIterator.hasNext());
    }

}