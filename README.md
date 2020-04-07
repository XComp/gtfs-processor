# GTFS Processor

This is a small playground project to play around with Kafka and SpringBoot. I got the 
initial configuration from @daggerok's GitHub project `spring-streaming-with-kafka`.

## Start

```bash
./gradlew composeUp
./gradlew gtfs-processor:bootRun --args <path-to-zip-archive>
```