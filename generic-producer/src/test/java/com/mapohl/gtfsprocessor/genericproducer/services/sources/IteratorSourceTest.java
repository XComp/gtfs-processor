package com.mapohl.gtfsprocessor.genericproducer.services.sources;

import com.google.common.collect.Lists;
import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import com.mapohl.gtfsprocessor.genericproducer.domain.TestEntity;
import com.mapohl.gtfsprocessor.genericproducer.utils.InstantBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class IteratorSourceTest {

    private static InstantBuilder instantBuilder = new InstantBuilder(2019, 1, 2, 1, 0, 0);
    private static List<Integer> input = Lists.newArrayList();
    private static List<TestEntity> expectedOutput = Lists.newArrayList();
    private static int entityLimit = 4;
    private IteratorSource<TestEntity, TestEntity> testInstance;

    @BeforeAll
    public static void setupData() {
        for (int i = 0; i < entityLimit; i++) {
            input.add(i);
            expectedOutput.add(new TestEntity(i, instantBuilder.hour(i).build(), i));
        }
    }

    @Test
    public void testIteration() {
        this.testInstance = new IteratorSource(input.iterator(), new TestEntityMapper());

        for (int i = 0; i < entityLimit; i++) {
            assertEquals(expectedOutput.get(i).getEventTime(), testInstance.peekNextEventTime());
            assertTrue(testInstance.hasNext());
            assertEquals(expectedOutput.get(i), testInstance.next());
        }

        assertFalse(testInstance.hasNext());
        assertThrows(NoSuchElementException.class, () -> testInstance.next());
    }

    private static class TestEntityMapper implements EntityMapper<Integer, TestEntity> {

        @Override
        public TestEntity map(Integer i) {
            return new TestEntity(i, instantBuilder.hour(i).build(), i);
        }
    }

}