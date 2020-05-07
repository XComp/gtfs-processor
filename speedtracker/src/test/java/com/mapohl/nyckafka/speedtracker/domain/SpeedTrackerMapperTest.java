package com.mapohl.nyckafka.speedtracker.domain;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Stream;

import static com.mapohl.nyckafka.speedtracker.domain.SpeedTracker.DATE_TIME_FORMATTER;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SpeedTrackerMapperTest {

    private SpeedTrackerMapper testInstance = new SpeedTrackerMapper();

    private static Stream<Arguments> getLinkPointTestData() {
        return Stream.of(
                Arguments.arguments("", new double[0]),
                Arguments.arguments("asd", new double[0]),
                Arguments.arguments("4", new double[0]),
                Arguments.arguments("40.63511", new double[0]),
                Arguments.arguments("40.63511,", new double[0]),
                Arguments.arguments("40.63511,-", new double[0]),
                Arguments.arguments("40.63511,-7", new double[]{40.63511, -7.0}),
                Arguments.arguments("40.63511,-74.19581", new double[]{40.63511, -74.19581}),
                Arguments.arguments("40.63511,-74.19581 ", new double[]{40.63511, -74.19581}),
                Arguments.arguments("40.63511,-74.19581 asd", new double[]{40.63511, -74.19581}),
                Arguments.arguments("40.63511,-74.19581 40", new double[]{40.63511, -74.19581}),
                Arguments.arguments("40.63511,-74.19581 40.63381", new double[]{40.63511, -74.19581}),
                Arguments.arguments("40.63511,-74.19581 40.63381,", new double[]{40.63511, -74.19581}),
                Arguments.arguments("40.63511,-74.19581 40.63381,-", new double[]{40.63511, -74.19581}),
                Arguments.arguments("40.63511,-74.19581 40.63381, asd-74.19336,40.63086", new double[]{40.63511, -74.19581}),
                Arguments.arguments("40.63511,-74.19581 40.63381,asd -74.19336,40.63086", new double[]{40.63511, -74.19581}),
                Arguments.arguments("40.63511,-74.19581 asd 40.63381,-74.19336", new double[]{40.63511, -74.19581}),
                Arguments.arguments("40.63511,-74.19581 40.63381,-74.19336 40.63086", new double[]{40.63511, -74.19581, 40.63381, -74.19336})
        );
    }

    @DisplayName("Parse String to create LinkPoint lists")
    @ParameterizedTest(name = "Parse \"{0}\"")
    @MethodSource("getLinkPointTestData")
    void parseLinkPoints(String input, double... expectedLatLon) {
        List<LinkPoint> expected = Lists.newArrayList();
        for (int i = 0; i < expectedLatLon.length; i += 2) {
            expected.add(new LinkPoint(expectedLatLon[i], expectedLatLon[i + 1]));
        }

        List<LinkPoint> actual = SpeedTrackerMapper.parseLinkPoints(input);
        assertEquals(expected, actual);
    }

    @DisplayName("Parse String to create SpeedTracker entity")
    @Test
    void mapStringToEntity() {
        String creationTimeStr = "2020-01-09 14:40:47.0";
        Instant creationTime = LocalDateTime.parse(creationTimeStr, DATE_TIME_FORMATTER).toInstant(ZoneOffset.UTC);
        String[] values = new String[]{
                "1",                                                     // ID
                "10.5",                                                  // SPEED
                "1337",                                                  // TRAVEL_TIME
                "-101",                                                  // STATUS
                creationTimeStr,                                         // DATA_AS_OF
                "0",                                                     // LINK_ID
                "\"40.78819,-73.79052 40.7870405,-73.77592 40.786440\"", // LINK_POINTS
                "em...CoD",                                              // ENCODED_POLY_LINE
                "BBBBBBBB",                                              // ENCODED_POLY_LINE_LVLS
                "NYC-DOT-Region 10",                                     // OWNER
                "4362247",                                               // TRANSCOM_ID
                "Queens",                                                // BOROUGH
                "Link description"                                       // LINK_NAME
        };

        SpeedTracker expectedEntity = SpeedTracker.builder()
                .speed(10.5)
                .travelTimeInSeconds(1337)
                .creationTimeStr(creationTimeStr)
                .linkId(0)
                .linkPoints(Lists.newArrayList(
                        new LinkPoint(40.78819, -73.79052),
                        new LinkPoint(40.7870405, -73.77592)
                ))
                .borough("Queens")
                .description("Link description")
                .build();
        List<SpeedTracker> actualEntities = testInstance.map(String.join(",", values));
        assertEquals(1, actualEntities.size());
        SpeedTracker actualEntity = actualEntities.get(0);

        assertEquals(expectedEntity, actualEntity);
        assertEquals(expectedEntity.getEntityId(), creationTime.getEpochSecond());
    }
}