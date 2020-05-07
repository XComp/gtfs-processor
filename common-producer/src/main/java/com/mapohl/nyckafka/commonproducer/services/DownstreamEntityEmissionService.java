package com.mapohl.nyckafka.commonproducer.services;

import com.mapohl.nyckafka.commonproducer.domain.Entity;
import com.mapohl.nyckafka.commonproducer.services.sources.EntityQueue;
import org.springframework.kafka.core.KafkaTemplate;

public class DownstreamEntityEmissionService<I, ID, E extends Entity<ID>> extends BaseEntityEmissionService<ID, E, EntityQueue<I, E>> {

    public DownstreamEntityEmissionService(EntityQueue<I, E> entitySource, String topic, KafkaTemplate<ID, E> kafkaTemplate) {
        super(entitySource, topic, kafkaTemplate);
    }
}
