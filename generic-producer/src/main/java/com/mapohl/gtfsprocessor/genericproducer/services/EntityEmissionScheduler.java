package com.mapohl.gtfsprocessor.genericproducer.services;

import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;
import com.mapohl.gtfsprocessor.genericproducer.services.sources.EntitySource;
import com.mapohl.gtfsprocessor.genericproducer.utils.TimePeriod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Slf4j
@RequiredArgsConstructor
public class EntityEmissionScheduler<ID, E extends Entity<ID>> {

    private static final DateTimeFormatter INSTANT_FORMATTER = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneOffset.UTC);

    private final KafkaTemplate<ID, E> kafkaTemplate;

    private final EntitySource<E> entitySource;

    public void emit(TimePeriod initialTimePeriod, Duration realtimeTimeSlotLength) throws Exception {
        TimePeriod currentTimeslot = initialTimePeriod;

        int entityCount = 0;
        while (this.entitySource.hasNext()) {
            E entity = this.entitySource.take(currentTimeslot);
            if (entity == null) {
                log.info("{} sent for {} (second to next emission: {}).",
                        entityCount == 1 ? "1 row was" : entityCount + " rows were",
                        INSTANT_FORMATTER.format(currentTimeslot.getInclusiveStartTime()),
                        INSTANT_FORMATTER.format(this.entitySource.peek().getEventTime()));
                entityCount = 0;
                currentTimeslot = currentTimeslot.next();
                Thread.sleep(realtimeTimeSlotLength.toMillis());
                continue;
            }

            this.kafkaTemplate.send(this.entitySource.getTopic(), entity);
            entityCount++;
        }

        log.info("Final emission: {} sent for {}.",
                entityCount == 1 ? "1 row was" : entityCount + " rows were",
                INSTANT_FORMATTER.format(currentTimeslot.getInclusiveStartTime()));
    }
}
