package com.mapohl.gtfsprocessor.genericproducer.services.sources;

import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;
import com.mapohl.gtfsprocessor.genericproducer.utils.TimePeriod;

import java.time.Instant;

public interface EntitySource<E extends Entity<?>> {

    Instant peekNextEventTime();

    boolean isEmpty();

    boolean hasNext();

    boolean hasNext(TimePeriod timePeriod);

    E next(TimePeriod timePeriod);
}
