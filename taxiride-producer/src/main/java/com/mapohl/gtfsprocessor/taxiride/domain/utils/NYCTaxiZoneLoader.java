package com.mapohl.gtfsprocessor.taxiride.domain.utils;

import com.mapohl.gtfsprocessor.taxiride.domain.Borough;
import com.mapohl.gtfsprocessor.taxiride.domain.NYCTaxiZone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class NYCTaxiZoneLoader {

    private static final String RESOURCE_NAME = "/nyc_taxi_zones.csv.gz";

    private static NYCTaxiZone fromLine(String line) {
        String[] values = line.split(",");

        return NYCTaxiZone.builder()
                .zoneId(Integer.parseInt(values[0]))
                .name(values[1])
                .borough(Borough.from(values[2]))
                .latitude(Double.parseDouble(values[3]))
                .longitude(Double.parseDouble(values[4]))
                .build();
    }

    public static Map<Integer, NYCTaxiZone> loadNYCTaxiZoneIndex() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new GZIPInputStream(NYCTaxiZoneLoader.class.getResourceAsStream(RESOURCE_NAME))))) {
            return reader.lines()
                    .map(NYCTaxiZoneLoader::fromLine)
                    .collect(Collectors.toMap(nycTaxiZone -> nycTaxiZone.getZoneId(), Function.identity()));
        } catch (IOException e) {
            throw new IllegalStateException("An error occurred while loading the NYC taxi zone data.", e);
        }
    }
}
