package com.mapohl.nyckafka.commonproducer;

import com.mapohl.nyckafka.commonproducer.domain.Entity;
import com.mapohl.nyckafka.commonproducer.domain.EntityMapper;
import com.mapohl.nyckafka.commonproducer.services.DownstreamEntityEmissionService;
import com.mapohl.nyckafka.commonproducer.services.EntityEmissionScheduler;
import com.mapohl.nyckafka.commonproducer.services.InitialEntityEmissionService;
import com.mapohl.nyckafka.commonproducer.services.sources.EntitySource;
import com.mapohl.nyckafka.commonproducer.utils.LineIterator;
import lombok.Getter;
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
@Getter
public class CsvEntityProducer<ID, E extends Entity<ID>> extends AbstractEntityProducer<String, ID, E> {

    @CommandLine.Option(names = {"-c", "--csv"}, required = true)
    private String csvFilePath;

    public CsvEntityProducer(EntityMapper<String, E> initialEntityMapper,
                             String initialTopic,
                             KafkaTemplate<ID, E> initialKafkaTemplate,
                             DownstreamEntityEmissionService<String, ?, ?>... downstreamEmissionServices) {
        super(initialEntityMapper, initialTopic, initialKafkaTemplate, downstreamEmissionServices);
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
            InitialEntityEmissionService<ID, E> initialEmissionScheduler = new InitialEntityEmissionService<>(
                    entitySource,
                    this.getInitialTopic(),
                    this.getInitialKafkaTemplate()
            );

            EntityEmissionScheduler scheduler = new EntityEmissionScheduler(initialEmissionScheduler, this.getEntityLimit(), this.getDownstreamEmissionServices());
            scheduler.emit(this.getInitialTimePeriod(), this.getRealTimeSlotDuration());
        }

        return 0;
    }
}
