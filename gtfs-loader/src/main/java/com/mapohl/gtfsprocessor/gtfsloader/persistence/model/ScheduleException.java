package com.mapohl.gtfsprocessor.gtfsloader.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "schedule_exceptions")
public class ScheduleException extends AbstractEntity {

    @Id
    @Column(name = "schedule_exception_id", updatable = false, nullable = false)
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
