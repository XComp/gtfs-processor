package com.mapohl.nyckafka.taxiride.configuration;

import com.mapohl.nyckafka.commonproducer.CsvEntityProducer;
import com.mapohl.nyckafka.commonproducer.domain.EntityMapper;
import com.mapohl.nyckafka.taxiride.domain.NYCTaxiZone;
import com.mapohl.nyckafka.taxiride.domain.taxiride.TaxiRide;
import com.mapohl.nyckafka.taxiride.domain.taxiride.TaxiRideMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

@Configuration
@Import(BaseTaxiRideConfiguration.class)
public class TaxiRideConfiguration {

    @Value("${kafka.taxiride.topic}")
    private String kafkaTopic;

    @Bean
    public NewTopic taxiRideTopic(
            @Value("${kafka.taxiride.partition-count}") int partitionCount,
            @Value("${kafka.taxiride.replication-factor}") short replicationFactor) {
        return new NewTopic(this.kafkaTopic, partitionCount, replicationFactor);
    }

    @Bean
    public KafkaTemplate<Long, TaxiRide> taxiRideKafkaTemplate(ProducerFactory<Long, TaxiRide> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public EntityMapper<String, TaxiRide> taxiRideEntityMapper(Map<Integer, NYCTaxiZone> nycTaxiZoneIndex) {
        return new TaxiRideMapper(nycTaxiZoneIndex);
    }

    @Bean
    public CsvEntityProducer<Long, TaxiRide> taxiRideProducer(EntityMapper<String, TaxiRide> entityMapper, KafkaTemplate<Long, TaxiRide> kafkaTemplate) {
        return new CsvEntityProducer(entityMapper, this.kafkaTopic, kafkaTemplate);
    }
}
