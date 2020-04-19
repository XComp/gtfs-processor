package com.mapohl.gtfsprocessor.genericproducer.services.entityloader;

import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;

import java.util.function.Predicate;

public abstract class AbstractEntityLoader<E extends Entity<?>> implements EntityLoader<E> {

    private Predicate<String> lineFilter = l -> true;
    private Predicate<E> entityFilter = e -> true;

    private volatile boolean endOfData = false;

    @Override
    public EntityLoader<E> withLineFilter(Predicate<String> lineFilter) {
        this.lineFilter = lineFilter;

        return this;
    }

    protected boolean process(String line) {
        return this.lineFilter.test(line);
    }

    @Override
    public EntityLoader<E> withEntityFilter(Predicate<E> entityFilter) {
        this.entityFilter = entityFilter;

        return this;
    }

    protected boolean process(E entity) {
        return this.entityFilter.test(entity);
    }

    protected void endOfDataReached() {
        this.endOfData = true;
    }

    @Override
    public boolean endOfData() {
        return this.endOfData;
    }
}
