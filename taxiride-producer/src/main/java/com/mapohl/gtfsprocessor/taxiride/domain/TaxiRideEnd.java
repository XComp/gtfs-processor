package com.mapohl.gtfsprocessor.taxiride.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

import static com.mapohl.gtfsprocessor.taxiride.domain.utils.NYCTaxiRideUtils.parse;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TaxiRideEnd implements Entity<Integer> {

    private Integer entityId;

    private String dropOffTimeStr;

    private double distance;

    private NYCTaxiZone dropOffZone;

    private double tollAmount;
    private double totalAmount;

    @JsonIgnore
    @Override
    public Instant getEventTime() {
        return parse(this.getDropOffTimeStr());
    }
}
