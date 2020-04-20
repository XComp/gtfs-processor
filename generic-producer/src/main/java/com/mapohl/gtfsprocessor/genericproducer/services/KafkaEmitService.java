package com.mapohl.gtfsprocessor.genericproducer.services;

import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;
import com.mapohl.gtfsprocessor.genericproducer.services.entityloader.EntityLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaEmitService<ID, E extends Entity<ID>> {

    private static final DateTimeFormatter INSTANT_FORMATTER = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneOffset.UTC);

    private final KafkaTemplate<ID, E> kafkaTemplate;
    private final NewTopic kafkaTopic;
    private final BlockingQueue<E> entityQueue;

    public KafkaEmitService(KafkaTemplate<ID, E> kafkaTemplate, NewTopic kafkaTopic) {
        this(kafkaTemplate, kafkaTopic, new ArrayBlockingQueue<>(10_000));
    }

    public void emit(EntityLoader<E> entityLoader, Duration timeSlotLength, TemporalUnit initialAccuracy, Duration realtimeTimeSlotLength) throws Exception {
        entityLoader.load(this.entityQueue);

        Instant currentTime = null;
        Instant nextTimeSlot = null;

        int entityCount = 0;
        while (!entityQueue.isEmpty() || !entityLoader.endOfData()) {
            E entity = entityQueue.take();

            if (entity == null) {
                continue;
            }

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
                log.info("{} sent for {}s (second to next emission: {}).",
                        entityCount == 1 ? "1 row was" : entityCount + " rows were",
                        INSTANT_FORMATTER.format(currentTime),
                        INSTANT_FORMATTER.format(entityTime));
                entityCount = 0;

                Thread.sleep(realtimeTimeSlotLength.toMillis());
                currentTime = nextTimeSlot;
                nextTimeSlot = currentTime.plus(timeSlotLength);
            }

            this.kafkaTemplate.send(this.kafkaTopic.name(), entity.getEntityId(), entity);
            entityCount++;
        }

        log.info("Final emission: {} sent for {}s.",
                entityCount == 1 ? "1 row was" : entityCount + " rows were",
                INSTANT_FORMATTER.format(currentTime));
    }
}
