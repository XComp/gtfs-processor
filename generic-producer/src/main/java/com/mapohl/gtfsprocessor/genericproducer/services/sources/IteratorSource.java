package com.mapohl.gtfsprocessor.genericproducer.services.sources;

import com.google.common.collect.AbstractIterator;
import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;
import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Iterator;

@Slf4j
public class IteratorSource<I, E extends Entity<?>> extends AbstractIterator<E> implements EntitySource<E> {

    private final Iterator<I> inputIterator;
    private final EntityMapper<I, E> entityMapper;
    private final int entityLimit;
    private final EntityQueue<I, ? extends Entity<?>>[] downstreamEntityQueues;

    private int entityCount = 0;

    public IteratorSource(Iterator<I> inputIterator,
                          EntityMapper<I, E> entityMapper,
                          EntityQueue<I, ? extends Entity<?>>... downstreamEntityQueues) {
        this(inputIterator, entityMapper, Integer.MAX_VALUE, downstreamEntityQueues);
    }

    public IteratorSource(Iterator<I> inputIterator, EntityMapper<I, E> entityMapper, int entityLimit, EntityQueue<I, ? extends Entity<?>>... downstreamEntityQueues) {
        this.inputIterator = inputIterator;
        this.entityMapper = entityMapper;
        this.entityLimit = entityLimit;
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
    protected E computeNext() {
        if (this.inputIterator.hasNext() && this.entityCount < this.entityLimit) {
            this.entityCount++;
            I input = this.inputIterator.next();

            this.propagateInputToDownstreamQueues(input);

            if (!this.inputIterator.hasNext() || this.entityCount >= this.entityLimit) {
                // propagate end of data as early as possible
                this.propagateEndOfDataDownstream();
            }
            return this.entityMapper.map(input);
        }

        return this.endOfData();
    }

    @Override
    public Instant peekNextEventTime() {
        return this.hasNext() ? this.peek().getEventTime() : null;
    }
}
