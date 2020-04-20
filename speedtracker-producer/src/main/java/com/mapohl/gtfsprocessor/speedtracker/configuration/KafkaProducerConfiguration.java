package com.mapohl.gtfsprocessor.speedtracker.configuration;

import com.google.common.collect.Maps;
import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import com.mapohl.gtfsprocessor.genericproducer.services.KafkaEmitService;
import com.mapohl.gtfsprocessor.speedtracker.domain.SpeedTracker;
import com.mapohl.gtfsprocessor.speedtracker.domain.SpeedTrackerMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

@Configuration
public class KafkaProducerConfiguration {

    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    @Bean
    public ProducerFactory<Long, SpeedTracker> producerFactory() {
        Map<String, Object> configProps = Maps.newHashMap();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<Long, SpeedTracker> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public KafkaEmitService<Long, SpeedTracker> kafkaEmitService(
            KafkaTemplate<Long, SpeedTracker> kafkaTemplate,
            NewTopic topic) {
        return new KafkaEmitService<>(kafkaTemplate, topic);
    }

    @Bean
    public EntityMapper<SpeedTracker> entityMapper() {
        return new SpeedTrackerMapper();
    }
}
