package com.mapohl.gtfsprocessor.genericproducer.services.entityloader;

import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;
import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

@Slf4j
@RequiredArgsConstructor
public class BackgroundEntityLoader<E extends Entity<?>> extends AbstractEntityLoader<E> {

    @RequiredArgsConstructor
    private class ReaderThread extends Thread {

        private final BlockingQueue<E> entityQueue;
        private final int limit;

        @Override
        public void run() {
            int entityCount = 0;
            try (BufferedReader reader = new BufferedReader(new FileReader(BackgroundEntityLoader.this.csvFilePath))) {
                String line = reader.readLine();
                while (line != null && entityCount++ < limit) {
                    if (BackgroundEntityLoader.this.process(line)) {
                        E entity = BackgroundEntityLoader.this.entityMapper.map(line);
                        if (BackgroundEntityLoader.this.process(entity)) {
                            this.entityQueue.put(entity);
                        }
                    }

                    line = reader.readLine();
                }

                BackgroundEntityLoader.this.endOfDataReached();
            } catch (IOException | InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private final String csvFilePath;
    private final EntityMapper<E> entityMapper;

    @Override
    public void load(BlockingQueue<E> entityQueue, int limit) throws Exception {
        Thread readingThread = new ReaderThread(entityQueue, limit);
        readingThread.start();
    }
}
