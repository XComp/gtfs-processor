package com.mapohl.gtfsprocessor.speedtracker.domain;

import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import org.apache.spark.sql.Row;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;

public class SpeedTrackerMapper implements EntityMapper<SpeedTracker> {

    private static List<LinkPoint> parseLinkPoints(String linkPointStr) {
        return Arrays.stream(linkPointStr.split(" ")).flatMap((Function<String, Stream<LinkPoint>>) s -> {
            try {
                String[] values = s.split(",");
                return Stream.of(new LinkPoint(Double.parseDouble(values[0]), Double.parseDouble(values[1])));
            } catch (Throwable t) {
                // ignore invalid points
                return Stream.empty();
            }
        }).collect(Collectors.toList());
    }

    // 0 - ID
    // 1 - SPEED
    // 2 - TRAVEL_TIME
    // 3 - STATUS
    // 4 - DATA_AS_OF
    // 5 - LINK_ID
    // 6 - LINK_POINTS
    // 7 - ENCODED_POLY_LINE
    // 8 - ENCODED_POLY_LINE_LVLS
    // 9 - OWNER
    // 10 - TRANSCOM_ID
    // 11 - BOROUGH
    // 12 LINK_NAME
    @Override
    public SpeedTracker map(Row row) {
        return SpeedTracker.builder()
                .speed(row.getDouble(1))
                .travelTimeInSeconds(row.getInt(2))
                .creationTimeStr(row.getTimestamp(4).toString())
                .linkId(row.getInt(5))
                .linkPoints(parseLinkPoints(row.getString(6)))
                .borough(row.getString(11))
                .description(row.getString(12))
                .build();
    }
}
