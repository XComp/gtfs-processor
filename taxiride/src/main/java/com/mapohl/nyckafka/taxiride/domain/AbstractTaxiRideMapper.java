package com.mapohl.nyckafka.taxiride.domain;

import com.mapohl.nyckafka.common.Entity;
import com.mapohl.nyckafka.commonproducer.domain.EntityMapper;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public abstract class AbstractTaxiRideMapper<E extends Entity<?>> implements EntityMapper<String, E> {

    private final Map<Integer, NYCTaxiZone> nycTaxiZoneIndex;

    protected NYCTaxiZone getNYCTaxiZone(int zoneId) {
        return this.nycTaxiZoneIndex.get(zoneId);
    }
}
