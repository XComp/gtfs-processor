package com.mapohl.gtfsprocessor.taxiride.domain.taxiride.start;

import com.google.common.collect.Lists;
import com.mapohl.gtfsprocessor.taxiride.domain.AbstractTaxiRideMapper;
import com.mapohl.gtfsprocessor.taxiride.domain.NYCTaxiZone;

import java.util.List;
import java.util.Map;

public class TaxiRideStartMapper extends AbstractTaxiRideMapper<TaxiRideStart> {

    public TaxiRideStartMapper(Map<Integer, NYCTaxiZone> nycTaxiZoneIndex) {
        super(nycTaxiZoneIndex);
    }

    @Override
    public List<TaxiRideStart> map(String line) {
        String[] values = line.split(",");
        return Lists.newArrayList(TaxiRideStart.builder()
                .entityId(line.hashCode())
                .pickupTimeStr(values[1])
                .passengerCount(Integer.parseInt(values[3]))
                .pickupZone(this.getNYCTaxiZone(Integer.parseInt(values[7])))
                .build());
    }
}
