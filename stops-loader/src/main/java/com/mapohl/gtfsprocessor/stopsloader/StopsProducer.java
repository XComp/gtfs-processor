package com.mapohl.gtfsprocessor.stopsloader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.mapohl.gtfsprocessor.stopsloader.domain.Stop;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.types.DataTypes;
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

import static org.apache.spark.sql.functions.callUDF;
import static org.apache.spark.sql.functions.col;

@Slf4j
@SpringBootApplication()
public class StopsProducer implements CommandLineRunner {

    private KafkaTemplate<Long, Stop> template;

    private NewTopic kafkaTopic;

    @Autowired
    public StopsProducer(KafkaTemplate<Long, Stop> template, NewTopic topic) {
        this.template = template;
        this.kafkaTopic = topic;
    }

    public static void main(String[] args) {
        SpringApplication.run(StopsProducer.class, args).close();
    }

    @Override
    public void run(String... args) throws IOException, InterruptedException {
        Preconditions.checkArgument(args.length > 0, "No ZIP archive was specified.");
        String zipArchivePath = args[0];

        ZipFile zipArchive = new ZipFile(zipArchivePath);
        Path stopsFile = extractFile(zipArchive, "stops.txt");
        Path stopTimesFile = extractFile(zipArchive, "stop_times.txt");

        SparkSession spark = SparkSession.builder().master("local[8]").getOrCreate();
        JavaSparkContext javaSparkContext = new JavaSparkContext(spark.sparkContext());
        SQLContext sqlContext = new SQLContext(javaSparkContext);

        sqlContext.udf().register("extract_seconds_of_day", (UDF1<String, Integer>) (timeStr) -> {
            String[] values = timeStr.split(":");
            int secondsOfDay = Integer.parseInt(values[0]) * 60;
            secondsOfDay += Integer.parseInt(values[1]);
//            secondsOfDay += RandomUtils.nextInt(60);

            return secondsOfDay;
        }, DataTypes.IntegerType);

        Dataset<Row> stopsDataset = spark.read()
                .option("header", "true")
                .option("inferSchema", "true")
                .csv(stopsFile.toAbsolutePath().toString())
                .as("stops");

        Dataset<Row> stopTimes = spark.read()
                .option("header", "true")
                .option("inferSchema", "true")
                .csv(stopTimesFile.toAbsolutePath().toString())
                .as("stop_times");

        List<Row> rows = stopTimes.join(stopsDataset, col("stop_times.stop_id").equalTo(col("stops.stop_id")))
                .select("arrival_time", "departure_time", "stops.stop_id", "stop_sequence", "stop_name", "stop_lat", "stop_lon")
                .withColumn("seconds_of_day", callUDF("extract_seconds_of_day", col("arrival_time")))
                .orderBy("seconds_of_day")
                .collectAsList();

        int startSecond = LocalTime.now().toSecondOfDay();

        int rowCount = 0;
        for (Row row : rows) {
            Stop stop = Stop.builder().arrivalTime(row.getString(0))
                    .departureTime(row.getString(1))
                    .stopId(row.getString(2))
                    .stopSequence(row.getInt(3))
                    .name(row.getString(4))
                    .latitude(row.getDouble(5))
                    .longitude(row.getDouble(6))
                    .secondOfDay(row.getInt(7)).build();

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

        spark.stop();
    }

    private static Path extractFile(ZipFile zipArchive, String entryName) throws IOException {
        Path extractedFile = Files.createTempFile("", "-" + entryName);
        extractedFile.toFile().delete();

        ZipEntry entry = zipArchive.getEntry(entryName);

        Files.copy(zipArchive.getInputStream(entry), extractedFile);

        return extractedFile;
    }
}
