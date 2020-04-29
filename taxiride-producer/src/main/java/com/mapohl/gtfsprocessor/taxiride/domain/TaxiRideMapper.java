package com.mapohl.gtfsprocessor.taxiride.domain;

import org.apache.commons.lang3.RandomUtils;

import java.util.Map;

public class TaxiRideMapper extends AbstractTaxiRideMapper<TaxiRide> {

    public TaxiRideMapper(Map<Integer, NYCTaxiZone> nycTaxiZoneIndex) {
        super(nycTaxiZoneIndex);
    }

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
    public TaxiRide map(String line) {
        String[] values = line.split(",");
        return TaxiRide.builder()
                .id(RandomUtils.nextLong())
                .pickupTimeStr(values[1])
                .dropOffTimeStr(values[2])
                .passengerCount(Integer.parseInt(values[3]))
                .distance(Double.parseDouble(values[4]))
                .pickupZone(this.getNYCTaxiZone(Integer.parseInt(values[7])))
                .dropOffZone(this.getNYCTaxiZone(Integer.parseInt(values[8])))
                .paymentTypeId(Integer.parseInt(values[9]))
                .tollAmount(Double.parseDouble(values[14]))
                .totalAmount(Double.parseDouble(values[16]))
                .build();
    }
}
