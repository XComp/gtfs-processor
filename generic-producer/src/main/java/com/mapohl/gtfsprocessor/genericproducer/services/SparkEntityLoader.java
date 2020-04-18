package com.mapohl.gtfsprocessor.genericproducer.services;

import com.mapohl.gtfsprocessor.genericproducer.domain.Entity;
import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import lombok.RequiredArgsConstructor;
import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SparkEntityLoader<E extends Entity<?>> implements EntityLoader<E> {

    private final String filepath;
    private final Class<E> clazz;
    private final EntityMapper<E> entityMapper;

    private boolean hasHeader = false;
    private boolean inferSchema = true;

    private String sortKeyColumn = null;
    private FilterFunction<E> filterFunction = null;

    public SparkEntityLoader<E> withHeader() {
        this.hasHeader = true;

        return this;
    }

    public SparkEntityLoader<E> withoutInferingSchema() {
        this.inferSchema = false;

        return this;
    }

    public SparkEntityLoader<E> withSortingEnabled(String sortKeyColumn) {
        this.sortKeyColumn = sortKeyColumn;

        return this;
    }

    public SparkEntityLoader<E> withFilter(FilterFunction<E> filterFunction) {
        this.filterFunction = filterFunction;

        return this;
    }

    public List<E> load() {
        SparkSession spark = SparkSession.builder().master("local[8]").getOrCreate();

        Dataset<Row> data = spark.read()
                .option("header", String.valueOf(this.hasHeader))
                .option("inferSchema", String.valueOf(this.inferSchema))
                .csv(this.filepath);

        if (this.sortKeyColumn != null) {
            data = data.sort(this.sortKeyColumn);
        }

        Dataset<E> mappedData = data.map(
                (MapFunction<Row, E>) row -> SparkEntityLoader.this.entityMapper.map(row),
                Encoders.bean(this.clazz));

        if (this.filterFunction != null) {
            mappedData = mappedData.filter(this.filterFunction);
        }

        List<E> entities = mappedData.collectAsList();

        spark.stop();

        return entities;
    }
}
