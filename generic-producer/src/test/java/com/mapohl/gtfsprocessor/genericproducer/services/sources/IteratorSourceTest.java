package com.mapohl.gtfsprocessor.genericproducer.services.sources;

import com.google.common.collect.Lists;
import com.mapohl.gtfsprocessor.test.domain.TestEntity;
import com.mapohl.gtfsprocessor.test.domain.TestEntityMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static com.mapohl.gtfsprocessor.test.utils.TestUtils.createEntity;
import static org.junit.jupiter.api.Assertions.*;

class IteratorSourceTest {

    @Test
    public void testIteration() {
        List<String> input = Lists.newArrayList("0", "12");
        IteratorSource<String, TestEntity> testInstance = new IteratorSource(input.iterator(), 1, new TestEntityMapper());

        assertTrue(testInstance.hasNext());
        assertTrue(testInstance.upstreamHasNext());
        assertEquals(createEntity(0), testInstance.next());

        assertTrue(testInstance.hasNext());
        assertFalse(testInstance.upstreamHasNext());
        assertEquals(createEntity(1), testInstance.next());

        assertTrue(testInstance.hasNext());
        assertFalse(testInstance.upstreamHasNext());
        assertEquals(createEntity(2), testInstance.next());

        assertFalse(testInstance.upstreamHasNext());
        assertFalse(testInstance.hasNext());
        assertThrows(NoSuchElementException.class, () -> testInstance.next());
    }

}