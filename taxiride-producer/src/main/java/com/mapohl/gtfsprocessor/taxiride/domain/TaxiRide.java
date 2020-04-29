package com.mapohl.gtfsprocessor.taxiride.domain;

import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaxiRide implements Entity<Long> {

    static DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .append(ISO_LOCAL_TIME)
            .toFormatter();

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
        return LocalDateTime.parse(this.getPickupTimeStr(), DATE_TIME_FORMATTER).toInstant(ZoneOffset.UTC);
    }
}
