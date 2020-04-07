package com.mapohl.gtfsprocessor.stopsloader;

import com.google.common.base.Preconditions;
import com.mapohl.gtfsprocessor.stopsloader.domain.Stop;
import com.mapohl.gtfsprocessor.stopsloader.services.SparkService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.spark.sql.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Slf4j
@SpringBootApplication()
public class StopsProducer implements CommandLineRunner {

    private KafkaTemplate<Long, Stop> template;

    private NewTopic kafkaTopic;

    private SparkService sparkService;

    @Autowired
    public StopsProducer(KafkaTemplate<Long, Stop> template, NewTopic topic, SparkService sparkService) {
        this.template = template;
        this.kafkaTopic = topic;

        this.sparkService = sparkService;
    }

    @Override
    public void run(String... args) throws IOException, InterruptedException {
        Preconditions.checkArgument(args.length > 0, "No ZIP archive was specified.");
        String zipArchivePath = args[0];

        ZipFile zipArchive = new ZipFile(zipArchivePath);
        Path stopsFile = extractFile(zipArchive, "stops.txt");
        Path stopTimesFile = extractFile(zipArchive, "stop_times.txt");

        List<Stop> stops = this.sparkService.loadStops(
                stopsFile.toAbsolutePath().toString(),
                stopTimesFile.toAbsolutePath().toString());

        int startSecond = LocalTime.now().toSecondOfDay();

        int rowCount = 0;
        for (Stop stop : stops) {
            int rowSecond = stop.getSecondOfDay();
            int currentSecond = LocalTime.now().toSecondOfDay() - startSecond;

            if (rowSecond < currentSecond) {
                continue;
            }

            while (rowSecond > currentSecond) {
                if (rowCount > 0) {
                    log.info("{} rows were sent for {}s (next second to emit: {}).", rowCount, currentSecond, rowSecond);
                    rowCount = 0;
                } else {
                    log.info("No rows were sent for {}s (next second to emit: {}).", currentSecond, rowSecond);
                }

                Thread.sleep(1000);
                currentSecond = LocalTime.now().toSecondOfDay() - startSecond;
            }

            this.template.send(this.kafkaTopic.name(), stop);
            rowCount++;
        }
    }

    private static Path extractFile(ZipFile zipArchive, String entryName) throws IOException {
        Path extractedFile = Files.createTempFile("", "-" + entryName);
        extractedFile.toFile().delete();

        ZipEntry entry = zipArchive.getEntry(entryName);

        Files.copy(zipArchive.getInputStream(entry), extractedFile);

        return extractedFile;
    }

    public static void main(String[] args) {
        SpringApplication.run(StopsProducer.class, args).close();
    }
}
