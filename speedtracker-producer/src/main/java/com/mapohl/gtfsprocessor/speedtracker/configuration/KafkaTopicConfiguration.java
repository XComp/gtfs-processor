package com.mapohl.gtfsprocessor.speedtracker.configuration;

import com.google.common.collect.Maps;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.Map;

@Configuration
public class KafkaTopicConfiguration {

    @Bean
    public KafkaAdmin kafkaAdmin(
            @Value("${kafka.bootstrapAddress}") String bootstrapAddress
    ) {
        Map<String, Object> configs = Maps.newHashMap();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);

        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic topic(
            @Value(value = "${kafka.topic}") String topicName,
            @Value(value = "${kafka.partition-count}") int partitionCount,
            @Value(value = "${kafka.replication-factor}") short replicationFactor
    ) {
        return new NewTopic(topicName, partitionCount, replicationFactor);
    }
}
