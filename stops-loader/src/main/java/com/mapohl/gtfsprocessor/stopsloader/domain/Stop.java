package com.mapohl.gtfsprocessor.stopsloader.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stop implements Serializable {

    private String arrivalTime;

    private String departureTime;

    private String stopId;

    private int stopSequence;

    private String name;

    private double latitude;

    private double longitude;

    private int secondOfDay;
}
