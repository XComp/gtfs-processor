package com.mapohl.gtfsprocessor.taxiride.domain;

import java.util.Map;

public class TaxiRideStartMapper extends AbstractTaxiRideMapper<TaxiRideStart> {

    public TaxiRideStartMapper(Map<Integer, NYCTaxiZone> nycTaxiZoneIndex) {
        super(nycTaxiZoneIndex);
    }

    @Override
    public TaxiRideStart map(String line) {
        String[] values = line.split(",");
        return TaxiRideStart.builder()
                .entityId(line.hashCode())
                .pickupTimeStr(values[1])
                .passengerCount(Integer.parseInt(values[3]))
                .pickupZone(this.getNYCTaxiZone(Integer.parseInt(values[7])))
                .build();
    }
}
