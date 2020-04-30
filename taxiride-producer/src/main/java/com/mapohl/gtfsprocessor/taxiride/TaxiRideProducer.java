package com.mapohl.gtfsprocessor.taxiride;

import com.mapohl.gtfsprocessor.taxiride.configuration.TaxiRideConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class TaxiRideProducer {

    public static void main(String[] args) {
        SpringApplication.run(TaxiRideConfiguration.class, args).close();
    }

}