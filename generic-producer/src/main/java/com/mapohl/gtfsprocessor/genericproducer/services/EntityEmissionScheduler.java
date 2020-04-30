package com.mapohl.gtfsprocessor.genericproducer.services;

import com.google.common.collect.Lists;
import com.mapohl.gtfsprocessor.genericproducer.utils.TimePeriod;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public class EntityEmissionScheduler {

    private final List<BaseEntityEmissionService> entityEmissionServices;

    public EntityEmissionScheduler(InitialEntityEmissionService initialEmissionService,
                                   DownstreamEntityEmissionService... otherEmissionServices) {
        this.entityEmissionServices = Lists.newArrayList(initialEmissionService);
        this.entityEmissionServices.addAll(Lists.newArrayList(otherEmissionServices));
    }

    private static String format(Instant instant) {
        return instant.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME);
    }

    private boolean hasData() {
        for (BaseEntityEmissionService entityEmissionService : this.entityEmissionServices) {
            if (entityEmissionService.hasNext()) {
                return true;
            }
        }

        return false;
    }

    public void emit(TimePeriod initialTimePeriod, Duration realtimeTimeSlotLength) throws Exception {
        TimePeriod currentTimePeriod = initialTimePeriod;

        long startMillis = System.currentTimeMillis();
        int totalEntityCount = 0;
        while (this.hasData()) {
            int entityCount = 0;
            Instant earliestEventTime = Instant.ofEpochMilli(Long.MAX_VALUE);
            for (BaseEntityEmissionService entityEmissionService : this.entityEmissionServices) {
                entityCount += entityEmissionService.emit(currentTimePeriod);

                Instant nextEventTime = entityEmissionService.peekNextEventTime();
                if (nextEventTime != null && earliestEventTime.isAfter(nextEventTime)) {
                    earliestEventTime = nextEventTime;
                }
            }

            log.info("{} sent for {} (second to next emission: {}).",
                    entityCount == 1 ? "1 row was" : entityCount + " rows were",
                    format(currentTimePeriod.getInclusiveStartTime()),
                    format(earliestEventTime));

            totalEntityCount += entityCount;
            currentTimePeriod = currentTimePeriod.next();
            Thread.sleep(realtimeTimeSlotLength.toMillis());
        }

        log.info("Emission finished after {}ms: {} emitted.",
                System.currentTimeMillis() - startMillis,
                totalEntityCount == 1 ? "1 entity was" : totalEntityCount + " entities were");
    }
}
