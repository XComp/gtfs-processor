package com.mapohl.gtfsprocessor.gtfsloader.persistence.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Builder
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "trips")
public class Trip extends AbstractEntity {

    @Id
    @Column(name = "trip_id", nullable = false)
    int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_entry_id", nullable = false)
    ScheduleEntry scheduleEntry;

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
