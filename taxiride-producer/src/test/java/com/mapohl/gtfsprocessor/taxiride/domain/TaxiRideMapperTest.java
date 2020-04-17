package com.mapohl.gtfsprocessor.taxiride.domain;

import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaxiRideMapperTest {

    private TaxiRideMapper testInstance = new TaxiRideMapper();

    @Test
    void map() {
        String pickupTimeStr = "2019-01-01 00:46:40.0";
        String dropOffTimeStr = "2019-01-01 00:53:20.0";

        Row row = RowFactory.create(
                1,                                      // 0 - VendorID
                Timestamp.valueOf(pickupTimeStr),       // 1 - tpep_pickup_datetime
                Timestamp.valueOf(dropOffTimeStr),      // 2 - tpep_dropoff_datetime
                1,                                      // 3 - passenger_count
                1.5,                                    // 4 - trip_distance
                1,                                      // 5 - RatecodeID
                "N",                                    // 6 - store_and_fwd_flag
                151,                                    // 7 - PULocationID
                239,                                    // 8 - DOLocationID
                1,                                      // 9 - payment_type
                7,                                      // 10 - fare_amount
                0.5,                                    // 11 - extra
                0.5,                                    // 12 - mta_tax
                1.65,                                   // 13 - tip_amount
                0.0,                                    // 14 - tolls_amount
                0.3,                                    // 15 - improvement_surcharge
                9.95,                                   // 16 - total_amount
                null                                    // 17 - congestion_surcharge
        );

        TaxiRide actualEntity = testInstance.map(row);
        TaxiRide expectedEntity = TaxiRide.builder()
                .pickupTimeStr(pickupTimeStr)
                .dropOffTimeStr(dropOffTimeStr)
                .id(actualEntity.getId())
                .passengerCount(1)
                .distance(1.5)
                .pickupZoneId(151)
                .dropOffZoneId(239)
                .paymentTypeId(1)
                .tollAmount(0.0)
                .totalAmount(9.95)
                .build();

        assertEquals(expectedEntity, actualEntity);
    }
}