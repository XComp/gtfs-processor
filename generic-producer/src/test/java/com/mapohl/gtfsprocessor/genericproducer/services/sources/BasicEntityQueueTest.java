package com.mapohl.gtfsprocessor.genericproducer.services.sources;

import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import com.mapohl.gtfsprocessor.test.domain.TestEntity;
import com.mapohl.gtfsprocessor.test.domain.TestEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static com.mapohl.gtfsprocessor.test.utils.TestUtils.createEntity;
import static org.junit.jupiter.api.Assertions.*;

class BasicEntityQueueTest {

    private BasicEntityQueue<String, TestEntity> testInstance;

    @BeforeEach
    public void initializeTestInstance() {
        this.testInstance = new BasicEntityQueue<>(new TestEntityMapper());
    }

    @Test
    public void testNoEntity() {
        assertTrue(testInstance.hasNext());
        assertNull(testInstance.peekNextEventTime());
        assertNull(testInstance.next());
    }

    @Test
    public void testOneEntity() {
        testInstance.add("0");

        TestEntity e = createEntity(0);
        assertTrue(testInstance.hasNext());
        assertEquals(e.getEventTime(), testInstance.peekNextEventTime());
        assertEquals(e, testInstance.next());
    }

    @Test
    public void testEndOfData() {
        assertTrue(testInstance.hasNext());

        testInstance.endOfDataReached();

        assertFalse(testInstance.hasNext());
        assertThrows(NoSuchElementException.class, () -> testInstance.next());
        assertThrows(IllegalStateException.class, () -> testInstance.add("0"));
    }

    @Test
    public void testDifferentOrder() {
        // added in reverse order
        testInstance.add("1");
        testInstance.add("0");

        // order gets fixed
        assertEquals(createEntity(0), testInstance.next());
        assertEquals(createEntity(1), testInstance.next());
    }

    @Test
    public void testInfiniteDataStreamDownstreamPropagation() {
        EntityMapper<String, TestEntity> entityMapper = new TestEntityMapper();
        BasicEntityQueue<String, TestEntity> downstreamTestInstance = new BasicEntityQueue(entityMapper);
        BasicEntityQueue<String, TestEntity> upstreamTestInstance = new BasicEntityQueue(entityMapper, downstreamTestInstance);

        assertTrue(upstreamTestInstance.hasNext());
        assertTrue(downstreamTestInstance.hasNext());

        upstreamTestInstance.add("0");

        assertTrue(upstreamTestInstance.hasNext());
        assertTrue(downstreamTestInstance.hasNext());

        TestEntity e0 = createEntity(0);
        assertEquals(e0, upstreamTestInstance.next());

        assertTrue(upstreamTestInstance.hasNext());
        assertTrue(downstreamTestInstance.hasNext());

        assertEquals(e0, downstreamTestInstance.next());

        assertTrue(upstreamTestInstance.hasNext());
        assertTrue(downstreamTestInstance.hasNext());

        assertNull(upstreamTestInstance.next());
        assertNull(downstreamTestInstance.next());

        // end-of-data is reached after all data was processed
        upstreamTestInstance.endOfDataReached();

        assertFalse(upstreamTestInstance.hasNext());
        assertFalse(downstreamTestInstance.hasNext());
    }

    @Test
    public void testLimitedDataStreamDownstreamPropagation() {
        EntityMapper<String, TestEntity> entityMapper = new TestEntityMapper();
        BasicEntityQueue<String, TestEntity> downstreamTestInstance = new BasicEntityQueue(entityMapper);
        BasicEntityQueue<String, TestEntity> upstreamTestInstance = new BasicEntityQueue(entityMapper, downstreamTestInstance);

        assertTrue(upstreamTestInstance.hasNext());
        assertTrue(downstreamTestInstance.hasNext());

        upstreamTestInstance.add("0");
        upstreamTestInstance.add("12");

        // end-of-data is reached before data is processed
        upstreamTestInstance.endOfDataReached();

        assertTrue(upstreamTestInstance.hasNext());
        assertTrue(downstreamTestInstance.hasNext());

        assertEquals(createEntity(0), upstreamTestInstance.next());
        assertEquals(createEntity(0), downstreamTestInstance.next());

        assertTrue(upstreamTestInstance.hasNext());
        assertTrue(downstreamTestInstance.hasNext());

        assertTrue(upstreamTestInstance.hasNext());
        assertTrue(downstreamTestInstance.hasNext());

        assertEquals(createEntity(1), upstreamTestInstance.next());
        assertEquals(createEntity(1), downstreamTestInstance.next());

        assertTrue(upstreamTestInstance.hasNext());
        assertTrue(downstreamTestInstance.hasNext());

        assertEquals(createEntity(2), upstreamTestInstance.next());
        assertEquals(createEntity(2), downstreamTestInstance.next());

        assertFalse(upstreamTestInstance.hasNext());
        assertFalse(downstreamTestInstance.hasNext());
    }

    @Test
    public void testLateDataPropagation() {
        EntityMapper<String, TestEntity> entityMapper = new TestEntityMapper();
        BasicEntityQueue<String, TestEntity> downstreamTestInstance = new BasicEntityQueue(entityMapper);
        BasicEntityQueue<String, TestEntity> upstreamTestInstance = new BasicEntityQueue(entityMapper, downstreamTestInstance);

        assertTrue(upstreamTestInstance.hasNext());
        assertTrue(downstreamTestInstance.hasNext());

        upstreamTestInstance.add("1");
        upstreamTestInstance.add("12");

        // end-of-data is reached before data is processed
        upstreamTestInstance.endOfDataReached();

        assertTrue(upstreamTestInstance.hasNext());
        assertTrue(downstreamTestInstance.hasNext());

        assertEquals(createEntity(1), upstreamTestInstance.next());
        assertEquals(createEntity(1), downstreamTestInstance.next());

        assertTrue(upstreamTestInstance.hasNext());
        assertTrue(downstreamTestInstance.hasNext());

        assertTrue(upstreamTestInstance.hasNext());
        assertTrue(downstreamTestInstance.hasNext());

        assertEquals(createEntity(1), upstreamTestInstance.next());
        assertEquals(createEntity(1), downstreamTestInstance.next());

        assertTrue(upstreamTestInstance.hasNext());
        assertTrue(downstreamTestInstance.hasNext());

        assertEquals(createEntity(2), upstreamTestInstance.next());
        assertEquals(createEntity(2), downstreamTestInstance.next());

        assertFalse(upstreamTestInstance.hasNext());
        assertFalse(downstreamTestInstance.hasNext());
    }

}