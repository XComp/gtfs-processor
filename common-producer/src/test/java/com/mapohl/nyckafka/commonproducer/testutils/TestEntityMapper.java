package com.mapohl.nyckafka.commonproducer.testutils;

import com.mapohl.nyckafka.common.TestEntity;
import com.mapohl.nyckafka.common.TestUtils;
import com.mapohl.nyckafka.commonproducer.domain.EntityMapper;

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
