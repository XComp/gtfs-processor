package com.mapohl.gtfsprocessor.genericproducer.services;

import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;

import java.io.Serializable;
import java.util.List;

public interface EntityLoader<E extends Entity<?>> extends Serializable {

    public List<E> load();
}
