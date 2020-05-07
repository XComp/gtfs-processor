package com.mapohl.nyckafka.genericproducer.services.sources;

import com.mapohl.nyckafka.genericproducer.domain.Entity;

public interface EntityQueue<I, E extends Entity<?>> extends EntitySource<E> {

    void add(I input);

    default void endOfDataReached() {
        this.add(null);
    }
}
