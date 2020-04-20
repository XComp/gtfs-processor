package com.mapohl.gtfsprocessor.speedtracker;

import com.mapohl.gtfsprocessor.genericproducer.EntityProducer;
import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import com.mapohl.gtfsprocessor.genericproducer.services.KafkaEmitService;
import com.mapohl.gtfsprocessor.speedtracker.domain.SpeedTracker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class SpeedTrackerProducer extends EntityProducer<Long, SpeedTracker> {

    public SpeedTrackerProducer(EntityMapper<SpeedTracker> entityMapper, KafkaEmitService<Long, SpeedTracker> kafkaEmitService) {
        super(entityMapper, kafkaEmitService);
    }

    public static void main(String[] args) {
        SpringApplication.run(SpeedTrackerProducer.class, args).close();
    }
}
