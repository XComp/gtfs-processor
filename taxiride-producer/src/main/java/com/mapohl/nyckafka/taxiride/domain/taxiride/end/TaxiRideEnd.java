package com.mapohl.nyckafka.taxiride.domain.taxiride.end;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mapohl.nyckafka.genericproducer.domain.Entity;
import com.mapohl.nyckafka.taxiride.domain.NYCTaxiZone;
import com.mapohl.nyckafka.taxiride.domain.utils.NYCTaxiRideUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaxiRideEnd implements Entity<Integer> {

    private Integer entityId;

    @JsonProperty("drop_off_time")
    private String dropOffTimeStr;

    private double distance;

    private NYCTaxiZone dropOffZone;

    private double tollAmount;
    private double totalAmount;

    @JsonIgnore
    @Override
    public Instant getEventTime() {
        return NYCTaxiRideUtils.parse(this.getDropOffTimeStr());
    }
}
