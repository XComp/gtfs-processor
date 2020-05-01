package com.mapohl.gtfsprocessor.taxiride.domain.taxiride.intermediateprice;

import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import com.mapohl.gtfsprocessor.genericproducer.utils.TimePeriod;
import com.mapohl.gtfsprocessor.taxiride.domain.utils.NYCTaxiRideUtils;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.Instant;

@RequiredArgsConstructor
public class IntermediatePriceMapper implements EntityMapper<String, IntermediatePrice> {

    // TODO: we have to make the EntityMapper interface more general
    private final int intermediateStepCount = 1;

    @Override
    public IntermediatePrice map(String line) {
        String[] values = line.split(",");

        double totalAmount = Double.parseDouble(values[16]);
        double partialAmount = totalAmount / (this.intermediateStepCount + 1);

        Instant pickupTime = NYCTaxiRideUtils.parse(values[1]);
        Instant dropOffTime = NYCTaxiRideUtils.parse(values[2]);
        Duration stepLength = Duration.between(pickupTime, dropOffTime).dividedBy(this.intermediateStepCount + 1);

        if (stepLength.getSeconds() < 1) {
            return IntermediatePrice.builder()
                    .entityId(line.hashCode())
                    .eventTime(dropOffTime)
                    .price(totalAmount)
                    .build();
        }

        double intermediateAmount = 0.0;
        TimePeriod intermediateTimePeriod = new TimePeriod(pickupTime, stepLength);
        for (int i = 0; i < this.intermediateStepCount; i++) {
            intermediateAmount += partialAmount;
            intermediateTimePeriod = intermediateTimePeriod.next();

            return IntermediatePrice.builder()
                    .entityId(line.hashCode())
                    .eventTime(intermediateTimePeriod.getInclusiveStartTime())
                    .price(intermediateAmount)
                    .build();
        }

        // this return is never reached for now since the intermediateStepCount is fixed - this needs to be refactored
        return null;
    }
}
