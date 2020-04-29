package com.mapohl.gtfsprocessor.genericproducer.services.sources;

import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;
import com.mapohl.gtfsprocessor.genericproducer.utils.TimePeriod;

public interface EntitySource<E extends Entity<?>> {

    String getTopic();

    E take(TimePeriod timeSlot) throws Exception;

    E peek();

    boolean hasNext();
}
