package com.mapohl.nyckafka.test.domain;

import com.mapohl.nyckafka.commonproducer.domain.Entity;
import com.mapohl.nyckafka.commonproducer.domain.EntityMapper;

import java.util.Collections;
import java.util.List;

public class NoEntitiesMapper<E extends Entity<?>> implements EntityMapper<Object, E> {

    @Override
    public List<E> map(Object input) {
        return Collections.emptyList();
    }
}
