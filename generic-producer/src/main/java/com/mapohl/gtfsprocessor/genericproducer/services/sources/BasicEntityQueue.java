package com.mapohl.gtfsprocessor.genericproducer.services.sources;

import com.google.common.collect.AbstractIterator;
import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;
import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

@RequiredArgsConstructor
@Slf4j
public class BasicEntityQueue<I, E extends Entity<?>> extends AbstractIterator<E> implements EntityQueue<I, E> {

    private final Queue<E> entityQueue = new PriorityBlockingQueue<>();
    private final EntityMapper<I, E> entityMapper;

    private boolean hasData = true;

    @Override
    public void add(I input) {
        if (input == null) {
            this.hasData = false;
            return;
        }

        this.entityQueue.offer(this.entityMapper.map(input));
    }

    @Override
    protected E computeNext() {
        if (this.entityQueue.isEmpty() && !this.hasData) {
            return this.endOfData();
        }

        if (this.entityQueue.isEmpty()) {
            log.error("No entities are buffered.");
        }

        return this.entityQueue.remove();
    }

    @Override
    public Instant peekNextEventTime() {
        return this.hasNext() ? this.peek().getEventTime() : null;
    }
}
