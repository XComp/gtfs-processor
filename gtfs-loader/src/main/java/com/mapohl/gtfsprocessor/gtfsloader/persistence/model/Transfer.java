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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_stop_id", nullable = false)
    Stop fromStop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_stop_id", nullable = false)
    Stop toStop;

    @Column(name = "transfer_type", nullable = false)
    int type;

    @Column(name = "min_transfer_time", nullable = false)
    int minimalTransferTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_route_id", nullable = false)
    Route fromRoute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_route_id", nullable = false)
    Route toRoute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_trip_id", nullable = false)
    Trip fromTrip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_trip_id", nullable = false)
    Trip toTrip;
}
