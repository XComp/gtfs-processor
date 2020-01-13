package com.mapohl.gtfsprocessor.gtfsloader.persistence.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDate;

@Builder
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "schedule_exceptions")
public class ScheduleException extends AbstractEntity {

    @Id
    @Column(name = "schedule_exception_id", nullable = false)
    int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_entry_id", nullable = false)
    ScheduleEntry scheduleEntry;

    @Column(name = "date")
    LocalDate date;

    // TODO: enum?
    @Column(name = "exception_type")
    int exceptionType;

}
