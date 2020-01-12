package com.mapohl.gtfsprocessor.gtfsloader.persistence.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "trips")
public class Trip extends AbstractEntity {

    @Id
    @Column(name = "trip_id", nullable = false)
    int id;

    // TODO: add FK
    @Column(name = "route_id", nullable = false)
    String route;

    // TODO: add FK
    @Column(name = "schedule_entry_id", nullable = false)
    int scheduleEntry;

    @Column(name = "headsign")
    String headsign;

    @Column(name = "short_name")
    String shortName;

    // TODO: enum?
    @Column(name = "direction_id")
    int directionId;

    @Column(name = "block_id")
    String blockId;

    // TODO: fix FK relation
    @Column(name = "shape_id")
    int shape;

    @Column(name = "wheelchair_accessible")
    boolean wheelchairAccessible;

    @Column(name = "bikes_allowed")
    boolean bikesAllowed;
}
