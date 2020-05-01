package com.mapohl.gtfsprocessor.taxiride.domain.taxiride.start;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;
import com.mapohl.gtfsprocessor.taxiride.domain.NYCTaxiZone;
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
public class TaxiRideStart implements Entity<Integer> {

    private Integer entityId;

    private String pickupTimeStr;

    private int passengerCount;

    private NYCTaxiZone pickupZone;

    @JsonIgnore
    @Override
    public Instant getEventTime() {
        return parse(this.getPickupTimeStr());
    }
}
