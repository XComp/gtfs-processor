package com.mapohl.gtfsprocessor.taxiride;

import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;
import com.mapohl.gtfsprocessor.taxiride.configuration.TaxiRideStartConfiguration;
import com.mapohl.gtfsprocessor.taxiride.domain.taxiride.end.TaxiRideEnd;
import com.mapohl.gtfsprocessor.taxiride.domain.taxiride.intermediateprice.IntermediatePrice;
import com.mapohl.gtfsprocessor.taxiride.domain.taxiride.start.TaxiRideStart;
import com.mapohl.gtfsprocessor.test.utils.InstantBuilder;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.junit.jupiter.api.DisplayName;
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

import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.util.Collections.singleton;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        classes = TaxiRideStartConfiguration.class,
        args = {"--csv", "src/test/resources/taxirides.test.csv", "-s", "2009-01-01T00:00:00", "-r", "50", "-ru", "MILLIS"})
@DirtiesContext
@EmbeddedKafka
public class TaxiRideComponentTest {

    private static final InstantBuilder INSTANT_BUILDER = new InstantBuilder(2009, 1, 1, 0, 5, 0);

    @Value("${kafka.taxiridestart.topic}")
    private String startTopic;

    @Value("${kafka.intermediateprice.topic}")
    private String intermediateTopic;

    @Value("${kafka.taxirideend.topic}")
    private String endTopic;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private static void assertItermediatePriceRecordsOfRow(Iterator<ConsumerRecord<Integer, IntermediatePrice>> it, double priceIncrease, Instant... eventTimes) {
        double expectedPrice = 0.0;
        for (Instant eventTime : eventTimes) {
            expectedPrice += priceIncrease;

            assertTrue(it.hasNext());

            IntermediatePrice actual = it.next().value();
            assertEquals(expectedPrice, actual.getPrice());
            assertEquals(eventTime, actual.getEventTime());
        }
    }

    private <E extends Entity<Integer>> ConsumerRecords<Integer, E> initializeConsumer(Class<E> entityClass, String topic) {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("consumer", "false", this.embeddedKafkaBroker));
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Consumer<Integer, E> consumer = new DefaultKafkaConsumerFactory<>(configs, new IntegerDeserializer(), new JsonDeserializer<>(entityClass)).createConsumer();
        consumer.subscribe(singleton(topic));

        ConsumerRecords<Integer, E> records = KafkaTestUtils.getRecords(consumer, 3000);
        consumer.close();

        return records;
    }

    @DisplayName("Test TaxiRideStart emission")
    @Test
    public void testTaxiRideStartEmission() {
        ConsumerRecords<Integer, TaxiRideStart> actualConsumerRecords = initializeConsumer(TaxiRideStart.class, this.startTopic);
        assertEquals(3, actualConsumerRecords.count());

        Iterator<ConsumerRecord<Integer, TaxiRideStart>> actualIterator = actualConsumerRecords.records(this.startTopic).iterator();

        // first row
        assertTrue(actualIterator.hasNext());

        TaxiRideStart actual = actualIterator.next().value();
        assertEquals(INSTANT_BUILDER.minute(5).second(0).build(), actual.getEventTime());
        assertEquals(193, actual.getPickupZone().getZoneId());
        assertEquals(1, actual.getPassengerCount());

        // second row
        assertTrue(actualIterator.hasNext());

        actual = actualIterator.next().value();
        assertEquals(INSTANT_BUILDER.minute(5).second(10).build(), actual.getEventTime());
        assertEquals(261, actual.getPickupZone().getZoneId());
        assertEquals(1, actual.getPassengerCount());

        // third row
        assertTrue(actualIterator.hasNext());

        actual = actualIterator.next().value();
        assertEquals(INSTANT_BUILDER.minute(6).second(0).build(), actual.getEventTime());
        assertEquals(261, actual.getPickupZone().getZoneId());
        assertEquals(6, actual.getPassengerCount());

        assertFalse(actualIterator.hasNext());
    }

    @DisplayName("Test IntermediatePrice emission")
    @Test
    public void testIntermediatePriceEmission() {
        ConsumerRecords<Integer, IntermediatePrice> actualConsumerRecords = initializeConsumer(IntermediatePrice.class, this.intermediateTopic);
        assertEquals(12, actualConsumerRecords.count());

        Iterator<ConsumerRecord<Integer, IntermediatePrice>> actualIterator = actualConsumerRecords.records(this.intermediateTopic).iterator();

        // first row
        assertItermediatePriceRecordsOfRow(actualIterator, 2.0,
                INSTANT_BUILDER.minute(5).second(10).build(),
                INSTANT_BUILDER.minute(5).second(20).build(),
                INSTANT_BUILDER.minute(5).second(30).build(),
                INSTANT_BUILDER.minute(5).second(40).build(),
                INSTANT_BUILDER.minute(5).second(50).build());

        // second row
        assertItermediatePriceRecordsOfRow(actualIterator, 0.0,
                INSTANT_BUILDER.minute(5).second(20).build(),
                INSTANT_BUILDER.minute(5).second(30).build());

        // third row
        assertItermediatePriceRecordsOfRow(actualIterator, 10.0,
                INSTANT_BUILDER.minute(6).second(10).build(),
                INSTANT_BUILDER.minute(6).second(20).build(),
                INSTANT_BUILDER.minute(6).second(30).build(),
                INSTANT_BUILDER.minute(6).second(40).build(),
                INSTANT_BUILDER.minute(6).second(50).build());

        assertFalse(actualIterator.hasNext());
    }

    @DisplayName("Test TaxiRideEnd emission")
    @Test
    public void testTaxiRideEndEmission() {
        ConsumerRecords<Integer, TaxiRideEnd> actualConsumerRecords = initializeConsumer(TaxiRideEnd.class, this.endTopic);
        assertEquals(3, actualConsumerRecords.count());

        Iterator<ConsumerRecord<Integer, TaxiRideEnd>> actualIterator = actualConsumerRecords.records(this.endTopic).iterator();

        // second row
        assertTrue(actualIterator.hasNext());

        TaxiRideEnd actual = actualIterator.next().value();
        assertEquals(INSTANT_BUILDER.minute(5).second(40).build(), actual.getEventTime());
        assertEquals(193, actual.getDropOffZone().getZoneId());
        assertEquals(2.0, actual.getDistance());
        assertEquals(0.0, actual.getTollAmount());
        assertEquals(0.0, actual.getTotalAmount());

        // first row
        assertTrue(actualIterator.hasNext());

        actual = actualIterator.next().value();
        assertEquals(INSTANT_BUILDER.minute(6).second(0).build(), actual.getEventTime());
        assertEquals(193, actual.getDropOffZone().getZoneId());
        assertEquals(1.0, actual.getDistance());
        assertEquals(0.0, actual.getTollAmount());
        assertEquals(12.0, actual.getTotalAmount());

        // third row
        assertTrue(actualIterator.hasNext());

        actual = actualIterator.next().value();
        assertEquals(INSTANT_BUILDER.minute(7).second(0).build(), actual.getEventTime());
        assertEquals(193, actual.getDropOffZone().getZoneId());
        assertEquals(3.0, actual.getDistance());
        assertEquals(0.0, actual.getTollAmount());
        assertEquals(60.0, actual.getTotalAmount());

        assertFalse(actualIterator.hasNext());
    }
}