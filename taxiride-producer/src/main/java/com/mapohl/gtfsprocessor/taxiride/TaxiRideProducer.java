package com.mapohl.gtfsprocessor.taxiride;

import com.mapohl.gtfsprocessor.genericproducer.EntityProducer;
import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import com.mapohl.gtfsprocessor.taxiride.domain.TaxiRide;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@SpringBootApplication
public class TaxiRideProducer extends EntityProducer<Long, TaxiRide> {

    public TaxiRideProducer(KafkaTemplate<Long, TaxiRide> kafkaTemplate, String kafkaTopic, EntityMapper<String, TaxiRide> entityMapper) {
        super(kafkaTemplate, kafkaTopic, entityMapper);
    }

    public static void main(String[] args) {
        SpringApplication.run(TaxiRideProducer.class, args).close();
    }

}