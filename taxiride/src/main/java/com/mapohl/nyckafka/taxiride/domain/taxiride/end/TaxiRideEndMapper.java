package com.mapohl.nyckafka.taxiride.domain.taxiride.end;

import com.google.common.collect.Lists;
import com.mapohl.nyckafka.taxiride.domain.AbstractTaxiRideMapper;
import com.mapohl.nyckafka.taxiride.domain.NYCTaxiZone;

import java.util.List;
import java.util.Map;

public class TaxiRideEndMapper extends AbstractTaxiRideMapper<TaxiRideEnd> {

    public TaxiRideEndMapper(Map<Integer, NYCTaxiZone> nycTaxiZoneIndex) {
        super(nycTaxiZoneIndex);
    }

    @Override
    public List<TaxiRideEnd> map(String line) {
        String[] values = line.split(",");
        return Lists.newArrayList(TaxiRideEnd.builder()
                .entityId(line.hashCode())
                .dropOffTimeStr(values[2])
                .distance(Double.parseDouble(values[4]))
                .dropOffZone(this.getNYCTaxiZone(Integer.parseInt(values[8])))
                .tollAmount(Double.parseDouble(values[14]))
                .totalAmount(Double.parseDouble(values[16]))
                .build());
    }
}
