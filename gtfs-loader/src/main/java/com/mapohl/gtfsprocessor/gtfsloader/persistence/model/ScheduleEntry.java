package com.mapohl.gtfsprocessor.gtfsloader.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "schedule_entries")
public class ScheduleEntry extends AbstractEntity {

    @Id
    @Column(name = "schedule_entry_id", nullable = false)
    int id;

    @Column(name = "monday", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    boolean monday;

    @Column(name = "tuesday", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    boolean tuesday;

    @Column(name = "wednesday", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    boolean wednesday;

    @Column(name = "thursday", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    boolean thursday;

    @Column(name = "friday", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    boolean friday;

    @Column(name = "saturday", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    boolean saturday;

    @Column(name = "sunday", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    boolean sunday;

    @Column(name = "start_date", nullable = false)
    LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    LocalDate endDate;
}
