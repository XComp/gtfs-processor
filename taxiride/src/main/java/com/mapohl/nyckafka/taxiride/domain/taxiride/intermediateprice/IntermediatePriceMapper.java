package com.mapohl.nyckafka.taxiride.domain.taxiride.intermediateprice;

import com.google.common.collect.Lists;
import com.mapohl.nyckafka.common.TimePeriod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class IntermediatePriceMapper extends AbstractIntermediatePriceMapper {

    private final int intermediateStepCount;

    @Override
    protected List<IntermediatePrice> createEvents(int entityId, Instant pickupTime, Instant dropOffTime, double totalAmount) {
        double partialAmount = totalAmount / (this.intermediateStepCount + 1);
        Duration stepLength = Duration.between(pickupTime, dropOffTime).dividedBy(this.intermediateStepCount + 1);

        if (stepLength.getSeconds() < 1) {
            return Lists.newArrayList();
        }

        List<IntermediatePrice> entities = Lists.newArrayList();
        double intermediateAmount = 0.0;
        TimePeriod intermediateTimePeriod = new TimePeriod(pickupTime, stepLength);
        for (int i = 0; i < this.intermediateStepCount; i++) {
            intermediateAmount += partialAmount;
            intermediateTimePeriod = intermediateTimePeriod.next();

            entities.add(IntermediatePrice.builder()
                    .entityId(entityId)
                    .eventTime(intermediateTimePeriod.getInclusiveStartTime())
                    .price(intermediateAmount)
                    .build());
        }

        return entities;
    }
}
