package com.mapohl.gtfsprocessor.taxiride.domain.taxiride.intermediateprice;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
public class PerSecondsIntermediatePriceMapper extends AbstractIntermediatePriceMapper {

    private final long stepSizeInSeconds;

    @Override
    protected List<IntermediatePrice> createEvents(int entityId, Instant pickupTime, Instant dropOffTime, double totalAmount) {
        long seconds = dropOffTime.getEpochSecond() - pickupTime.getEpochSecond();
        long eventCount = (seconds - 1) / stepSizeInSeconds;

        double priceIncrease = totalAmount / seconds * stepSizeInSeconds;

        double intermediateAmount = 0.0;
        Instant eventTime = pickupTime;
        List<IntermediatePrice> entities = Lists.newArrayList();
        for (long eventCnt = 0; eventCnt < eventCount; eventCnt++) {
            intermediateAmount += priceIncrease;
            eventTime = eventTime.plusSeconds(this.stepSizeInSeconds);

            entities.add(IntermediatePrice.builder()
                    .entityId(entityId)
                    .eventTime(eventTime)
                    .price(intermediateAmount)
                    .build());
        }

        return entities;
    }
}
