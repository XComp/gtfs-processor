# GTFS Processor

This is a small playground project to play around with Kafka and SpringBoot. I got the 
initial configuration from @daggerok's GitHub project `spring-streaming-with-kafka`.

## Commands

Start the Kafka infrastructure:
```bash
./gradlew composeUp
```
Start dummy consumer reading from the topic `stops`:
```bash
./gradlew dummy-consumer:bootRun
```
Start the stops producer:
```bash
./gradlew gtfs-processor:bootRun --args <path-to-zip-archive>
```
Shutdown the Kafka infrastructure:
```bash
./gradlew composeDown
```