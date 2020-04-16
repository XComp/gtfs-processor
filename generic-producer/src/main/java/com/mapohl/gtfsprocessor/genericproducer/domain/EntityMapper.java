package com.mapohl.gtfsprocessor.genericproducer.domain;

import org.apache.spark.sql.Row;

import java.io.Serializable;

public interface EntityMapper<E extends Entity<?>> extends Serializable {

    E map(Row row);
}
