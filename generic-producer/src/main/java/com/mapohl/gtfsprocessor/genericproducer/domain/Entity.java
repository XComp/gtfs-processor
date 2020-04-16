package com.mapohl.gtfsprocessor.genericproducer.domain;

import java.io.Serializable;
import java.time.Instant;

public interface Entity<ID> extends Serializable {

    ID getEntityId();

    Instant getCreationTime();
}
