package com.mapohl.nyckafka.test.domain;

import com.mapohl.nyckafka.genericproducer.domain.EntityMapper;
import com.mapohl.nyckafka.test.utils.TestUtils;

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
