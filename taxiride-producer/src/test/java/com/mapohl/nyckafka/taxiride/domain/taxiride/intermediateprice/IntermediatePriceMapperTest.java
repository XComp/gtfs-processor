package com.mapohl.nyckafka.taxiride.domain.taxiride.intermediateprice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.JacksonUtils;

import java.util.List;

import static com.mapohl.nyckafka.test.utils.TestUtils.createInstant;
import static com.mapohl.nyckafka.test.utils.TestUtils.createInstantStr;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IntermediatePriceMapperTest {

    private IntermediatePriceMapper testInstance = new IntermediatePriceMapper(2);

    private static String createTestLine(int pickupHour, int pickupMinute, int pickupSecond, int dropoffHour, int dropoffMinute, int dropOffSecond, double totalAmount) {
        String[] values = new String[]{
                "1",                                            // 0 - VendorID
                // 1 - tpep_pickup_datetime
                createInstantStr(pickupHour, pickupMinute, pickupSecond),
                // 2 - tpep_dropoff_datetime
                createInstantStr(dropoffHour, dropoffMinute, dropOffSecond),
                "1",                                            // 3 - passenger_count
                "1.5",                                          // 4 - trip_distance
                "1",                                            // 5 - RatecodeID
                "N",                                            // 6 - store_and_fwd_flag
                "151",                                          // 7 - PULocationID
                "239",                                          // 8 - DOLocationID
                "1",                                            // 9 - payment_type
                "7",                                            // 10 - fare_amount
                "0.5",                                          // 11 - extra
                "0.5",                                          // 12 - mta_tax
                "1.65",                                         // 13 - tip_amount
                "0.0",                                          // 14 - tolls_amount
                "0.3",                                          // 15 - improvement_surcharge
                String.valueOf(totalAmount),                    // 16 - total_amount
                ""                                              // 17 - congestion_surcharge
        };

        return String.join(",", values);
    }

    @Test
    void testJson() throws JsonProcessingException {
        String inputLine = createTestLine(0, 0, 0, 0, 10, 0, 10.0);
        ObjectMapper objMapper = JacksonUtils.enhancedObjectMapper();

        String json = objMapper.writeValueAsString(testInstance.map(inputLine));
        objMapper.readValue(json, List.class);
    }

    @DisplayName("Parse String to create multiple IntermediatePriceMapper entities")
    @Test
    void mapStringToMultipleEntities() {
        String inputLine = createTestLine(0, 0, 0, 0, 30, 0, 9.0);
        List<IntermediatePrice> expectedEntities = Lists.newArrayList(
                IntermediatePrice.builder()
                        .entityId(inputLine.hashCode())
                        .price(3.0)
                        .eventTime(createInstant(0, 10))
                        .build(),
                IntermediatePrice.builder()
                        .entityId(inputLine.hashCode())
                        .price(6.0)
                        .eventTime(createInstant(0, 20))
                        .build()
        );

        assertEquals(expectedEntities, testInstance.map(inputLine));
    }

    @DisplayName("Parse String to create only one IntermediatePriceMapper entity because of the time between pickup and dropoff being too short")
    @Test
    void mapStringToOnlyOneEntity() {
        for (int i = 0; i < 3; i++) {
            String inputLine = createTestLine(0, 0, 0, 0, 0, i, 10.0);
            List<IntermediatePrice> expectedEntities = Lists.newArrayList(
                    IntermediatePrice.builder()
                            .entityId(inputLine.hashCode())
                            .price(10.0)
                            .eventTime(createInstant(0, 0, i))
                            .build()
            );
            assertTrue(testInstance.map(inputLine).isEmpty());
        }
    }

    @DisplayName("Parse String to test whether multiple entities are created when providing the minimum duration")
    @Test
    void mapStringToMultipleEntitiesCloseToThreshold() {
        String inputLine = createTestLine(0, 0, 0, 0, 0, 3, 9.0);
        List<IntermediatePrice> expectedEntities = Lists.newArrayList(
                IntermediatePrice.builder()
                        .entityId(inputLine.hashCode())
                        .price(3.0)
                        .eventTime(createInstant(0, 0, 1))
                        .build(),
                IntermediatePrice.builder()
                        .entityId(inputLine.hashCode())
                        .price(6.0)
                        .eventTime(createInstant(0, 0, 2))
                        .build()
        );

        assertEquals(expectedEntities, testInstance.map(inputLine));
    }

}