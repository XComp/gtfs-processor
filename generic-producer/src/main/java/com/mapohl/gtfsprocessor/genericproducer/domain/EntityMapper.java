package com.mapohl.gtfsprocessor.genericproducer.domain;

import java.util.List;

public interface EntityMapper<I, E extends Entity<?>> {

    List<E> map(I input);

}
