package com.mapohl.gtfsprocessor.genericproducer.services;

import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaEmitService<ID, E extends Entity<ID>> {

    private static final DateTimeFormatter INSTANT_FORMATTER = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneOffset.UTC);

    private final KafkaTemplate<ID, E> kafkaTemplate;
    private final NewTopic kafkaTopic;

    public void emit(EntityLoader<E> entityLoader) throws InterruptedException {
        this.emit(entityLoader, Duration.ofSeconds(1), ChronoUnit.SECONDS);
    }

    public void emit(EntityLoader<E> entityLoader, Duration timeSlotLength, TemporalUnit initialAccuracy) throws InterruptedException {
        this.emit(entityLoader, timeSlotLength, initialAccuracy, Duration.ofSeconds(1));
    }

    public void emit(EntityLoader<E> entityLoader, Duration timeSlotLength, TemporalUnit initialAccuracy, Duration realtimeTimeSlotLength) throws InterruptedException {
        List<E> entities = entityLoader.load();

        Instant currentTime = null;
        Instant nextTimeSlot = null;

        int entityCount = 0;
        for (E entity : entities) {
            Instant entityTime = entity.getCreationTime();

            if (currentTime == null) {
                currentTime = entityTime.truncatedTo(initialAccuracy);
                nextTimeSlot = currentTime.plus(timeSlotLength);
            }

            if (entityTime.isBefore(currentTime)) {
                // skip older records
                log.info("Skip entity is waiting (creation time: '{}'). Current time: {}", entityTime, currentTime);
                continue;
            }

            while (!entityTime.isBefore(nextTimeSlot)) {
                if (entityCount > 0) {
                    log.info("{} rows were sent for {}s (next second to emit: {}).",
                            entityCount,
                            INSTANT_FORMATTER.format(currentTime),
                            INSTANT_FORMATTER.format(entityTime));
                    entityCount = 0;
                } else {
                    log.info("No rows were sent for {}s (next second to emit: {}).",
                            INSTANT_FORMATTER.format(currentTime),
                            INSTANT_FORMATTER.format(entityTime));
                }

                Thread.sleep(realtimeTimeSlotLength.toMillis());
                currentTime = nextTimeSlot;
                nextTimeSlot = currentTime.plus(timeSlotLength);
            }

            this.kafkaTemplate.send(this.kafkaTopic.name(), entity.getEntityId(), entity);
            entityCount++;
        }
    }
}
