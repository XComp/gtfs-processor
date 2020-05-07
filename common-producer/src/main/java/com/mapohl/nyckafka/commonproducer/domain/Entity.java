package com.mapohl.nyckafka.commonproducer.domain;

import java.time.Instant;

public interface Entity<ID> extends EntityData, Comparable<Entity<?>> {

    ID getEntityId();

    Instant getEventTime();

    @Override
    default int compareTo(Entity<?> otherEntity) {
        return this.getEventTime().compareTo(otherEntity.getEventTime());
    }
}
