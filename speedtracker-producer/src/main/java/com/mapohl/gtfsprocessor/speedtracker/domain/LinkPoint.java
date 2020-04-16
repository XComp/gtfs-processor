package com.mapohl.gtfsprocessor.speedtracker.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LinkPoint implements Serializable {

    private double latitude;
    private double longitude;
}
