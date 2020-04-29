package com.mapohl.gtfsprocessor.genericproducer.domain;

import java.io.Serializable;
import java.time.Instant;

public interface Entity<ID> extends Serializable, Comparable<Entity<?>> {

    ID getEntityId();

    Instant getEventTime();

    @Override
    default int compareTo(Entity<?> otherEntity) {
        return this.getEventTime().compareTo(otherEntity.getEventTime());
    }
}
