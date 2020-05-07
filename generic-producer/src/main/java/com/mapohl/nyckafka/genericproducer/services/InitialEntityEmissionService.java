package com.mapohl.nyckafka.genericproducer.services;

import com.mapohl.nyckafka.genericproducer.domain.Entity;
import com.mapohl.nyckafka.genericproducer.services.sources.EntitySource;
import org.springframework.kafka.core.KafkaTemplate;

public class InitialEntityEmissionService<ID, E extends Entity<ID>> extends BaseEntityEmissionService<ID, E, EntitySource<E>> {

    public InitialEntityEmissionService(EntitySource<E> entitySource, String topic, KafkaTemplate<ID, E> kafkaTemplate) {
        super(entitySource, topic, kafkaTemplate);
    }
}
