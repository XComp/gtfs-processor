package com.mapohl.gtfsprocessor.taxiride.domain;

import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import org.apache.commons.lang3.RandomUtils;
import org.apache.spark.sql.Row;

public class TaxiRideMapper implements EntityMapper<TaxiRide> {

    // 0 - VendorID
    // 1 - tpep_pickup_datetime
    // 2 - tpep_dropoff_datetime
    // 3 - passenger_count
    // 4 - trip_distance
    // 5 - RatecodeID
    // 6 - store_and_fwd_flag
    // 7 - PULocationID
    // 8 - DOLocationID
    // 9 - payment_type
    // 10 - fare_amount
    // 11 - extra
    // 12 - mta_tax
    // 13 - tip_amount
    // 14 - tolls_amount
    // 15 - improvement_surcharge
    // 16 - total_amount
    // 17 - congestion_surcharge
    @Override
    public TaxiRide map(Row row) {
        return TaxiRide.builder()
                .id(RandomUtils.nextLong())
                .pickupTimeStr(row.getTimestamp(1).toString())
                .dropOffTimeStr(row.getTimestamp(2).toString())
                .passengerCount(row.getInt(3))
                .distance(row.getDouble(4))
                .pickupZoneId(row.getInt(7))
                .dropOffZoneId(row.getInt(8))
                .paymentTypeId(row.getInt(9))
                .tollAmount(row.getDouble(14))
                .totalAmount(row.getDouble(16))
                .build();
    }
}
