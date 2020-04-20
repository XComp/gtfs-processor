package com.mapohl.gtfsprocessor.genericproducer;

import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;
import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import com.mapohl.gtfsprocessor.genericproducer.services.KafkaEmitService;
import com.mapohl.gtfsprocessor.genericproducer.services.entityloader.BackgroundEntityLoader;
import com.mapohl.gtfsprocessor.genericproducer.services.entityloader.EntityLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import picocli.CommandLine;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class EntityProducer<ID, E extends Entity<ID>> implements Callable<Integer>, CommandLineRunner {

    private final EntityMapper<E> entityMapper;
    private final KafkaEmitService<ID, E> kafkaEmitService;

    @CommandLine.Option(names = {"-c", "--csv"}, required = true)
    private String csvFilePath;

    @CommandLine.Option(names = {"-s", "--start-time"}, defaultValue = "1970-01-01T00:00:00")
    private String inclusiveStartTimeStr;

    @CommandLine.Option(names = {"-t", "--time-slot-length"}, defaultValue = "1")
    private int timeSlotLength;
    @CommandLine.Option(names = {"-tu", "--time-slot-time-unit"}, defaultValue = "MINUTES")
    private ChronoUnit timeSlotLengthTimeUnit;

    @CommandLine.Option(names = {"-r", "--real-time-slot-length"}, defaultValue = "1")
    private int realTimeSlotLength;
    @CommandLine.Option(names = {"-ru", "--real-time-slot-time-unit"}, defaultValue = "SECONDS")
    private ChronoUnit realTimeSlotLengthTimeUnit;

    @CommandLine.Option(names = {"-l", "--log-accuracy"}, defaultValue = "MINUTES")
    private ChronoUnit logAccuracy;

    @CommandLine.Option(names = {"-h", "--header-lines"}, defaultValue = "1")
    private int initialLinesToIgnore;

    @Override
    public Integer call() throws Exception {
        final Instant timeThreshold = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                .withZone(ZoneOffset.UTC)
                .parse(this.inclusiveStartTimeStr, Instant::from);

        log.info("Parameter information:");
        log.info("  CSV file path (--csv): {}", this.csvFilePath);
        log.info("  Start time (--start-time): {}", timeThreshold);
        log.info("  Time slot length (--time-slot-length/time-unit): {} {}", this.timeSlotLength, this.timeSlotLengthTimeUnit);
        log.info("  Real time slot length (--real-time-slot-length/time-unit): {} {}", this.realTimeSlotLength, this.realTimeSlotLengthTimeUnit);
        log.info("  Log accuracy (--log-accuracy): {}", this.logAccuracy);

        EntityLoader<E> entityLoader = new BackgroundEntityLoader<>(this.csvFilePath, this.entityMapper)
                .withInitialLinesToIgnore(this.initialLinesToIgnore)
                .withEntityFilter(v -> !v.getCreationTime().isBefore(timeThreshold));

        Duration timeSlotDuration = Duration.of(this.timeSlotLength, this.timeSlotLengthTimeUnit);
        Duration realTimeSlotDuration = Duration.of(this.realTimeSlotLength, this.realTimeSlotLengthTimeUnit);
        this.kafkaEmitService.emit(entityLoader, timeSlotDuration, this.logAccuracy, realTimeSlotDuration);

        return 0;
    }

    @Override
    public void run(String... args) {
        int returnCode = new CommandLine(new EntityProducer<>(
                this.entityMapper,
                this.kafkaEmitService)).execute(args);

        System.exit(returnCode);
    }
}
