package com.mapohl.gtfsprocessor.test.domain;

import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import com.mapohl.gtfsprocessor.test.utils.TestUtils;

import java.util.List;
import java.util.stream.Collectors;

public class TestEntityMapper implements EntityMapper<String, TestEntity> {

    @Override
    public List<TestEntity> map(String input) {
        return input.chars()
                .mapToObj(c -> TestUtils.createEntity(Integer.parseInt(String.valueOf((char) c))))
                .collect(Collectors.toList());
    }
}
