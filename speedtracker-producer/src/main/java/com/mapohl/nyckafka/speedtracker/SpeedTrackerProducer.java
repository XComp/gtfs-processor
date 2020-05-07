package com.mapohl.nyckafka.speedtracker;

import com.mapohl.nyckafka.speedtracker.configuration.SpeedTrackerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;

@Slf4j
public class SpeedTrackerProducer {

    public static void main(String[] args) {
        SpringApplication.run(SpeedTrackerConfiguration.class, args).close();
    }
}
