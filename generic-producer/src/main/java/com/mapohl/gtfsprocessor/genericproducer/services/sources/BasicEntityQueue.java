package com.mapohl.gtfsprocessor.genericproducer.services.sources;

import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;
import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

@Slf4j
public class BasicEntityQueue<I, E extends Entity<?>> extends AbstractEntitySource<I, E> implements EntityQueue<I, E> {

    private final Queue<I> entityQueue;

    private boolean hasData = true;

    public BasicEntityQueue(EntityMapper<I, E> entityMapper, EntityQueue<I, ? extends Entity<?>>... downstreamEntityQueues) {
        super(entityMapper, downstreamEntityQueues);

        this.entityQueue = new PriorityBlockingQueue<I>(100, entityMapper.createComparator());
    }

    @Override
    public void add(I input) {
        if (!this.hasNext()) {
            throw new IllegalStateException("The end of data was already reached.");
        }

        if (input == null) {
            this.hasData = false;

            if (!this.hasNext()) {
                this.propagateEndOfDataDownstream();
            }
        } else {
            this.entityQueue.offer(input);
        }
    }

    private E peek() {
        return this.mapOrNull(this.entityQueue.peek());
    }

    private E poll() {
        I input = this.entityQueue.poll();
        this.propagateInputToDownstreamQueues(input);
        if (!this.hasNext()) {
            this.propagateEndOfDataDownstream();
        }

        return this.mapOrNull(input);
    }

    @Override
    public Instant peekNextEventTime() {
        return this.isEmpty() ? null : this.peek().getEventTime();
    }

    @Override
    public boolean hasNext() {
        return !this.isEmpty() || this.hasData;
    }

    @Override
    public boolean isEmpty() {
        return this.entityQueue.isEmpty();
    }

    @Override
    public E next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }

        if (this.isEmpty()) {
            log.error("No entities are buffered.");
        }

        return this.poll();
    }

    public int size() {
        return this.entityQueue.size();
    }
}
