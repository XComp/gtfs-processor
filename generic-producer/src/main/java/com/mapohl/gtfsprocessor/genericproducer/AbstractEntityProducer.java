package com.mapohl.gtfsprocessor.genericproducer;

import com.google.common.collect.Lists;
import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;
import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import com.mapohl.gtfsprocessor.genericproducer.services.sources.EntityQueue;
import com.mapohl.gtfsprocessor.genericproducer.services.sources.EntitySource;
import com.mapohl.gtfsprocessor.genericproducer.services.sources.IteratorSource;
import com.mapohl.gtfsprocessor.genericproducer.utils.TimePeriod;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.KafkaTemplate;
import picocli.CommandLine;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)
public abstract class AbstractEntityProducer<I, ID, E extends Entity<ID>> implements Callable<Integer>, CommandLineRunner {

    private final KafkaTemplate<ID, E> kafkaTemplate;
    private final String kafkaTopic;
    private final EntityMapper<I, E> entityMapper;
    private final EntityQueue<I, E>[] downstreamEntityQueues;

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

    protected void logParameters() {
        log.info("Parameter information:");
        log.info("  Start time (--start-time): {}", this.inclusiveStartTimeStr);
        log.info("  Time slot length (--time-slot-length/time-unit): {} {}", this.timeSlotLength, this.timeSlotLengthTimeUnit);
        log.info("  Real time slot length (--real-time-slot-length/time-unit): {} {}", this.realTimeSlotLength, this.realTimeSlotLengthTimeUnit);
        log.info("  Header lines (--header-lines): {}", this.initialLinesToIgnore);
        log.info("  Line limit (--line-limit): {}", this.lineLimit);
        log.info("  Entity limit (--entity-limit): {}", this.entityLimit);
    }

    private Instant getStartTime() {
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME
                .withZone(ZoneOffset.UTC)
                .parse(this.getInclusiveStartTimeStr(), Instant::from);
    }

    private Duration getTimeSlotDuration() {
        return Duration.of(this.getTimeSlotLength(), this.getTimeSlotLengthTimeUnit());
    }

    protected Duration getRealTimeSlotDuration() {
        return Duration.of(this.getRealTimeSlotLength(), this.getRealTimeSlotLengthTimeUnit());
    }

    protected TimePeriod getInitialTimePeriod() {
        return new TimePeriod(this.getStartTime(), this.getTimeSlotDuration());
    }

    protected EntitySource<E> createEntitySource(I... data) {
        return this.createEntitySource(Lists.newArrayList(data).iterator());
    }

    protected EntitySource<E> createEntitySource(Iterator<I> iterator) {
        return new IteratorSource<>(this.getKafkaTopic(),
                iterator,
                this.getEntityMapper(),
                this.getEntityLimit(),
                this.getDownstreamEntityQueues());
    }

    @Override
    public void run(String... args) {
        new CommandLine(this).execute(args);
    }
}
