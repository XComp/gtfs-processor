package com.mapohl.gtfsprocessor.genericproducer.domain;

public class TestEntityMapper implements EntityMapper<String, TestEntity> {

    @Override
    public TestEntity map(String input) {
        String[] values = input.split(",");

        return new TestEntity(Integer.parseInt(values[0]), values[1], Integer.parseInt(values[2]));
    }
}
