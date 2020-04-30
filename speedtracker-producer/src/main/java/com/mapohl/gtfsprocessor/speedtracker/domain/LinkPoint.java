package com.mapohl.gtfsprocessor.speedtracker.domain;

import com.mapohl.gtfsprocessor.genericproducer.domain.EntityData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LinkPoint implements EntityData {

    private double latitude;
    private double longitude;
}
