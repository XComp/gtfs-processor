package com.mapohl.nyckafka.commonproducer.services.sources;

import com.mapohl.nyckafka.common.Entity;

public interface EntityQueue<I, E extends Entity<?>> extends EntitySource<E> {

    void add(I input);

    default void endOfDataReached() {
        this.add(null);
    }
}
