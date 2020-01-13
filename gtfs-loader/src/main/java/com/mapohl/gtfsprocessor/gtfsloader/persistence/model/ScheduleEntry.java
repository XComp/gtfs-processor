package com.mapohl.gtfsprocessor.gtfsloader.persistence.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Builder
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "schedule_entries")
public class ScheduleEntry extends AbstractEntity {

    @Id
    @Column(name = "schedule_entry_id", nullable = false)
    int id;

    @Column(name = "monday", nullable = false)
    boolean monday;

    @Column(name = "tuesday", nullable = false)
    boolean tuesday;

    @Column(name = "wednesday", nullable = false)
    boolean wednesday;

    @Column(name = "thursday", nullable = false)
    boolean thursday;

    @Column(name = "friday", nullable = false)
    boolean friday;

    @Column(name = "saturday", nullable = false)
    boolean saturday;

    @Column(name = "sunday", nullable = false)
    boolean sunday;

    @Column(name = "start_date", nullable = false)
    LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    LocalDate endDate;
}
