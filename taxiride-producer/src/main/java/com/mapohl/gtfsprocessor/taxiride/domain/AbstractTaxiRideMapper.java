package com.mapohl.gtfsprocessor.taxiride.domain;

import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;
import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public abstract class AbstractTaxiRideMapper<E extends Entity<?>> implements EntityMapper<String, E> {

    private final Map<Integer, NYCTaxiZone> nycTaxiZoneIndex;

    protected NYCTaxiZone getNYCTaxiZone(int zoneId) {
        return this.nycTaxiZoneIndex.get(zoneId);
    }
}
