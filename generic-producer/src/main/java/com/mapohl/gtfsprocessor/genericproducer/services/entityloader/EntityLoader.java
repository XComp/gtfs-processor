package com.mapohl.gtfsprocessor.genericproducer.services.entityloader;

import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;

import java.io.Serializable;
import java.util.Queue;
import java.util.function.Predicate;

public interface EntityLoader<E extends Entity<?>> extends Serializable {

    public default void load(Queue<E> entityQueue) throws Exception {
        this.load(entityQueue, Integer.MAX_VALUE);
    }

    public void load(Queue<E> entityQueue, int limit) throws Exception;

    public EntityLoader<E> withLineFilter(Predicate<String> line);

    public EntityLoader<E> withEntityFilter(Predicate<E> entity);

    public boolean endOfData();
}
