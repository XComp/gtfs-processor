package com.mapohl.gtfsprocessor.gtfsloader.persistence.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Builder
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "routes")
public class Route extends AbstractEntity {

    @Id
    @Column(name = "route_id", nullable = false)
    String id;

    @Column(name = "agency_id", nullable = false)
    int agency;

    @Column(name = "short_name")
    String shortName;

    @Column(name = "long_name")
    String longName;

    @Column(name = "type")
    int type;

    @Column(name = "background_color")
    String backgroundColor;

    @Column(name = "text_color")
    String textColor;

    @Column(name = "description")
    String description;

}
