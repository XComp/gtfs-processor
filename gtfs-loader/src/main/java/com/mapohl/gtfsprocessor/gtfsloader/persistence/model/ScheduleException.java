package com.mapohl.gtfsprocessor.gtfsloader.persistence.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "schedule_exceptions")
public class ScheduleException extends AbstractEntity {

    @Id
    @Column(name = "schedule_exception_id", nullable = false)
    int id;

    // TODO: FK
    @Column(name = "schedule_entry", nullable = false)
    int scheduleEntry;

    @Column(name = "date")
    LocalDate date;

    // TODO: enum?
    @Column(name = "exception_type")
    int exceptionType;

}
