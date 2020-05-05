package com.mapohl.gtfsprocessor.test.domain;

import com.google.common.collect.Lists;
import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;
import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;

import java.util.List;

public class IdentityMapper<E extends Entity<?>> implements EntityMapper<E, E> {

    @Override
    public List<E> map(E entity) {
        return Lists.newArrayList(entity);
    }
}
