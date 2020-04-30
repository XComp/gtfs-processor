package com.mapohl.gtfsprocessor.taxiride.configuration;

import com.google.common.collect.Maps;
import com.mapohl.gtfsprocessor.genericproducer.CsvEntityProducer;
import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import com.mapohl.gtfsprocessor.taxiride.domain.NYCTaxiZone;
import com.mapohl.gtfsprocessor.taxiride.domain.TaxiRide;
import com.mapohl.gtfsprocessor.taxiride.domain.TaxiRideMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
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
public class TaxiRideConfiguration {

    @Value("${kafka.taxiride.topic}")
    private String kafkaTopic;

    @Bean
    public NewTopic topic(
            @Value("${kafka.taxiride.partition-count}") int partitionCount,
            @Value("${kafka.taxiride.replication-factor}") short replicationFactor) {
        return new NewTopic(this.kafkaTopic(), partitionCount, replicationFactor);
    }

    @Bean
    public String kafkaTopic() {
        return this.kafkaTopic;
    }

    @Bean
    public ProducerFactory<Long, TaxiRide> producerFactory(
            @Value("${kafka.bootstrap-servers}") String bootstrapServersStr) {
        Map<String, Object> configProps = Maps.newHashMap();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServersStr);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<Long, TaxiRide> kafkaTemplate(ProducerFactory<Long, TaxiRide> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public EntityMapper<String, TaxiRide> entityMapper(Map<Integer, NYCTaxiZone> nycTaxiZoneIndex) {
        return new TaxiRideMapper(nycTaxiZoneIndex);
    }

    @Bean
    public CsvEntityProducer<Long, TaxiRide> producer(EntityMapper<String, TaxiRide> entityMapper, KafkaTemplate<Long, TaxiRide> kafkaTemplate) {
        return new CsvEntityProducer(entityMapper, this.kafkaTopic, kafkaTemplate);
    }
}
