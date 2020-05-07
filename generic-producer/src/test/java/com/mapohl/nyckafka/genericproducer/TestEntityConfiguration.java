package com.mapohl.nyckafka.genericproducer;

import com.google.common.collect.Maps;
import com.mapohl.nyckafka.genericproducer.domain.EntityMapper;
import com.mapohl.nyckafka.genericproducer.services.DownstreamEntityEmissionService;
import com.mapohl.nyckafka.genericproducer.services.sources.BasicEntityQueue;
import com.mapohl.nyckafka.test.domain.TestEntity;
import com.mapohl.nyckafka.test.domain.TestEntityMapper;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
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
public class TestEntityConfiguration {

    @Value(value = "${kafka.bootstrap-servers}")
    private String bootstrapServersStr;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = Maps.newHashMap();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServersStr);

        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic upstreamTopic(
            @Value(value = "${kafka.upstream.topic}") String upstreamKafkaTopic,
            @Value(value = "${kafka.partition-count}") int partitionCount,
            @Value(value = "${kafka.replication-factor}") short replicationFactor) {
        return new NewTopic(upstreamKafkaTopic, partitionCount, replicationFactor);
    }

    @Bean
    public NewTopic downstreamTopic(
            @Value(value = "${kafka.downstream.topic}") String downstreamKafkaTopic,
            @Value(value = "${kafka.partition-count}") int partitionCount,
            @Value(value = "${kafka.replication-factor}") short replicationFactor) {
        return new NewTopic(downstreamKafkaTopic, partitionCount, replicationFactor);
    }

    @Bean
    public ProducerFactory<Integer, TestEntity> producerFactory() {
        Map<String, Object> configProps = Maps.newHashMap();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServersStr);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<Integer, TestEntity> kafkaTemplate() {
        return new KafkaTemplate(producerFactory());
    }

    @Bean
    public EntityMapper<String, TestEntity> entityMapper() {
        return new TestEntityMapper();
    }

    @Bean
    public DownstreamEntityEmissionService downstreamEmissionService(
            @Value(value = "${kafka.downstream.topic}") String downstreamKafkaTopic,
            EntityMapper<String, TestEntity> entityMapper,
            KafkaTemplate<Integer, TestEntity> kafkaTemplate) {
        return new DownstreamEntityEmissionService(new BasicEntityQueue<>(entityMapper), downstreamKafkaTopic, kafkaTemplate);
    }

    @Bean
    public CsvEntityProducer<Integer, TestEntity> entityProducer(
            @Value(value = "${kafka.upstream.topic}") String upstreamKafkaTopic,
            EntityMapper<String, TestEntity> entityMapper,
            KafkaTemplate<Integer, TestEntity> kafkaTemplate,
            DownstreamEntityEmissionService<String, ?, ?>... downstreamEntityEmissionServices) {
        return new CsvEntityProducer<>(entityMapper, upstreamKafkaTopic, kafkaTemplate, downstreamEntityEmissionServices);
    }
}
