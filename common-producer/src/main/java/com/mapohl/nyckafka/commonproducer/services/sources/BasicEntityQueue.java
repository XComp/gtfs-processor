package com.mapohl.nyckafka.commonproducer.services.sources;

import com.google.common.base.Preconditions;
import com.mapohl.nyckafka.common.Entity;
import com.mapohl.nyckafka.common.TimePeriod;
import com.mapohl.nyckafka.commonproducer.domain.EntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

@Slf4j
public class BasicEntityQueue<I, E extends Entity<?>> implements EntityQueue<I, E> {

    private final EntityMapper<I, E> entityMapper;
    private final Queue<I> inputQueue;
    private final Queue<E> entityQueue;
    private final EntityQueue<I, ? extends Entity<?>>[] downstreamEntityQueues;
    private boolean hasData = true;

    public BasicEntityQueue(EntityMapper<I, E> entityMapper, EntityQueue<I, ? extends Entity<?>>... downstreamEntityQueues) {
        this.entityMapper = entityMapper;

        this.inputQueue = new PriorityBlockingQueue<>(100, new InputComparator(entityMapper));
        this.entityQueue = new PriorityBlockingQueue<>();

        this.downstreamEntityQueues = downstreamEntityQueues;
    }

    private void propagateInputToDownstreamQueues(I input) {
        for (EntityQueue<I, ?> downstreamEntityQueue : this.downstreamEntityQueues) {
            downstreamEntityQueue.add(input);
        }
    }

    private void propagateEndOfDataDownstream() {
        for (EntityQueue<I, ?> downstreamEntityQueue : this.downstreamEntityQueues) {
            downstreamEntityQueue.endOfDataReached();
        }
    }

    @Override
    public void add(I input) {
        if (!this.hasNext()) {
            throw new IllegalStateException("The end of data was already reached.");
        }

        if (input == null) {
            this.hasData = false;

            if (this.inputQueue.isEmpty()) {
                this.propagateEndOfDataDownstream();
            }
        } else {
            this.inputQueue.add(input);
        }
    }

    @Override
    public Instant peekNextEventTime() {
        Instant nextEventTime = null;
        if (!this.entityQueue.isEmpty()) {
            nextEventTime = this.entityQueue.peek().getEventTime();
        }

        Iterator<I> inputIterator = this.inputQueue.iterator();
        while (inputIterator.hasNext()) {
            I input = inputIterator.next();
            List<E> entities = this.entityMapper.map(input);
            if (!entities.isEmpty() &&
                    (nextEventTime == null || !nextEventTime.isBefore(entities.get(0).getEventTime()))) {
                return entities.get(0).getEventTime();
            }
        }

        return nextEventTime;
    }

    protected boolean upstreamHasNext() {
        return this.hasData;
    }

    @Override
    public boolean hasNext() {
        return this.upstreamHasNext() || !this.isEmpty();
    }

    @Override
    public boolean hasNext(TimePeriod timePeriod) {
        Instant peekedTime = this.peekNextEventTime();
        return peekedTime != null ? !timePeriod.timeIsAfterTimePeriod(peekedTime) : false;
    }

    @Override
    public boolean isEmpty() {
        return this.peekNextEventTime() == null;
    }

    @Override
    public E next(TimePeriod timePeriod) {
        Preconditions.checkNotNull(timePeriod);

        this.loadInputs(timePeriod);

        if (!this.entityQueue.isEmpty()) {
            if (timePeriod.timeIsAfterTimePeriod(this.entityQueue.peek().getEventTime())) {
                return null;
            }

            return this.entityQueue.poll();
        }

        if (this.inputQueue.isEmpty() && !this.upstreamHasNext()) {
            throw new NoSuchElementException();
        }

        return null;
    }

    private void loadInputs(TimePeriod timePeriod) {
        while (true) {
            if (this.inputQueue.isEmpty()) {
                if (!this.upstreamHasNext()) {
                    this.propagateEndOfDataDownstream();
                }

                return;
            }

            I input = this.inputQueue.peek();
            List<E> entities = this.entityMapper.map(input);
            if (!entities.isEmpty()) {
                if (timePeriod.timeIsAfterTimePeriod(entities.get(0).getEventTime())) {
                    return;
                }
            }

            this.propagateInputToDownstreamQueues(input);
            this.entityQueue.addAll(entities);
            this.inputQueue.remove();
        }
    }

    protected int cachedInputCount() {
        return this.inputQueue.size();
    }

    @RequiredArgsConstructor
    private class InputComparator implements Comparator<I> {

        private final EntityMapper<I, E> entityMapper;

        @Override
        public int compare(I input1, I input2) {
            List<E> entities1 = this.entityMapper.map(input1);
            List<E> entities2 = this.entityMapper.map(input2);

            if (entities1.isEmpty() && entities1.isEmpty()) {
                return 0;
            } else if (entities1.isEmpty()) {
                return 1;
            } else if (entities2.isEmpty()) {
                return -1;
            }

            return entities1.get(0).compareTo(entities2.get(0));
        }
    }
}
