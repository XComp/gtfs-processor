package com.mapohl.gtfsprocessor.gtfsloader.persistence.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "transfers")
public class Transfer extends AbstractEntity {

    @Id
    @Column(name = "transfer_id", nullable = false)
    int id;

    // TODO: FK
    @Column(name = "from_stop_id", nullable = false)
    String fromStop;

    // TODO: FK
    @Column(name = "to_stop_id", nullable = false)
    String toStop;

    @Column(name = "transfer_type", nullable = false)
    int type;

    @Column(name = "min_transfer_time", nullable = false)
    int minimalTransferTime;

    // TODO: FK
    @Column(name = "from_route_id")
    String fromRoute;

    // TODO: FK
    @Column(name = "to_route_id")
    String toRoute;

    // TODO: FK
    @Column(name = "from_trip_id")
    int fromTrip;

    // TODO: FK
    @Column(name = "to_trip_id")
    int toTrip;
}
