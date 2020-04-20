package com.mapohl.gtfsprocessor.genericproducer.services.entityloader;

import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;
import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

@RequiredArgsConstructor
public class SingleThreadedEntityLoader<E extends Entity<?>> extends AbstractEntityLoader<E> {

    private final String filepath;
    private final EntityMapper<E> entityMapper;

    @Override
    public void load(BlockingQueue<E> entityQueue, int limit) throws IOException {
        int entityCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(this.filepath))) {
            String line = reader.readLine();
            while (line != null && entityCount++ < limit) {
                if (this.process(line)) {
                    E entity = this.entityMapper.map(line);
                    if (this.process(entity)) {
                        entityQueue.add(entity);
                    }
                }

                line = reader.readLine();
            }

            this.endOfDataReached();
        }
    }
}
