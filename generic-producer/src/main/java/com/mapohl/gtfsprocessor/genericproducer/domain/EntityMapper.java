package com.mapohl.gtfsprocessor.genericproducer.domain;

public interface EntityMapper<I, E extends Entity<?>> {

    E map(I input);
}
