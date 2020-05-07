package com.mapohl.nyckafka.taxiride.configuration;

import com.mapohl.nyckafka.commonproducer.CsvEntityProducer;
import com.mapohl.nyckafka.commonproducer.domain.EntityMapper;
import com.mapohl.nyckafka.commonproducer.services.DownstreamEntityEmissionService;
import com.mapohl.nyckafka.taxiride.domain.NYCTaxiZone;
import com.mapohl.nyckafka.taxiride.domain.taxiride.start.TaxiRideStart;
import com.mapohl.nyckafka.taxiride.domain.taxiride.start.TaxiRideStartMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

@Configuration
@Import({BaseTaxiRideConfiguration.class, IntermediatePriceConfiguration.class, TaxiRideEndConfiguration.class})
public class TaxiRideStartConfiguration {

    @Value("${kafka.taxiridestart.topic}")
    private String kafkaTopic;

    @Bean
    public NewTopic taxiRideStartTopic(
            @Value("${kafka.taxiridestart.partition-count}") int partitionCount,
            @Value("${kafka.taxiridestart.replication-factor}") short replicationFactor) {
        return new NewTopic(this.kafkaTopic, partitionCount, replicationFactor);
    }

    @Bean
    public KafkaTemplate<Integer, TaxiRideStart> taxiRideStartKafkaTemplate(ProducerFactory<Integer, TaxiRideStart> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public EntityMapper<String, TaxiRideStart> taxiRideStartEntityMapper(Map<Integer, NYCTaxiZone> nycTaxiZoneIndex) {
        return new TaxiRideStartMapper(nycTaxiZoneIndex);
    }

    @Bean
    public CsvEntityProducer<Integer, TaxiRideStart> taxiRideStartProducer(EntityMapper<String, TaxiRideStart> taxiRideStartEntityMapper,
                                                                           KafkaTemplate<Integer, TaxiRideStart> taxiRideStartKafkaTemplate,
                                                                           DownstreamEntityEmissionService<String, ?, ?>... downstreamEntityEmissionServices) {
        return new CsvEntityProducer<>(taxiRideStartEntityMapper, this.kafkaTopic, taxiRideStartKafkaTemplate, downstreamEntityEmissionServices);
    }
}