package com.mapohl.gtfsprocessor.taxiride.configuration;

import com.google.common.collect.Maps;
import com.mapohl.gtfsprocessor.genericproducer.CsvEntityProducer;
import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import com.mapohl.gtfsprocessor.taxiride.domain.NYCTaxiZone;
import com.mapohl.gtfsprocessor.taxiride.domain.TaxiRideStart;
import com.mapohl.gtfsprocessor.taxiride.domain.TaxiRideStartMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@Configuration
@Import(BaseTaxiRideConfiguration.class)
public class TaxiRideStartConfiguration {

    @Value("${kafka.taxiridestart.topic}")
    private String kafkaTopic;

    @Bean
    public NewTopic topic(
            @Value("${kafka.taxiridestart.partition-count}") int partitionCount,
            @Value("${kafka.taxiridestart.replication-factor}") short replicationFactor) {
        return new NewTopic(this.kafkaTopic, partitionCount, replicationFactor);
    }

    @Bean
    public ProducerFactory<Integer, TaxiRideStart> producerFactory(
            @Value("${kafka.bootstrap-servers}") String bootstrapServersStr) {
        Map<String, Object> configProps = Maps.newHashMap();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServersStr);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<Integer, TaxiRideStart> kafkaTemplate(ProducerFactory<Integer, TaxiRideStart> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public EntityMapper<String, TaxiRideStart> entityMapper(Map<Integer, NYCTaxiZone> nycTaxiZoneIndex) {
        return new TaxiRideStartMapper(nycTaxiZoneIndex);
    }

    @Bean
    public CsvEntityProducer<Integer, TaxiRideStart> producer(EntityMapper<String, TaxiRideStart> entityMapper, KafkaTemplate<Integer, TaxiRideStart> kafkaTemplate) {
        return new CsvEntityProducer<>(entityMapper, this.kafkaTopic, kafkaTemplate);
    }
}
