package com.mapohl.gtfsprocessor.speedtracker;

import com.mapohl.gtfsprocessor.genericproducer.EntityProducer;
import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import com.mapohl.gtfsprocessor.speedtracker.domain.SpeedTracker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@SpringBootApplication
public class SpeedTrackerProducer extends EntityProducer<Long, SpeedTracker> {

    public SpeedTrackerProducer(KafkaTemplate<Long, SpeedTracker> kafkaTemplate, String kafkaTopic, EntityMapper<String, SpeedTracker> entityMapper) {
        super(kafkaTemplate, kafkaTopic, entityMapper);
    }

    public static void main(String[] args) {
        SpringApplication.run(SpeedTrackerProducer.class, args).close();
    }
}
