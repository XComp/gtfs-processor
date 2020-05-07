package com.mapohl.nyckafka.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.io.Serializable;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public interface EntityData extends Serializable {
}
