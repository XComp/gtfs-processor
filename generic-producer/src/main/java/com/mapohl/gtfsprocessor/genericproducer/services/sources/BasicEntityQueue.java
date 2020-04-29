package com.mapohl.gtfsprocessor.genericproducer.services.sources;

import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;
import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import com.mapohl.gtfsprocessor.genericproducer.utils.TimePeriod;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

@RequiredArgsConstructor
@Slf4j
public class BasicEntityQueue<I, E extends Entity<?>> implements EntityQueue<I, E> {

    private final Queue<E> entityQueue = new PriorityBlockingQueue<>();

    @Getter
    private final String topic;

    private final EntityMapper<I, E> entityMapper;

    private boolean hasData = true;

    @Override
    public E take(TimePeriod timePeriod) {
        while (true) {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }

            E entity = this.peek();
            if (entity == null ||
                    timePeriod.timeIsAfterTimePeriod(entity.getEventTime())) {
                return null;
            }

            entity = this.entityQueue.remove();
            if (timePeriod.timeIsBeforeTimePeriod(entity.getEventTime())) {
                log.debug("Entity is skipped: {}", entity);
            } else {
                return entity;
            }
        }
    }

    @Override
    public E peek() {
        return this.entityQueue.peek();
    }

    @Override
    public boolean hasNext() {
        return this.entityQueue.isEmpty() || this.hasData;
    }

    @Override
    public void add(I input) {
        this.entityQueue.offer(this.entityMapper.map(input));
    }

    @Override
    public void endOfData() {
        this.hasData = false;
    }
}
