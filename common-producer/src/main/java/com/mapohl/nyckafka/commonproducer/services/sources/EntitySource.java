package com.mapohl.nyckafka.commonproducer.services.sources;

import com.mapohl.nyckafka.commonproducer.domain.Entity;
import com.mapohl.nyckafka.commonproducer.utils.TimePeriod;

import java.time.Instant;

public interface EntitySource<E extends Entity<?>> {

    Instant peekNextEventTime();

    boolean isEmpty();

    boolean hasNext();

    boolean hasNext(TimePeriod timePeriod);

    E next(TimePeriod timePeriod);
}
