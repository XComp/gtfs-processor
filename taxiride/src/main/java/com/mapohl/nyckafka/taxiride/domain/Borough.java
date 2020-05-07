package com.mapohl.nyckafka.taxiride.domain;

public enum Borough {
    Bronx,
    Brooklyn,
    Manhattan,
    Queens,
    StatenIsland,
    Others;

    public static Borough from(String str) {
        try {
            return Borough.valueOf(str);
        } catch (IllegalArgumentException e) {
            // unknown borough was passed
            return Borough.Others;
        }
    }
}
