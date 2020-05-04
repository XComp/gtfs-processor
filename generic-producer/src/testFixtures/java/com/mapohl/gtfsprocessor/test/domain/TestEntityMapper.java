package com.mapohl.gtfsprocessor.test.domain;

import com.google.common.collect.Lists;
import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;

import java.util.List;

public class TestEntityMapper implements EntityMapper<String, TestEntity> {

    @Override
    public List<TestEntity> map(String input) {
        String[] values = input.split(",");

        return Lists.newArrayList(new TestEntity(Integer.parseInt(values[0]), values[1], Integer.parseInt(values[2])));
    }
}
