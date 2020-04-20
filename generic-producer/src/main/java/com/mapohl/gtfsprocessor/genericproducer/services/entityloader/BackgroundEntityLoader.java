package com.mapohl.gtfsprocessor.genericproducer.services.entityloader;

import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;
import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.concurrent.BlockingQueue;
import java.util.zip.GZIPInputStream;

@Slf4j
@RequiredArgsConstructor
public class BackgroundEntityLoader<E extends Entity<?>> extends AbstractEntityLoader<E> {

    @RequiredArgsConstructor
    private class ReaderThread extends Thread {

        private final BlockingQueue<E> entityQueue;

        @Override
        public void run() {
            try {
                InputStream iStream = new FileInputStream(BackgroundEntityLoader.this.csvFilePath);
                if (BackgroundEntityLoader.this.csvFilePath.endsWith(".gz")) {
                    iStream = new GZIPInputStream(iStream);
                }

                int entityCount = 0;
                int lineCount = 0;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(iStream, Charset.defaultCharset()))) {
                    String line = reader.readLine();
                    while (line != null && entityCount < BackgroundEntityLoader.this.entityLimit) {
                        if (++lineCount > BackgroundEntityLoader.this.firstLinesToIgnore && BackgroundEntityLoader.this.process(line)) {
                            E entity = BackgroundEntityLoader.this.entityMapper.map(line);
                            if (BackgroundEntityLoader.this.process(entity)) {
                                entityCount++;
                                this.entityQueue.put(entity);
                            }
                        }

                        line = reader.readLine();
                    }

                    BackgroundEntityLoader.this.endOfDataReached();
                }
            } catch (IOException | InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private final String csvFilePath;
    private final EntityMapper<E> entityMapper;
    private final int entityLimit;

    private int firstLinesToIgnore = 0;

    public BackgroundEntityLoader<E> withInitialLinesToIgnore(int lineCount) {
        this.firstLinesToIgnore = lineCount;

        return this;
    }

    @Override
    public void load(BlockingQueue<E> entityQueue) throws Exception {
        Thread readingThread = new ReaderThread(entityQueue);
        readingThread.start();
    }
}
