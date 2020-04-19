package com.mapohl.gtfsprocessor.genericproducer.domain;

public interface EntityMapper<E extends Entity<?>> {

    public E map(String lines);
}
