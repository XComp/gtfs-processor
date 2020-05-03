package com.mapohl.gtfsprocessor.genericproducer.domain;

import com.google.common.collect.Lists;

import java.util.List;

public class IdentityMapper<E extends Entity<?>> implements EntityMapper<E, E> {
    @Override
    public List<E> map(E input) {
        return Lists.newArrayList(input);
    }
}
