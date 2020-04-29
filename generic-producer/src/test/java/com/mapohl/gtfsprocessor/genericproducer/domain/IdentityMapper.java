package com.mapohl.gtfsprocessor.genericproducer.domain;

public class IdentityMapper<E extends Entity<?>> implements EntityMapper<E, E> {
    @Override
    public E map(E input) {
        return input;
    }
}
