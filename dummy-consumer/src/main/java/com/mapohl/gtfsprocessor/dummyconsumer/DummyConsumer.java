package com.mapohl.gtfsprocessor.dummyconsumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.List;

@Slf4j
@SpringBootApplication
public class DummyConsumer {

    public static void main(String[] args) {
        SpringApplication.run(DummyConsumer.class, args);
    }

    @KafkaListener(
            topics = { "#{'${kafka.topic}'}" },
            groupId = "#{'${kafka.group-id}'}",
            containerFactory = "kafkaListenerContainerFactory",
            autoStartup = "true")
    public void consumeData(@Payload List<String> containers) {
        containers.forEach(data -> log.info("message received: '{}'", data));
    }
}
