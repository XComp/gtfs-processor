package com.mapohl.gtfsprocessor.taxiride;

import com.mapohl.gtfsprocessor.taxiride.configuration.TaxiRideStartConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaxiRideStartProducer {

    public static void main(String[] args) {
        SpringApplication.run(TaxiRideStartConfiguration.class, args).close();
    }

}