package com.mapohl.nyckafka.commonproducer;

import com.mapohl.nyckafka.common.TestEntity;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.IntegerDeserializer;
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
import java.util.Map;

import static com.mapohl.nyckafka.common.TestUtils.createEntity;
import static java.util.Collections.singleton;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        classes = TestEntityConfiguration.class,
        args = {"--csv", "src/test/resources/test.csv",
                "-s", "1970-01-01T00:45:00",
                "-t", "90",
                "-tu", "MINUTES",
                "-r", "50",
                "-ru", "MILLIS"})
@DirtiesContext
@EmbeddedKafka
public class CsvEntityProducerTest {

    @Value("${kafka.upstream.topic}")
    private String upstreamTopic;

    @Value("${kafka.downstream.topic}")
    private String downstreamTopic;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private ConsumerRecords<Integer, TestEntity> initializeConsumer(String topic) {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("consumer", "false", this.embeddedKafkaBroker));
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Consumer<Integer, TestEntity> consumer = new DefaultKafkaConsumerFactory<>(configs, new IntegerDeserializer(), new JsonDeserializer<>(TestEntity.class)).createConsumer();
        consumer.subscribe(singleton(topic));

        ConsumerRecords<Integer, TestEntity> records = KafkaTestUtils.getRecords(consumer, 3000);
        consumer.close();

        return records;
    }

    @Test
    public void testUpstreamEmission() {
        ConsumerRecords<Integer, TestEntity> actualConsumerRecords = initializeConsumer(this.upstreamTopic);
        assertEquals(7, actualConsumerRecords.count());

        Iterator<ConsumerRecord<Integer, TestEntity>> actualIterator = actualConsumerRecords.records(this.upstreamTopic).iterator();

        for (int hour = 1; hour <= 7; hour++) {
            assertTrue(actualIterator.hasNext());
            assertEquals(createEntity(hour), actualIterator.next().value());
        }

        assertFalse(actualIterator.hasNext());
    }

    @Test
    public void testDownstreamEmission() {
        ConsumerRecords<Integer, TestEntity> actualConsumerRecords = initializeConsumer(this.downstreamTopic);
        assertEquals(7, actualConsumerRecords.count());

        Iterator<ConsumerRecord<Integer, TestEntity>> actualIterator = actualConsumerRecords.records(this.downstreamTopic).iterator();

        for (int hour = 1; hour <= 7; hour++) {
            assertTrue(actualIterator.hasNext());
            assertEquals(createEntity(hour), actualIterator.next().value());
        }

        assertFalse(actualIterator.hasNext());
    }

}