package com.mapohl.nyckafka.taxiride;

import com.mapohl.nyckafka.taxiride.configuration.TaxiRideConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;

@Slf4j
public class TaxiRideProducer {

    public static void main(String[] args) {
        SpringApplication.run(TaxiRideConfiguration.class, args).close();
    }

}