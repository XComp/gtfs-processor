package com.mapohl.gtfsprocessor.genericproducer.services.sources;

import com.google.common.base.Preconditions;
import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;
import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;

@Slf4j
public class IteratorSource<I, E extends Entity<?>> extends BasicEntityQueue<I, E> implements EntitySource<E> {

    private final Iterator<I> inputIterator;
    private final int bufferSize;

    public IteratorSource(Iterator<I> inputIterator, EntityMapper<I, E> entityMapper, EntityQueue<I, ? extends Entity<?>>... downstreamEntityQueues) {
        this(inputIterator, 1, entityMapper, downstreamEntityQueues);
    }

    public IteratorSource(Iterator<I> inputIterator, int bufferSize, EntityMapper<I, E> entityMapper, EntityQueue<I, ? extends Entity<?>>... downstreamEntityQueues) {
        super(entityMapper, downstreamEntityQueues);

        Preconditions.checkArgument(bufferSize > 0);

        this.inputIterator = Preconditions.checkNotNull(inputIterator);
        this.bufferSize = bufferSize;

        this.fillBuffer();
    }

    private void fillBuffer() {
        while (this.cachedInputCount() < this.bufferSize && this.inputIterator.hasNext()) {
            this.add(this.inputIterator.next());

            if (!this.inputIterator.hasNext()) {
                this.endOfDataReached();
                return;
            }
        }
    }

    @Override
    public E next() {
        E entity = super.next();
        this.fillBuffer();

        return entity;
    }
}
