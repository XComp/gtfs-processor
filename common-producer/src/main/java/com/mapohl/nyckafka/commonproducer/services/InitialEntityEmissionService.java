package com.mapohl.nyckafka.commonproducer.services;

import com.mapohl.nyckafka.common.Entity;
import com.mapohl.nyckafka.commonproducer.services.sources.EntitySource;
import org.springframework.kafka.core.KafkaTemplate;

public class InitialEntityEmissionService<ID, E extends Entity<ID>> extends BaseEntityEmissionService<ID, E, EntitySource<E>> {

    public InitialEntityEmissionService(EntitySource<E> entitySource, String topic, KafkaTemplate<ID, E> kafkaTemplate) {
        super(entitySource, topic, kafkaTemplate);
    }
}
