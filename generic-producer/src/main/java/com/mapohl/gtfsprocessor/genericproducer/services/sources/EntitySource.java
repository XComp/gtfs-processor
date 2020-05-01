package com.mapohl.gtfsprocessor.genericproducer.services.sources;

import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;

import java.time.Instant;
import java.util.Iterator;

public interface EntitySource<E extends Entity<?>> extends Iterator<E> {

    Instant peekNextEventTime();

    boolean isEmpty();
}
