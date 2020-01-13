package com.mapohl.gtfsprocessor.gtfsloader.persistence.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Builder
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "agencies")
public class Agency extends AbstractEntity {

    @Id
    @Column(name = "agency_id", nullable = false)
    int id;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "url")
    String url;

    @Column(name = "timezone")
    String timezone;

    @Column(name = "language")
    String language;

    @Column(name = "phone_number")
    String phoneNumber;
}
