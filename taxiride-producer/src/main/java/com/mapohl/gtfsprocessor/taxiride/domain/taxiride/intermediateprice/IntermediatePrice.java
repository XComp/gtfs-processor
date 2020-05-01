package com.mapohl.gtfsprocessor.taxiride.domain.taxiride.intermediateprice;

import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntermediatePrice implements Entity<Integer> {

    private Integer entityId;

    private Instant eventTime;

    private double price;
}
