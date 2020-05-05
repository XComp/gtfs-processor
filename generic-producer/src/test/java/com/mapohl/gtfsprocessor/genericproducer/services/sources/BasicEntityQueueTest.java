package com.mapohl.gtfsprocessor.genericproducer.services.sources;

import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import com.mapohl.gtfsprocessor.genericproducer.domain.IdentityMapper;
import com.mapohl.gtfsprocessor.test.domain.TestEntity;
import com.mapohl.gtfsprocessor.test.domain.TestEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static com.mapohl.gtfsprocessor.test.utils.TestUtils.createEntity;
import static org.junit.jupiter.api.Assertions.*;

class BasicEntityQueueTest {

    private BasicEntityQueue<TestEntity, TestEntity> testInstance;

    @BeforeEach
    public void initializeTestInstance() {
        this.testInstance = new BasicEntityQueue<>(new IdentityMapper<>());
    }

    @Test
    public void testNoEntity() {
        assertTrue(testInstance.hasNext());
        assertNull(testInstance.peekNextEventTime());
        assertNull(testInstance.next());
    }

    @Test
    public void testOneEntity() {
        TestEntity e = createEntity(0);
        testInstance.add(e);

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
        assertThrows(IllegalStateException.class, () -> testInstance.add(createEntity(0)));
    }

    @Test
    public void testDifferentOrder() {
        TestEntity e0 = createEntity(0);
        TestEntity e1 = createEntity(1);

        // added in reverse order
        testInstance.add(e1);
        testInstance.add(e0);

        // order gets fixed
        assertEquals(e0, testInstance.next());
        assertEquals(e1, testInstance.next());
    }

    @Test
    public void testDownstreamPropagation() {
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

        upstreamTestInstance.endOfDataReached();

        assertFalse(upstreamTestInstance.hasNext());
        assertFalse(downstreamTestInstance.hasNext());
    }

    @Test
    public void testDownstreamPropagation2() {
        EntityMapper<String, TestEntity> entityMapper = new TestEntityMapper();
        BasicEntityQueue<String, TestEntity> downstreamTestInstance = new BasicEntityQueue(entityMapper);
        BasicEntityQueue<String, TestEntity> upstreamTestInstance = new BasicEntityQueue(entityMapper, downstreamTestInstance);

        assertTrue(upstreamTestInstance.hasNext());
        assertTrue(downstreamTestInstance.hasNext());

        upstreamTestInstance.add("0");
        upstreamTestInstance.add("12");

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

}