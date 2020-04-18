package com.mapohl.gtfsprocessor.taxiride.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NYCTaxiZone implements Serializable {

    private int zoneId;
    private String name;
    private Borough borough;
    private double latitude;
    private double longitude;
}
