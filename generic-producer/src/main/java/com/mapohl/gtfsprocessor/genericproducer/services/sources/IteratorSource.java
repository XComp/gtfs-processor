package com.mapohl.gtfsprocessor.genericproducer.services.sources;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;
import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import com.mapohl.gtfsprocessor.genericproducer.utils.TimePeriod;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
public class IteratorSource<I, E extends Entity<?>> implements EntitySource<E> {

    @Getter
    private final String topic;

    private final Iterator<I> inputIterator;

    private final EntityMapper<I, E> entityMapper;

    private final int entityLimit;
    private final List<EntityQueue<I, ? extends Entity<?>>> downstreamEntityQueues;
    private I nextInput;
    private int entityCount = 0;

    public IteratorSource(String topic,
                          Iterator<I> inputIterator,
                          EntityMapper<I, E> entityMapper,
                          EntityQueue<I, ? extends Entity<?>>... downstreamEntityQueues) {
        this(topic, inputIterator, entityMapper, Integer.MAX_VALUE, downstreamEntityQueues);
    }

    public IteratorSource(String topic,
                          Iterator<I> inputIterator,
                          EntityMapper<I, E> entityMapper,
                          int entityLimit,
                          EntityQueue<I, ? extends Entity<?>>... downstreamEntityQueues) {
        this.topic = topic;
        this.inputIterator = inputIterator;
        this.entityMapper = entityMapper;
        this.entityLimit = entityLimit;
        this.downstreamEntityQueues = Lists.newArrayList(downstreamEntityQueues);

        this.loadNextInput();
    }

    public boolean hasNext() {
        return this.entityCount < this.entityLimit && this.nextInput != null;
    }

    private I loadNextInput() {
        I input = this.nextInput;

        if (this.inputIterator.hasNext() && this.entityCount + 1 < this.entityLimit) {
            this.nextInput = this.inputIterator.next();
        } else {
            this.nextInput = null;
        }

        return input;
    }

    @Override
    public E take(TimePeriod timePeriod) {
        Preconditions.checkNotNull(timePeriod);

        while (true) {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }

            E entity = this.peek();
            if (entity == null ||
                    timePeriod.timeIsAfterTimePeriod(entity.getEventTime())) {
                return null;
            }

            I input = this.loadNextInput();
            if (timePeriod.timeIsBeforeTimePeriod(entity.getEventTime())) {
                log.debug("Entity is skipped: {}", entity);
            } else {
                this.downstreamEntityQueues.forEach(q -> q.add(input));
                if (this.nextInput == null) {
                    this.downstreamEntityQueues.forEach(q -> q.endOfData());
                }

                this.entityCount++;
                return entity;
            }
        }
    }

    @Override
    public E peek() {
        return this.nextInput != null ? this.entityMapper.map(this.nextInput) : null;
    }
}
