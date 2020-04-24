# GTFS Processor

This is a small playground project to play around with Kafka and SpringBoot. I got the 
initial configuration from @daggerok's GitHub project `spring-streaming-with-kafka`.

## Data

### NYC Speed Tracking data

Download-Link: https://data.cityofnewyork.us/Transportation/Real-Time-Traffic-Speed-Data/qkm5-nuaq

### NYC Taxi data

Download-Link: https://www1.nyc.gov/site/tlc/about/tlc-trip-record-data.page

### NYC Taxi Zone lookup data

Download-Page: https://www1.nyc.gov/site/tlc/about/tlc-trip-record-data.page
(Download-Link: https://s3.amazonaws.com/nyc-tlc/misc/taxi+_zone_lookup.csv)

The Python script `src/main/python/extract_latlon.py` is used to create the actual lookup table 
used in this was used to extract the interesting information out of the `*.geojson` file.

The resulting lookup table was saved under `src/main/resources/nyc_taxi_zones.csv.gz`.

## Execution

`tmux.sh` can be used to start a session with two producers and two consumers being started. The script 
can be started from within a TMux session:
```bash
./tmux.sh start
```

All processes can be stopped: `./tmux.sh stop`

### Commands

Start the Kafka infrastructure:
```bash
./gradlew composeUp
```

Start dummy consumer reading from the topic `<topic>`:
```bash
./gradlew dummy-consumer:bootRun -Pargs="--kafka.topic=<topic>"
```

Start the speedtracker producer (filtering all entries that were created before May 5, 2017):
```bash
./gradlew speedtracker-producer:bootRun --args="-c <path-to-csv> -s 2017-05-05T09:44:00"
```

Start the taxiride producer (filtering all entries that were created before January 1, 2019):
```bash
./gradlew taxiride-producer:bootRun --args="-c <path-to-csv> -s 2019-01-01T00:00:00"
```

Shutdown the Kafka infrastructure:
```bash
./gradlew composeDown
```
