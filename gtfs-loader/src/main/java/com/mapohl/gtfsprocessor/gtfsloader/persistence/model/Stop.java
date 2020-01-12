package com.mapohl.gtfsprocessor.gtfsloader.persistence.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "stops")
public class Stop extends AbstractEntity {

    @Id
    @Column(name = "stop_id", nullable = false)
    String id;

    @Column(name = "stop_code")
    String stopCode;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "description")
    String description;

    @Column(name = "latitude")
    double latitude;

    @Column(name = "longitude")
    double longitude;

    @Column(name = "location_type")
    int locationType;

    @Column(name = "parent_station")
    String parentStation;

    @Column(name = "wheelchar_boarding")
    boolean wheelchairBoarding;

    @Column(name = "platform_code")
    String platformCode;

    @Column(name = "zone_id")
    String zoneId;
}
