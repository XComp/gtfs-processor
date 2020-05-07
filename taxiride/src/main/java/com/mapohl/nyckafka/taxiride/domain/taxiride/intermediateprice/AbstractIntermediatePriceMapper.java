package com.mapohl.nyckafka.taxiride.domain.taxiride.intermediateprice;

import com.mapohl.nyckafka.commonproducer.domain.EntityMapper;
import com.mapohl.nyckafka.taxiride.domain.utils.NYCTaxiRideUtils;

import java.time.Instant;
import java.util.List;

public abstract class AbstractIntermediatePriceMapper implements EntityMapper<String, IntermediatePrice> {

    protected abstract List<IntermediatePrice> createEvents(int entityId, Instant pickupTime, Instant dropOffTime, double totalAmount);

    @Override
    public List<IntermediatePrice> map(String line) {
        String[] values = line.split(",");

        Instant pickupTime = NYCTaxiRideUtils.parse(values[1]);
        Instant dropOffTime = NYCTaxiRideUtils.parse(values[2]);
        double totalAmount = Double.parseDouble(values[16]);

        return this.createEvents(line.hashCode(), pickupTime, dropOffTime, totalAmount);
    }
}
