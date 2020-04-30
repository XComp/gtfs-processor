package com.mapohl.gtfsprocessor.genericproducer.services;

import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;
import com.mapohl.gtfsprocessor.genericproducer.services.sources.EntitySource;
import com.mapohl.gtfsprocessor.genericproducer.utils.TimePeriod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Slf4j
@RequiredArgsConstructor
public class BaseEntityEmissionService<ID, E extends Entity<ID>, S extends EntitySource<E>> {

    private final S entitySource;
    private final String topic;
    private final KafkaTemplate<ID, E> kafkaTemplate;

    private static String format(Instant instant) {
        return instant.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME);
    }

    public int emit(TimePeriod timePeriod) {
        int entityCount = 0;
        while (this.hasNext() && !timePeriod.timeIsAfterTimePeriod(this.peekNextEventTime())) {
            E entity = this.entitySource.next();

            if (timePeriod.timeIsBeforeTimePeriod(entity.getEventTime())) {
                log.debug("Entity was skipped due to its event time ({}) being before the currently processed time period {}.",
                        format(entity.getEventTime()),
                        timePeriod);
                continue;
            }

            this.kafkaTemplate.send(this.topic, entity);
            entityCount++;
        }

        return entityCount;
    }

    public boolean hasNext() {
        return this.entitySource.hasNext();
    }

    public Instant peekNextEventTime() {
        return this.entitySource.peekNextEventTime();
    }

    public S getEntitySource() {
        return this.entitySource;
    }
}
