package com.mapohl.nyckafka.commonproducer.domain;

import java.util.List;

public interface EntityMapper<I, E extends Entity<?>> {

    List<E> map(I input);

}
