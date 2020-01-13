package com.mapohl.gtfsprocessor.gtfsloader.persistence.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalTime;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "stop_times")
public class StopTime extends AbstractEntity {

    @Id
    @Column(name = "trip_id", nullable = false)
    int id;

    @Column(name = "arrival_time", nullable = false)
    LocalTime arrivalTime;

    @Column(name = "departure_time", nullable = false)
    LocalTime departureTime;

    // TODO: add FK
    @Column(name = "stop_id", nullable = false)
    String stop;

    @Column(name = "stop_sequence", nullable = false)
    int stopSequence;

    // TODO: enum?
    @Column(name = "pickup_type")
    int pickupType;

    // TODO: enum?
    @Column(name = "dropoff_type")
    int dropoffType;

    @Column(name = "stop_headsign")
    String stopHeadsign;
}
