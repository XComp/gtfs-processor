package com.mapohl.gtfsprocessor.speedtracker;

import com.mapohl.gtfsprocessor.genericproducer.EntityProducer;
import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import com.mapohl.gtfsprocessor.genericproducer.services.KafkaEmitService;
import com.mapohl.gtfsprocessor.speedtracker.configuration.SpeedTrackerConfiguration;
import com.mapohl.gtfsprocessor.speedtracker.domain.LinkPoint;
import com.mapohl.gtfsprocessor.speedtracker.domain.SpeedTracker;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singleton;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {SpeedTrackerConfiguration.class})
@DirtiesContext
@EmbeddedKafka
public class SpeedTrackerProducerTest {

    @Value(value = "${kafka.topic}")
    private String testTopic;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private KafkaEmitService<Long, SpeedTracker> kafkaEmitService;

    @Autowired
    private EntityMapper<SpeedTracker> entityMapper;

    private EntityProducer<Long, SpeedTracker> testInstance;

    private Consumer<Long, SpeedTracker> consumer;

    @BeforeEach
    public void setUp() {
        this.testInstance = new EntityProducer<>(this.entityMapper, this.kafkaEmitService);

        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("consumer", "false", embeddedKafkaBroker));
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        this.consumer = new DefaultKafkaConsumerFactory<>(configs, new LongDeserializer(), new JsonDeserializer<>(SpeedTracker.class)).createConsumer();
        this.consumer.subscribe(singleton(this.testTopic));
    }

    @AfterEach
    public void tearDown() {
        this.consumer.close();
    }

    @Test
    public void testEmittingDataFromCsv() {
        List<SpeedTracker> expectedEntities = Lists.newArrayList(
                SpeedTracker.builder()
                        .speed(0.0)
                        .travelTimeInSeconds(0)
                        .creationTimeStr("1978-01-01 00:00:00")
                        .linkId(4616279)
                        .linkPoints(Lists.newArrayList(
                                new LinkPoint(40.63511, -74.19581),
                                new LinkPoint(40.63381, -74.19336)))
                        .description("278 E BRUNSWICK AVENUE - SIE E SOUTH AVENUE")
                        .borough("Staten Island")
                        .build(),
                SpeedTracker.builder()
                        .speed(0.0)
                        .travelTimeInSeconds(0)
                        .creationTimeStr("1978-01-01 00:00:00")
                        .linkId(4616280)
                        .linkPoints(Lists.newArrayList(
                                new LinkPoint(40.62017,-74.17683),
                                new LinkPoint(40.61893,-74.17729)))
                        .description("278 E BRUNSWICK AVENUE - WSE S SOUTH AVENUE")
                        .borough("Staten Island")
                        .build(),
                SpeedTracker.builder()
                        .speed(0.0)
                        .travelTimeInSeconds(0)
                        .creationTimeStr("1978-01-01 00:00:00")
                        .linkId(4616344)
                        .linkPoints(Lists.newArrayList(
                                new LinkPoint(40.7136405,-74.013781),
                                new LinkPoint(40.713981,-74.013551)))
                        .description("BBT Manhattan Portal inbound - West St N Watts St")
                        .borough("Manhattan")
                        .build()
        );

        this.testInstance.run("--csv", "src/test/resources/speedtrackers.test.csv", "-h", "0");

        // check that the message was received
        ConsumerRecords<Long, SpeedTracker> actualConsumerRecords = KafkaTestUtils.getRecords(this.consumer, 3000);
        assertEquals(expectedEntities.size(), actualConsumerRecords.count());

        Iterator<SpeedTracker> expectedEntitiesIterator = expectedEntities.iterator();
        for (ConsumerRecord<Long, SpeedTracker> actualConsumerRecord : actualConsumerRecords) {
            assertTrue(expectedEntitiesIterator.hasNext());
            assertEquals(expectedEntitiesIterator.next(), actualConsumerRecord.value());
        }

        assertFalse(expectedEntitiesIterator.hasNext());
    }

}
