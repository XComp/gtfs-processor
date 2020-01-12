package com.mapohl.gtfsprocessor.gtfsloader.persistence.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.Instant;

@Data
@MappedSuperclass
public abstract class AbstractEntity {

    @Column(name = "CREATED_AT", nullable = false)
    Instant createdAt = Instant.now();
}
