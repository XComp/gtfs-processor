package com.mapohl.gtfsprocessor.stopsloader.services;

import com.mapohl.gtfsprocessor.stopsloader.domain.Stop;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.types.DataTypes;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.apache.spark.sql.functions.callUDF;
import static org.apache.spark.sql.functions.col;

@Service
public class SparkService {

    public List<Stop> loadStops(String stopsFilePath, String stopTimesFilePath) {
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
                .csv(stopsFilePath)
                .as("stops");

        Dataset<Row> stopTimes = spark.read()
                .option("header", "true")
                .option("inferSchema", "true")
                .csv(stopTimesFilePath)
                .as("stop_times");

        List<Stop> stops = stopTimes.join(stopsDataset, col("stop_times.stop_id").equalTo(col("stops.stop_id")))
                .select("arrival_time", "departure_time", "stops.stop_id", "stop_sequence", "stop_name", "stop_lat", "stop_lon")
                .withColumn("seconds_of_day", callUDF("extract_seconds_of_day", col("arrival_time")))
                .orderBy("seconds_of_day")
                .toDF("arrivalTime", "departureTime", "stopId", "stopSequence", "name", "latitude", "longitude", "secondOfDay")
                .as(Encoders.bean(Stop.class))
                .collectAsList();

        spark.stop();

        return stops;
    }
}
