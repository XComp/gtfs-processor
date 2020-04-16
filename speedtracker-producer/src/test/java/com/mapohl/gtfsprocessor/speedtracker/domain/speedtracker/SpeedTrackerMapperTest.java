package com.mapohl.gtfsprocessor.speedtracker.domain.speedtracker;

import com.google.common.collect.Lists;
import com.mapohl.gtfsprocessor.speedtracker.domain.LinkPoint;
import com.mapohl.gtfsprocessor.speedtracker.domain.SpeedTracker;
import com.mapohl.gtfsprocessor.speedtracker.domain.SpeedTrackerMapper;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpeedTrackerMapperTest {

    private SpeedTrackerMapper testInstance = new SpeedTrackerMapper();

    @Test
    void map() {
        Row row = RowFactory.create(
                1,                                                      // ID
                10.5,                                                   // SPEED
                1337,                                                   // TRAVEL_TIME
                -101,                                                   // STATUS
                "2020-01-09 14:40:47",                                  // DATA_AS_OF
                1,                                                      // LINK_ID
                "40.78819,-73.79052 40.7870405,-73.77592 40.786440",    // LINK_POINTS
                "em...CoD",                                             // ENCODED_POLY_LINE
                "BBBBBBBB",                                             // ENCODED_POLY_LINE_LVLS
                "NYC-DOT-Region 10",                                    // OWNER
                4362247,                                                // TRANSCOM_ID
                "Queens",                                               // BOROUGH
                "Link description"                                      // LINK_NAME
                );

        Instant creationTime = Instant.parse("2020-01-09T14:40:47Z");
        SpeedTracker expectedEntity = SpeedTracker.builder()
                .speed(10.5)
                .travelTimeInSeconds(1337)
                .creationTime(creationTime)
                .linkId(1)
                .linkPoints(Lists.newArrayList(
                        new LinkPoint(40.78819, -73.79052),
                        new LinkPoint(40.7870405, -73.77592)
                ))
                .borough("Queens")
                .description("Link description")
                .build();
        SpeedTracker actualEntity = testInstance.map(row);

        assertEquals(expectedEntity, actualEntity);
        assertEquals(expectedEntity.getEntityId(), creationTime.getEpochSecond());
    }
}