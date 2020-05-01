package com.mapohl.gtfsprocessor.genericproducer.domain;

import java.util.Comparator;

public interface EntityMapper<I, E extends Entity<?>> {

    E map(I input);

    default Comparator<I> createComparator() {
        return Comparator.comparing(this::map);
    }
}
