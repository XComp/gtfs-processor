package com.mapohl.gtfsprocessor.taxiride.configuration;

import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import com.mapohl.gtfsprocessor.genericproducer.services.DownstreamEntityEmissionService;
import com.mapohl.gtfsprocessor.genericproducer.services.sources.BasicEntityQueue;
import com.mapohl.gtfsprocessor.taxiride.domain.NYCTaxiZone;
import com.mapohl.gtfsprocessor.taxiride.domain.taxiride.end.TaxiRideEnd;
import com.mapohl.gtfsprocessor.taxiride.domain.taxiride.end.TaxiRideEndMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

@Configuration
public class TaxiRideEndConfiguration {

    @Value("${kafka.taxirideend.topic}")
    private String kafkaTopic;

    @Bean
    public NewTopic taxiRideEndTopic(
            @Value("${kafka.taxirideend.partition-count}") int partitionCount,
            @Value("${kafka.taxirideend.replication-factor}") short replicationFactor) {
        return new NewTopic(this.kafkaTopic, partitionCount, replicationFactor);
    }

    @Bean
    public KafkaTemplate<Integer, TaxiRideEnd> taxiRideEndKafkaTemplate(ProducerFactory<Integer, TaxiRideEnd> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public EntityMapper<String, TaxiRideEnd> taxiRideEndEntityMapper(Map<Integer, NYCTaxiZone> nycTaxiZoneIndex) {
        return new TaxiRideEndMapper(nycTaxiZoneIndex);
    }

    @Bean
    public DownstreamEntityEmissionService taxiRideEndEntityEmissionService(EntityMapper<String, TaxiRideEnd> taxiRideEndEntityMapper,
                                                                            KafkaTemplate<Integer, TaxiRideEnd> taxiRideEndKafkaTemplate) {
        return new DownstreamEntityEmissionService(new BasicEntityQueue<>(taxiRideEndEntityMapper), this.kafkaTopic, taxiRideEndKafkaTemplate);
    }
}
