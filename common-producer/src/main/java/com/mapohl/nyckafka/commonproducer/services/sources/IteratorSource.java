package com.mapohl.nyckafka.commonproducer.services.sources;

import com.google.common.base.Preconditions;
import com.mapohl.nyckafka.commonproducer.domain.Entity;
import com.mapohl.nyckafka.commonproducer.domain.EntityMapper;
import com.mapohl.nyckafka.commonproducer.utils.TimePeriod;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;

@Slf4j
public class IteratorSource<I, E extends Entity<?>> extends BasicEntityQueue<I, E> implements EntitySource<E> {

    private final Iterator<I> inputIterator;
    private final int bufferSize;

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
    public E next(TimePeriod timePeriod) {
        E entity = super.next(timePeriod);
        this.fillBuffer();

        return entity;
    }
}
