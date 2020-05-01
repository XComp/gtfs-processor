package com.mapohl.gtfsprocessor.taxiride.domain;

import java.util.Map;

public class TaxiRideEndMapper extends AbstractTaxiRideMapper<TaxiRideEnd> {

    public TaxiRideEndMapper(Map<Integer, NYCTaxiZone> nycTaxiZoneIndex) {
        super(nycTaxiZoneIndex);
    }

    @Override
    public TaxiRideEnd map(String line) {
        String[] values = line.split(",");
        return TaxiRideEnd.builder()
                .entityId(line.hashCode())
                .dropOffTimeStr(values[2])
                .distance(Double.parseDouble(values[4]))
                .dropOffZone(this.getNYCTaxiZone(Integer.parseInt(values[8])))
                .tollAmount(Double.parseDouble(values[14]))
                .totalAmount(Double.parseDouble(values[16]))
                .build();
    }
}
