package com.mapohl.gtfsprocessor.taxiride;

import com.google.common.base.Preconditions;
import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import com.mapohl.gtfsprocessor.genericproducer.services.EntityLoader;
import com.mapohl.gtfsprocessor.genericproducer.services.KafkaEmitService;
import com.mapohl.gtfsprocessor.taxiride.domain.TaxiRide;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.api.java.function.FilterFunction;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Slf4j
@AllArgsConstructor
@SpringBootApplication
public class TaxiRideProducer implements CommandLineRunner {

    private KafkaEmitService<Long, TaxiRide> kafkaEmitService;

    private EntityMapper<TaxiRide> entityMapper;

    @Override
    public void run(String... args) throws InterruptedException {
        Preconditions.checkArgument(args.length > 0, "CSV file was not passed");
        String csvFilePath = args[0];

        final Instant timeThreshold;
        if (args.length == 1) {
            timeThreshold = Instant.ofEpochMilli(0);
        } else {
            timeThreshold = DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneOffset.UTC).parse(args[1], Instant::from);
        }

        EntityLoader<TaxiRide> entityLoader = new EntityLoader<>(
                csvFilePath, TaxiRide.class, this.entityMapper)
                .withHeader()
                .withFilter((FilterFunction<TaxiRide>) v ->
                        v.getCreationTime().isAfter(timeThreshold));

        this.kafkaEmitService.emit(entityLoader, Duration.ofMinutes(1), ChronoUnit.MINUTES);
    }

    public static void main(String[] args) {
        SpringApplication.run(TaxiRideProducer.class, args).close();
    }
}
