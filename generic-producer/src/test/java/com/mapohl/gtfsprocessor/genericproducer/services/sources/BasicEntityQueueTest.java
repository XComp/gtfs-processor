package com.mapohl.gtfsprocessor.genericproducer.services.sources;

import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import com.mapohl.gtfsprocessor.genericproducer.utils.TimePeriod;
import com.mapohl.gtfsprocessor.test.domain.NoEntitiesMapper;
import com.mapohl.gtfsprocessor.test.domain.TestEntity;
import com.mapohl.gtfsprocessor.test.domain.TestEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static com.mapohl.gtfsprocessor.test.utils.TestUtils.createEntity;
import static com.mapohl.gtfsprocessor.test.utils.TestUtils.createTimePeriod;
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
        assertNull(testInstance.next(createTimePeriod(0, 0, 1, 0)));
    }

    @Test
    public void testOneEntity() {
        testInstance.add("1");

        TimePeriod timePeriod = createTimePeriod(0, 15, 0, 45);
        TestEntity e = createEntity(1);
        assertTrue(testInstance.hasNext());
        assertEquals(e.getEventTime(), testInstance.peekNextEventTime());

        assertNull(testInstance.next(timePeriod));
        assertEquals(e, testInstance.next(timePeriod.next()));
    }

    @Test
    public void testEndOfData() {
        assertTrue(testInstance.hasNext());

        testInstance.endOfDataReached();

        assertFalse(testInstance.hasNext());
        assertThrows(NoSuchElementException.class, () -> testInstance.next(createTimePeriod(0, 0, 0, 1)));
        assertThrows(IllegalStateException.class, () -> testInstance.add("0"));
    }

    @Test
    public void testNullHandling() {
        assertThrows(NullPointerException.class, () -> testInstance.next(null));
    }

    @Test
    public void testDifferentOrder() {
        // added in reverse order
        testInstance.add("1");
        testInstance.add("0");

        // order gets fixed
        TimePeriod timePeriod = createTimePeriod(0, 0, 1, 30);
        assertEquals(createEntity(0), testInstance.next(timePeriod));
        assertEquals(createEntity(1), testInstance.next(timePeriod));
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

        TimePeriod timePeriod = createTimePeriod(0, 0, 0, 5);
        TestEntity e0 = createEntity(0);
        assertEquals(e0, upstreamTestInstance.next(timePeriod));

        assertTrue(upstreamTestInstance.hasNext());
        assertTrue(downstreamTestInstance.hasNext());

        assertEquals(e0, downstreamTestInstance.next(timePeriod));

        assertTrue(upstreamTestInstance.hasNext());
        assertTrue(downstreamTestInstance.hasNext());

        assertNull(upstreamTestInstance.next(timePeriod));
        assertNull(downstreamTestInstance.next(timePeriod));

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

        TimePeriod timePeriod = createTimePeriod(0, 0, 1, 30);
        assertEquals(createEntity(0), upstreamTestInstance.next(timePeriod));
        assertEquals(createEntity(0), downstreamTestInstance.next(timePeriod));

        assertTrue(upstreamTestInstance.hasNext());
        assertTrue(downstreamTestInstance.hasNext());

        assertEquals(createEntity(1), upstreamTestInstance.next(timePeriod));
        assertEquals(createEntity(1), downstreamTestInstance.next(timePeriod));

        assertTrue(upstreamTestInstance.hasNext());
        assertTrue(downstreamTestInstance.hasNext());

        assertNull(upstreamTestInstance.next(timePeriod));
        assertNull(downstreamTestInstance.next(timePeriod));

        assertTrue(upstreamTestInstance.hasNext());
        assertTrue(downstreamTestInstance.hasNext());

        timePeriod = timePeriod.next();
        assertEquals(createEntity(2), upstreamTestInstance.next(timePeriod));
        assertEquals(createEntity(2), downstreamTestInstance.next(timePeriod));

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

        TimePeriod timePeriod = createTimePeriod(1, 0, 2, 0);
        assertEquals(createEntity(1), upstreamTestInstance.next(timePeriod));
        assertEquals(createEntity(1), downstreamTestInstance.next(timePeriod));

        assertTrue(upstreamTestInstance.hasNext());
        assertTrue(downstreamTestInstance.hasNext());

        assertEquals(createEntity(1), upstreamTestInstance.next(timePeriod));
        assertEquals(createEntity(1), downstreamTestInstance.next(timePeriod));

        assertTrue(upstreamTestInstance.hasNext());
        assertTrue(downstreamTestInstance.hasNext());

        assertNull(upstreamTestInstance.next(timePeriod));
        assertNull(downstreamTestInstance.next(timePeriod));

        assertTrue(upstreamTestInstance.hasNext());
        assertTrue(downstreamTestInstance.hasNext());

        timePeriod = timePeriod.next();
        assertEquals(createEntity(2), upstreamTestInstance.next(timePeriod));
        assertEquals(createEntity(2), downstreamTestInstance.next(timePeriod));

        assertFalse(upstreamTestInstance.hasNext());
        assertFalse(downstreamTestInstance.hasNext());
    }

    @Test
    public void testNoEntitiesInputPropagation() {
        BasicEntityQueue<String, TestEntity> downstreamTestInstance = new BasicEntityQueue(new TestEntityMapper());
        BasicEntityQueue<String, TestEntity> upstreamTestInstance = new BasicEntityQueue(new NoEntitiesMapper(), downstreamTestInstance);

        assertTrue(upstreamTestInstance.hasNext());
        assertTrue(downstreamTestInstance.hasNext());

        upstreamTestInstance.add("1");

        // end-of-data is reached before data is processed
        upstreamTestInstance.endOfDataReached();

        assertFalse(upstreamTestInstance.hasNext());
        assertTrue(downstreamTestInstance.hasNext());

        TimePeriod timePeriod = createTimePeriod(1, 0, 1, 30);
        assertThrows(NoSuchElementException.class, () -> upstreamTestInstance.next(timePeriod));
        assertEquals(createEntity(1), downstreamTestInstance.next(timePeriod));

        assertFalse(upstreamTestInstance.hasNext());
        assertFalse(downstreamTestInstance.hasNext());
    }

}