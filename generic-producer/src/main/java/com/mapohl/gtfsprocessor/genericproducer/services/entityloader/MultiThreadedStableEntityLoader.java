package com.mapohl.gtfsprocessor.genericproducer.services.entityloader;

import com.google.common.collect.Lists;
import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;
import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
@Slf4j
public class MultiThreadedStableEntityLoader<E extends Entity<?>> extends AbstractEntityLoader<E> {

    private final String filepath;
    private final EntityMapper<E> entityMapper;
    private final int threadPoolSize;

    @Override
    public void load(BlockingQueue<E> entityQueue) throws IOException {
        List<Future<E>> futures = Lists.newArrayList();
        ExecutorService threadPool = Executors.newFixedThreadPool(this.threadPoolSize);
        int lineCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(this.filepath))) {
            String line = reader.readLine();
            while (line != null) {
                if (this.process(line)) {
                    final String currentLine = line;
                    futures.add(threadPool.submit(() -> this.entityMapper.map(currentLine)));
                }

                if (lineCount % 100_000 == 0) {
                    log.info("{} lines read.", lineCount);
                }
                line = reader.readLine();
            }

            this.endOfDataReached();
        } finally {
            log.info("Reading the file is finished. {} lines are in the queue.", futures.size());
        }

        threadPool.shutdown();

        int entityCount = 0;
        for (Future<E> future : futures) {
            E entity = null;
            try {
                entity = future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            if (entity != null && this.process(entity)) {
                entityQueue.add(entity);

                if (++entityCount % 100_000 == 0) {
                    log.info("{} entities are loaded.", entityCount);
                }
            }
        }
    }
}
