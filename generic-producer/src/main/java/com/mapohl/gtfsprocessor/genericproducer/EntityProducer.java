package com.mapohl.gtfsprocessor.genericproducer;

import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;
import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import com.mapohl.gtfsprocessor.genericproducer.services.EntityEmissionScheduler;
import com.mapohl.gtfsprocessor.genericproducer.services.sources.EntitySource;
import com.mapohl.gtfsprocessor.genericproducer.services.sources.IteratorSource;
import com.mapohl.gtfsprocessor.genericproducer.utils.LineIterator;
import com.mapohl.gtfsprocessor.genericproducer.utils.TimePeriod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.KafkaTemplate;
import picocli.CommandLine;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Callable;
import java.util.zip.GZIPInputStream;

@Slf4j
@RequiredArgsConstructor
public class EntityProducer<ID, E extends Entity<ID>> implements Callable<Integer>, CommandLineRunner {

    private final KafkaTemplate<ID, E> kafkaTemplate;
    private final String kafkaTopic;
    private final EntityMapper<String, E> entityMapper;

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

    @CommandLine.Option(names = {"-h", "--header-lines"}, defaultValue = "1")
    private int initialLinesToIgnore;

    @CommandLine.Option(names = {"-l", "--line-limit"}, defaultValue = "10000")
    private int lineLimit;

    @CommandLine.Option(names = {"-e", "--entity-limit"}, defaultValue = Integer.MAX_VALUE + "")
    private int entityLimit;

    private InputStreamReader createReader() throws IOException {
        InputStream inputStream = new FileInputStream(this.csvFilePath);
        if (this.csvFilePath.endsWith(".gz")) {
            inputStream = new GZIPInputStream(inputStream);
        }

        return new InputStreamReader(inputStream, StandardCharsets.UTF_8);
    }

    @Override
    public Integer call() throws Exception {
        log.info("Parameter information:");
        log.info("  CSV file path (--csv): {}", this.csvFilePath);
        log.info("  Start time (--start-time): {}", this.inclusiveStartTimeStr);
        log.info("  Time slot length (--time-slot-length/time-unit): {} {}", this.timeSlotLength, this.timeSlotLengthTimeUnit);
        log.info("  Real time slot length (--real-time-slot-length/time-unit): {} {}", this.realTimeSlotLength, this.realTimeSlotLengthTimeUnit);
        log.info("  Header lines (--header-lines): {}", this.initialLinesToIgnore);
        log.info("  Line limit (--line-limit): {}", this.lineLimit);
        log.info("  Entity limit (--entity-limit): {}", this.entityLimit);

        try (Reader reader = this.createReader()) {
            LineIterator lineIterator = new LineIterator(reader, this.lineLimit, this.initialLinesToIgnore);
            EntitySource<E> entitySource = new IteratorSource<>(
                    this.kafkaTopic, lineIterator, this.entityMapper, this.entityLimit);
            EntityEmissionScheduler<ID, E> entityEmissionScheduler = new EntityEmissionScheduler<>(
                    this.kafkaTemplate, entitySource);

            final Instant timeThreshold = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                    .withZone(ZoneOffset.UTC)
                    .parse(this.inclusiveStartTimeStr, Instant::from);
            Duration timeSlotDuration = Duration.of(this.timeSlotLength, this.timeSlotLengthTimeUnit);
            Duration realTimeSlotDuration = Duration.of(this.realTimeSlotLength, this.realTimeSlotLengthTimeUnit);

            entityEmissionScheduler.emit(new TimePeriod(timeThreshold, timeSlotDuration), realTimeSlotDuration);
        }

        return 0;
    }

    @Override
    public void run(String... args) {
        new CommandLine(this).execute(args);
    }
}
