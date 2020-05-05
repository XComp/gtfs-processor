package com.mapohl.gtfsprocessor.genericproducer;

import com.google.common.collect.Lists;
import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import com.mapohl.gtfsprocessor.test.domain.TestEntity;
import com.mapohl.gtfsprocessor.test.utils.InstantBuilder;
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

import static com.mapohl.gtfsprocessor.test.utils.TestUtils.createEntity;
import static java.util.Collections.singleton;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {TestEntityConfiguration.class})
@DirtiesContext
@EmbeddedKafka
public class CsvEntityProducerTest {

    @Value(value = "${kafka.topic}")
    private String testTopic;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private KafkaTemplate<Integer, TestEntity> kafkaTemplate;

    @Autowired
    private EntityMapper<String, TestEntity> entityMapper;

    private CsvEntityProducer<Integer, TestEntity> testInstance;

    private Consumer<Integer, TestEntity> consumer;

    @BeforeEach
    public void setUp() {
        this.testInstance = new CsvEntityProducer<>(this.entityMapper, this.testTopic, this.kafkaTemplate);

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
                // createEntity(0),
                // createEntity(1),
                createEntity(2),
                createEntity(3),
                createEntity(4),
                createEntity(5),
                createEntity(6),
                createEntity(7)
        );

        String startTime = new InstantBuilder(DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneOffset.UTC)).hour(2).toString();
        this.testInstance.run("--csv", "src/test/resources/test.csv",
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