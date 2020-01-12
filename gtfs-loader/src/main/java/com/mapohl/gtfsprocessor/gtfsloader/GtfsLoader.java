package com.mapohl.gtfsprocessor.gtfsloader;

import com.mapohl.gtfsprocessor.gtfsloader.configuration.RootConfiguration;
import org.springframework.boot.SpringApplication;

public class GtfsLoader {

    public static void main(String[] args) {
        SpringApplication.run(RootConfiguration.class, args);
    }
}
