package com.mapohl.gtfsprocessor.taxiride.domain.taxiride.end;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
        return parse(this.getDropOffTimeStr());
    }
}
