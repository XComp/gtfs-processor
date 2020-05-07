package com.mapohl.nyckafka.commonproducer.domain;

import com.mapohl.nyckafka.common.Entity;

import java.util.List;

public interface EntityMapper<I, E extends Entity<?>> {

    List<E> map(I input);

}
