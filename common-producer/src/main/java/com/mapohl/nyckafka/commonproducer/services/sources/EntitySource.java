package com.mapohl.nyckafka.commonproducer.services.sources;

import com.mapohl.nyckafka.common.Entity;
import com.mapohl.nyckafka.common.TimePeriod;

import java.time.Instant;

public interface EntitySource<E extends Entity<?>> {

    Instant peekNextEventTime();

    boolean isEmpty();

    boolean hasNext();

    boolean hasNext(TimePeriod timePeriod);

    E next(TimePeriod timePeriod);
}
