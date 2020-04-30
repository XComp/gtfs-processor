package com.mapohl.gtfsprocessor.genericproducer.services;

import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;
import com.mapohl.gtfsprocessor.genericproducer.services.sources.EntitySource;
import org.springframework.kafka.core.KafkaTemplate;

public class InitialEntityEmissionService<ID, E extends Entity<ID>> extends BaseEntityEmissionService<ID, E, EntitySource<E>> {

    public InitialEntityEmissionService(EntitySource<E> entitySource, String topic, KafkaTemplate<ID, E> kafkaTemplate) {
        super(entitySource, topic, kafkaTemplate);
    }
}
