package com.mapohl.gtfsprocessor.genericproducer.services;

import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;
import com.mapohl.gtfsprocessor.genericproducer.services.sources.EntityQueue;
import org.springframework.kafka.core.KafkaTemplate;

public class DownstreamEntityEmissionService<I, ID, E extends Entity<ID>> extends BaseEntityEmissionService<ID, E, EntityQueue<I, E>> {

    public DownstreamEntityEmissionService(EntityQueue<I, E> entitySource, String topic, KafkaTemplate<ID, E> kafkaTemplate) {
        super(entitySource, topic, kafkaTemplate);
    }
}
