package com.mapohl.gtfsprocessor.genericproducer.services.sources;

import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;
import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
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
        if (!this.entityQueue.isEmpty()) {
            return this.entityQueue.peek().getEventTime();
        }

        Iterator<I> inputIterator = this.inputQueue.iterator();
        while (inputIterator.hasNext()) {
            I input = inputIterator.next();
            List<E> entities = this.entityMapper.map(input);
            if (!entities.isEmpty()) {
                return entities.get(0).getEventTime();
            }
        }

        return null;
    }

    protected boolean upstreamHasNext() {
        return this.hasData;
    }

    @Override
    public boolean hasNext() {
        return this.upstreamHasNext() || !this.isEmpty();
    }

    @Override
    public boolean isEmpty() {
        return this.peekNextEventTime() == null;
    }

    @Override
    public E next() {
        while (true) {
            if (!this.entityQueue.isEmpty()) {
                return this.entityQueue.poll();
            }

            if (this.inputQueue.isEmpty()) {
                if (this.upstreamHasNext()) {
                    return null;
                }

                this.propagateEndOfDataDownstream();
                throw new NoSuchElementException();
            }

            I input = this.inputQueue.poll();
            this.propagateInputToDownstreamQueues(input);
            if (this.inputQueue.isEmpty() && !this.upstreamHasNext()) {
                this.propagateEndOfDataDownstream();
            }

            this.entityQueue.addAll(this.entityMapper.map(input));
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
