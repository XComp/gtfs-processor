package com.mapohl.gtfsprocessor.test.domain;

import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;
import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;

import java.util.Collections;
import java.util.List;

public class NoEntitiesMapper<E extends Entity<?>> implements EntityMapper<Object, E> {

    @Override
    public List<E> map(Object input) {
        return Collections.emptyList();
    }
}
