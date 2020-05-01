package com.mapohl.gtfsprocessor.taxiride;

import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import com.mapohl.gtfsprocessor.taxiride.configuration.TaxiRideConfiguration;
import com.mapohl.gtfsprocessor.taxiride.domain.NYCTaxiZone;
import com.mapohl.gtfsprocessor.taxiride.domain.taxiride.TaxiRide;
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
import org.springframework.kafka.core.KafkaTemplate;
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

@SpringBootTest(classes = TaxiRideConfiguration.class, args = {"--csv", "src/test/resources/taxirides.test.csv", "-s", "2009-01-01T00:00:00", "-r", "50", "-ru", "MILLIS"})
@DirtiesContext
@EmbeddedKafka
class TaxiRideProducerTest {

    @Value(value = "${kafka.taxiride.topic}")
    private String testTopic;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private KafkaTemplate<Long, TaxiRide> kafkaTemplate;

    @Autowired
    private EntityMapper<String, TaxiRide> entityMapper;

    @Autowired
    private Map<Integer, NYCTaxiZone> nycTaxiZoneIndex;

    private Consumer<Long, TaxiRide> consumer;

    @BeforeEach
    public void setUp() {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("consumer", "false", embeddedKafkaBroker));
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        this.consumer = new DefaultKafkaConsumerFactory<>(configs, new LongDeserializer(), new JsonDeserializer<>(TaxiRide.class)).createConsumer();
        this.consumer.subscribe(singleton(this.testTopic));
    }

    @AfterEach
    public void tearDown() {
        this.consumer.close();
    }

    @Test
    public void testEmittingDataFromCsv() {
        List<TaxiRide> expectedEntities = Lists.newArrayList(
                TaxiRide.builder()
                        .pickupTimeStr("2009-01-01 00:05:07")
                        .dropOffTimeStr("2009-01-01 00:05:32")
                        .passengerCount(1)
                        .distance(0.0)
                        .pickupZone(this.nycTaxiZoneIndex.get(193))
                        .dropOffZone(this.nycTaxiZoneIndex.get(193))
                        .paymentTypeId(2)
                        .tollAmount(0.0)
                        .totalAmount(3.8)
                        .build(),
                TaxiRide.builder()
                        .pickupTimeStr("2009-01-01 00:05:12")
                        .dropOffTimeStr("2009-01-01 00:05:59")
                        .passengerCount(1)
                        .distance(0.0)
                        .pickupZone(this.nycTaxiZoneIndex.get(264))
                        .dropOffZone(this.nycTaxiZoneIndex.get(193))
                        .paymentTypeId(2)
                        .tollAmount(0.0)
                        .totalAmount(0.0)
                        .build(),
                TaxiRide.builder()
                        .pickupTimeStr("2009-01-01 00:06:01")
                        .dropOffTimeStr("2009-01-01 00:06:55")
                        .passengerCount(6)
                        .distance(0.0)
                        .pickupZone(this.nycTaxiZoneIndex.get(264))
                        .dropOffZone(this.nycTaxiZoneIndex.get(193))
                        .paymentTypeId(2)
                        .tollAmount(0.0)
                        .totalAmount(0.0)
                        .build()
        );

        // check that the message was received
        ConsumerRecords<Long, TaxiRide> actualConsumerRecords = KafkaTestUtils.getRecords(this.consumer, 3000);
        assertEquals(expectedEntities.size(), actualConsumerRecords.count());

        Iterator<TaxiRide> expectedEntitiesIterator = expectedEntities.iterator();
        for (ConsumerRecord<Long, TaxiRide> actualConsumerRecord : actualConsumerRecords) {
            assertTrue(expectedEntitiesIterator.hasNext());

            TaxiRide actual = actualConsumerRecord.value();
            TaxiRide expect = expectedEntitiesIterator.next();
            expect.setEntityId(actual.getEntityId());

            assertEquals(expect, actual);
        }

        assertFalse(expectedEntitiesIterator.hasNext());
    }

}