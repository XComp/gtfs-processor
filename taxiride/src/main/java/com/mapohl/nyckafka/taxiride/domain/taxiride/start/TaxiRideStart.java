package com.mapohl.nyckafka.taxiride.domain.taxiride.start;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mapohl.nyckafka.common.Entity;
import com.mapohl.nyckafka.taxiride.domain.NYCTaxiZone;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

import static com.mapohl.nyckafka.taxiride.domain.utils.NYCTaxiRideUtils.parse;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaxiRideStart implements Entity<Integer> {

    private Integer entityId;

    @JsonProperty("pickup_time")
    private String pickupTimeStr;

    private int passengerCount;

    private NYCTaxiZone pickupZone;

    @JsonIgnore
    @Override
    public Instant getEventTime() {
        return parse(this.getPickupTimeStr());
    }
}
