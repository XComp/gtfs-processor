package com.mapohl.nyckafka.speedtracker.domain;

import com.mapohl.nyckafka.common.EntityData;
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
