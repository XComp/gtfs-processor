package com.mapohl.gtfsprocessor.gtfsloader.persistence.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalTime;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "stop_times")
public class StopTime extends AbstractEntity {

    @Id
    @GeneratedValue
    @Column(name = "stop_time_id", nullable = false)
    int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    Trip trip;

    @Column(name = "arrival_time", nullable = false)
    LocalTime arrivalTime;

    @Column(name = "departure_time", nullable = false)
    LocalTime departureTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stop_id", nullable = false)
    Stop stop;

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
