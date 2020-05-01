package com.mapohl.gtfsprocessor.genericproducer.services.sources;

import com.google.common.base.Preconditions;
import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;
import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;

public abstract class AbstractEntitySource<I, E extends Entity<?>> implements EntitySource<E> {

    private final EntityMapper<I, E> entityMapper;
    private final EntityQueue<I, ? extends Entity<?>>[] downstreamEntityQueues;

    public AbstractEntitySource(EntityMapper<I, E> entityMapper, EntityQueue<I, ? extends Entity<?>>... downstreamEntityQueues) {
        this.entityMapper = Preconditions.checkNotNull(entityMapper);
        this.downstreamEntityQueues = downstreamEntityQueues;
    }

    protected E mapOrNull(I input) {
        return input != null ? this.entityMapper.map(input) : null;
    }

    protected void propagateInputToDownstreamQueues(I input) {
        for (EntityQueue<I, ?> downstreamEntityQueue : this.downstreamEntityQueues) {
            downstreamEntityQueue.add(input);
        }
    }

    protected void propagateEndOfDataDownstream() {
        for (EntityQueue<I, ?> downstreamEntityQueue : this.downstreamEntityQueues) {
            downstreamEntityQueue.endOfDataReached();
        }
    }
}
