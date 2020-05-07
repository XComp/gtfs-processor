package com.mapohl.nyckafka.genericproducer.services.sources;

import com.mapohl.nyckafka.genericproducer.domain.Entity;
import com.mapohl.nyckafka.genericproducer.utils.TimePeriod;

import java.time.Instant;

public interface EntitySource<E extends Entity<?>> {

    Instant peekNextEventTime();

    boolean isEmpty();

    boolean hasNext();

    boolean hasNext(TimePeriod timePeriod);

    E next(TimePeriod timePeriod);
}
