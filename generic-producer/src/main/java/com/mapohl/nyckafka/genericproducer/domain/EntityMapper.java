package com.mapohl.nyckafka.genericproducer.domain;

import java.util.List;

public interface EntityMapper<I, E extends Entity<?>> {

    List<E> map(I input);

}
