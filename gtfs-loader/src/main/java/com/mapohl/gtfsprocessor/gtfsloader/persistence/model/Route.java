package com.mapohl.gtfsprocessor.gtfsloader.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "routes")
public class Route extends AbstractEntity {

    @Id
    @Column(name = "route_id", nullable = false)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id", nullable = false)
    Agency agency;

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
