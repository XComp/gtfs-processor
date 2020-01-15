package com.mapohl.gtfsprocessor.gtfsloader;

import com.mapohl.gtfsprocessor.gtfsloader.configuration.RootConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;

@Slf4j
public class GtfsLoader {

    public static void main(String[] args) {
        SpringApplication.run(RootConfiguration.class, args);
    }
}
