package com.mapohl.gtfsprocessor.speedtracker;

import com.mapohl.gtfsprocessor.genericproducer.EntityProducer;
import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import com.mapohl.gtfsprocessor.genericproducer.services.KafkaEmitService;
import com.mapohl.gtfsprocessor.speedtracker.domain.SpeedTracker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;

@Slf4j
@AllArgsConstructor
@SpringBootApplication
public class SpeedTrackerProducer implements CommandLineRunner {

    private KafkaEmitService<Long, SpeedTracker> kafkaEmitService;

    private EntityMapper<SpeedTracker> entityMapper;

    public static void main(String[] args) {
        SpringApplication.run(SpeedTrackerProducer.class, args).close();
    }

    @Override
    public void run(String... args) {
        int returnCode = new CommandLine(new EntityProducer<>(
                this.entityMapper,
                this.kafkaEmitService)).execute(args);

        System.exit(returnCode);
    }
}