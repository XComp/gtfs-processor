package com.mapohl.gtfsprocessor.genericproducer;

import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;
import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import com.mapohl.gtfsprocessor.genericproducer.services.EntityEmissionScheduler;
import com.mapohl.gtfsprocessor.genericproducer.services.sources.EntityQueue;
import com.mapohl.gtfsprocessor.genericproducer.services.sources.EntitySource;
import com.mapohl.gtfsprocessor.genericproducer.utils.LineIterator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import picocli.CommandLine;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

@Slf4j
public class EntityProducer<ID, E extends Entity<ID>> extends AbstractEntityProducer<String, ID, E> {

    @CommandLine.Option(names = {"-c", "--csv"}, required = true)
    private String csvFilePath;

    public EntityProducer(KafkaTemplate<ID, E> kafkaTemplate,
                          String kafkaTopic,
                          EntityMapper<String, E> entityMapper,
                          EntityQueue<String, E>... downstreamEntityQueues) {
        super(kafkaTemplate, kafkaTopic, entityMapper, downstreamEntityQueues);
    }

    protected InputStreamReader createReader() throws IOException {
        InputStream inputStream = new FileInputStream(this.csvFilePath);
        if (this.csvFilePath.endsWith(".gz")) {
            inputStream = new GZIPInputStream(inputStream);
        }

        return new InputStreamReader(inputStream, StandardCharsets.UTF_8);
    }

    protected LineIterator createLineIterator(Reader reader) {
        return new LineIterator(reader, this.getLineLimit(), this.getInitialLinesToIgnore());
    }

    protected EntitySource<E> createEntitySource(Reader reader) {
        return this.createEntitySource(this.createLineIterator(reader));
    }

    @Override
    protected void logParameters() {
        super.logParameters();
        log.info("  CSV file path (--csv): {}", this.csvFilePath);
    }

    @Override
    public Integer call() throws Exception {
        this.logParameters();

        try (Reader reader = this.createReader()) {
            EntitySource<E> entitySource = this.createEntitySource(reader);
            EntityEmissionScheduler<ID, E> entityEmissionScheduler = new EntityEmissionScheduler(
                    this.getKafkaTemplate(),
                    entitySource);

            entityEmissionScheduler.emit(this.getInitialTimePeriod(), this.getRealTimeSlotDuration());
        }

        return 0;
    }
}
