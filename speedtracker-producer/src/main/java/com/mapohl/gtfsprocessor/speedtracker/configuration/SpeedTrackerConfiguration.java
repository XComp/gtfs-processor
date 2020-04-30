package com.mapohl.gtfsprocessor.speedtracker.configuration;

import com.google.common.collect.Maps;
import com.mapohl.gtfsprocessor.genericproducer.CsvEntityProducer;
import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import com.mapohl.gtfsprocessor.speedtracker.domain.SpeedTracker;
import com.mapohl.gtfsprocessor.speedtracker.domain.SpeedTrackerMapper;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@Configuration
public class SpeedTrackerConfiguration {

    @Value(value = "${kafka.topic}")
    private String kafkaTopic;

    @Value(value = "${kafka.bootstrap-servers}")
    private String bootstrapServersStr;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = Maps.newHashMap();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServersStr);

        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic topic(
            @Value(value = "${kafka.partition-count}") int partitionCount,
            @Value(value = "${kafka.replication-factor}") short replicationFactor) {
        return new NewTopic(this.kafkaTopic(), partitionCount, replicationFactor);
    }

    @Bean
    public String kafkaTopic() {
        return this.kafkaTopic;
    }

    @Bean
    public ProducerFactory<Long, SpeedTracker> producerFactory() {
        Map<String, Object> configProps = Maps.newHashMap();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServersStr);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<Long, SpeedTracker> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public EntityMapper<String, SpeedTracker> entityMapper() {
        return new SpeedTrackerMapper();
    }

    @Bean
    public CsvEntityProducer<Long, SpeedTracker> producer(EntityMapper<String, SpeedTracker> entityMapper, KafkaTemplate<Long, SpeedTracker> kafkaTemplate) {
        return new CsvEntityProducer(entityMapper, this.kafkaTopic, kafkaTemplate);
    }
}
