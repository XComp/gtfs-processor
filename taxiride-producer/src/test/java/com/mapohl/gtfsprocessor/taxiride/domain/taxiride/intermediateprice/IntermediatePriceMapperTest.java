package com.mapohl.gtfsprocessor.taxiride.domain.taxiride.intermediateprice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mapohl.gtfsprocessor.taxiride.domain.utils.NYCTaxiRideUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IntermediatePriceMapperTest {

    private IntermediatePriceMapper testInstance = new IntermediatePriceMapper();

    @Test
    void testJson() throws JsonProcessingException {
        System.out.println(new ObjectMapper().writeValueAsString(IntermediatePrice.builder()
                .price(1.0)
                .entityId(1)
                .eventTime(Instant.now())
                .build()));
    }

    @DisplayName("Parse String to create IntermediatePriceMapper entity")
    @Test
    void mapStringToEntity() {
        String[] values = new String[]{
                "1",                    // 0 - VendorID
                "2019-01-01 00:10:00",  // 1 - tpep_pickup_datetime
                "2019-01-01 00:20:00",  // 2 - tpep_dropoff_datetime
                "1",                    // 3 - passenger_count
                "1.5",                  // 4 - trip_distance
                "1",                    // 5 - RatecodeID
                "N",                    // 6 - store_and_fwd_flag
                "151",                  // 7 - PULocationID
                "239",                  // 8 - DOLocationID
                "1",                    // 9 - payment_type
                "7",                    // 10 - fare_amount
                "0.5",                  // 11 - extra
                "0.5",                  // 12 - mta_tax
                "1.65",                 // 13 - tip_amount
                "0.0",                  // 14 - tolls_amount
                "0.3",                  // 15 - improvement_surcharge
                "10.0",                 // 16 - total_amount
                ""                      // 17 - congestion_surcharge
        };

        String line = String.join(",", values);
        IntermediatePrice actualEntity = testInstance.map(line).get(0);
        IntermediatePrice expectedEntity = IntermediatePrice.builder()
                .entityId(line.hashCode())
                .price(5.0)
                .eventTime(NYCTaxiRideUtils.parse("2019-01-01 00:15:00"))
                .build();

        assertEquals(expectedEntity, actualEntity);
    }

}