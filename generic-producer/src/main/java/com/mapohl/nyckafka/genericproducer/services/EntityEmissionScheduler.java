package com.mapohl.nyckafka.genericproducer.services;

import com.mapohl.nyckafka.genericproducer.utils.TimePeriod;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Slf4j
public class EntityEmissionScheduler {

    private final BaseEntityEmissionService initialEmissionService;
    private final int entityLimit;
    private final BaseEntityEmissionService[] downstreamEmissionServices;

    public EntityEmissionScheduler(InitialEntityEmissionService initialEmissionService,
                                   DownstreamEntityEmissionService... otherEmissionServices) {
        this(initialEmissionService, Integer.MAX_VALUE, otherEmissionServices);
    }

    public EntityEmissionScheduler(BaseEntityEmissionService initialEmissionService, int entityLimit, BaseEntityEmissionService... downstreamEmissionServices) {
        this.initialEmissionService = initialEmissionService;
        this.entityLimit = entityLimit;
        this.downstreamEmissionServices = downstreamEmissionServices;
    }

    private static String format(Instant instant) {
        return instant != null ? instant.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME) : "null";
    }

    private boolean hasData() {
        if (this.initialEmissionService.hasNext()) {
            return true;
        }

        for (BaseEntityEmissionService entityEmissionService : this.downstreamEmissionServices) {
            if (entityEmissionService.hasNext()) {
                return true;
            }
        }

        return false;
    }

    public void emit(TimePeriod initialTimePeriod, Duration realtimeTimeSlotLength) throws Exception {
        TimePeriod currentTimePeriod = initialTimePeriod;

        long startMillis = System.currentTimeMillis();
        int initialEntityCount = 0;
        int totalEntityCount = 0;
        while (this.hasData() && initialEntityCount < this.entityLimit) {
            int entityCount = this.initialEmissionService.emit(currentTimePeriod, this.entityLimit - initialEntityCount);
            initialEntityCount += entityCount;
            Instant earliestEventTime = this.initialEmissionService.peekNextEventTime();

            for (BaseEntityEmissionService entityEmissionService : this.downstreamEmissionServices) {
                entityCount += entityEmissionService.emit(currentTimePeriod);

                Instant nextEventTime = entityEmissionService.peekNextEventTime();
                if (earliestEventTime == null || (nextEventTime != null && earliestEventTime.isAfter(nextEventTime))) {
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
