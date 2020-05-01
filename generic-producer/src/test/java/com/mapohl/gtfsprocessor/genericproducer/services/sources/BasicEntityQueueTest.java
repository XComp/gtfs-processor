package com.mapohl.gtfsprocessor.genericproducer.services.sources;

import com.google.common.base.Preconditions;
import com.mapohl.gtfsprocessor.genericproducer.domain.IdentityMapper;
import com.mapohl.gtfsprocessor.genericproducer.domain.TestEntity;
import com.mapohl.gtfsprocessor.genericproducer.utils.InstantBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BasicEntityQueueTest {

    private static InstantBuilder instantBuilder = new InstantBuilder();
    private BasicEntityQueue<TestEntity, TestEntity> testInstance;

    private static TestEntity createEntity(int hour) {
        Preconditions.checkArgument(hour >= 0 && hour < 24);
        return new TestEntity(hour, instantBuilder.hour(hour).build(), hour);
    }

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
        assertEquals(instantBuilder.build(), testInstance.peekNextEventTime());
        assertEquals(e, testInstance.next());
    }

    @Test
    public void testEndOfData() {
        assertTrue(testInstance.hasNext());

        testInstance.endOfDataReached();

        assertFalse(testInstance.hasNext());
        assertThrows(IllegalStateException.class, () -> testInstance.add(createEntity(0)));
    }

    @Test
    public void testDifferentOrder() {
        TestEntity e0 = createEntity(0);
        TestEntity e1 = createEntity(1);

        testInstance.add(e1);
        testInstance.add(e0);

        assertEquals(e0, testInstance.next());
        assertEquals(e1, testInstance.next());
    }

    @Test
    public void testDownstreamPropagation() {
        BasicEntityQueue<Integer, TestEntity> downstreamTestInstance = new BasicEntityQueue(new IdentityMapper());
        BasicEntityQueue<TestEntity, TestEntity> upstreamTestInstance = new BasicEntityQueue(new IdentityMapper(), downstreamTestInstance);

        assertTrue(upstreamTestInstance.hasNext());
        assertTrue(downstreamTestInstance.hasNext());

        TestEntity e0 = createEntity(0);
        upstreamTestInstance.add(e0);

        assertTrue(upstreamTestInstance.hasNext());
        assertTrue(downstreamTestInstance.hasNext());

        assertEquals(e0, upstreamTestInstance.next());
        assertEquals(e0, downstreamTestInstance.next());

        assertTrue(upstreamTestInstance.hasNext());
        assertTrue(downstreamTestInstance.hasNext());

        upstreamTestInstance.endOfDataReached();

        assertFalse(upstreamTestInstance.hasNext());
        assertFalse(downstreamTestInstance.hasNext());
    }

}