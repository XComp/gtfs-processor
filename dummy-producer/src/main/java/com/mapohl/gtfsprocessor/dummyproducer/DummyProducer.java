package com.mapohl.gtfsprocessor.dummyproducer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.annotation.InboundChannelAdapter;

@Slf4j
@SpringBootApplication
@EnableBinding(Source.class)
public class DummyProducer {

    public static void main(String[] args) {
        SpringApplication.run(DummyProducer.class, args);
    }

    @InboundChannelAdapter(Source.OUTPUT)
    public String currentTimeInMillis() {

        final String greeting = "current time is: " + System.currentTimeMillis();

        log.info("producing: {}", greeting);
        return greeting;
    }

}
