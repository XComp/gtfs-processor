package com.mapohl.gtfsprocessor.stopsloader.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stop {

    private String arrivalTime;

    private String departureTime;

    private String stopId;

    private int stopSequence;

    private String name;

    private double latitude;

    private double longitude;

    private int secondOfDay;
}
