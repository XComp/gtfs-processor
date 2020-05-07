package com.mapohl.nyckafka.taxiride.domain.taxiride.intermediateprice;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.mapohl.nyckafka.common.TestUtils.INSTANT_BUILDER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PerSecondsIntermediatePriceMapperTest {

    @Test
    public void testModulo0() {
        PerSecondsIntermediatePriceMapper testInstance = new PerSecondsIntermediatePriceMapper(1);
        List<IntermediatePrice> actualEvents = testInstance.createEvents(0,
                INSTANT_BUILDER.second(0).build(), INSTANT_BUILDER.second(3).build(), 9.0);

        List<IntermediatePrice> expectedEvents = Lists.newArrayList(
                IntermediatePrice.builder().entityId(0).price(3.0).eventTime(INSTANT_BUILDER.second(1).build()).build(),
                IntermediatePrice.builder().entityId(0).price(6.0).eventTime(INSTANT_BUILDER.second(2).build()).build()
        );

        assertEquals(expectedEvents, actualEvents);
    }

    @Test
    public void testModuloNon0() {
        PerSecondsIntermediatePriceMapper testInstance = new PerSecondsIntermediatePriceMapper(30);
        List<IntermediatePrice> actualEvents = testInstance.createEvents(0,
                INSTANT_BUILDER.second(0).build(), INSTANT_BUILDER.second(45).build(), 9.0);

        List<IntermediatePrice> expectedEvents = Lists.newArrayList(
                IntermediatePrice.builder().entityId(0).price(6.0).eventTime(INSTANT_BUILDER.second(30).build()).build()
        );

        assertEquals(expectedEvents, actualEvents);
    }

    @Test
    public void testNoEvents() {
        PerSecondsIntermediatePriceMapper testInstance = new PerSecondsIntermediatePriceMapper(30);
        List<IntermediatePrice> actualEvents = testInstance.createEvents(0,
                INSTANT_BUILDER.second(0).build(), INSTANT_BUILDER.second(10).build(), 9.0);

        assertTrue(actualEvents.isEmpty());
    }

}