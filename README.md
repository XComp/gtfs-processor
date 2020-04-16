# GTFS Processor

This is a small playground project to play around with Kafka and SpringBoot. I got the 
initial configuration from @daggerok's GitHub project `spring-streaming-with-kafka`.

## Data

### NYC Speed Tracking data

Download-Link: https://data.cityofnewyork.us/Transportation/Real-Time-Traffic-Speed-Data/qkm5-nuaq

### NYC Taxi data

Download-Link: https://www1.nyc.gov/site/tlc/about/tlc-trip-record-data.page

## Commands

Start the Kafka infrastructure:
```bash
./gradlew composeUp
```
Start dummy consumer reading from the topic `speedtrackers`:
```bash
./gradlew dummy-consumer:bootRun
```
Start the speedtracker producer (filtering all entries that were created before May 5, 2017):
```bash
./gradlew speedtracker-producer:bootRun --args="<path-to-csv> '2017-05-05T00:00:00'"
```
Shutdown the Kafka infrastructure:
```bash
./gradlew composeDown
```