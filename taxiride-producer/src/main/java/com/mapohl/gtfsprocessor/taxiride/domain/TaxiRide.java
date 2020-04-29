package com.mapohl.gtfsprocessor.taxiride.domain;

import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;
import com.mapohl.gtfsprocessor.taxiride.domain.utils.NYCTaxiRideUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.format.DateTimeFormatter.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaxiRide implements Entity<Long> {

    private long id;

    private String pickupTimeStr;
    private String dropOffTimeStr;

    private int passengerCount;

    private double distance;

    private NYCTaxiZone pickupZone;
    private NYCTaxiZone dropOffZone;

    private int paymentTypeId;
    private double tollAmount;
    private double totalAmount;

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public Instant getEventTime() {
        return NYCTaxiRideUtils.parse(this.getPickupTimeStr());
    }
}
