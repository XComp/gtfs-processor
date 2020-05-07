package com.mapohl.nyckafka.speedtracker.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mapohl.nyckafka.commonproducer.domain.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpeedTracker implements Entity<Long> {

    static DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .append(ISO_LOCAL_TIME)
            .toFormatter();

    private double speed;
    private int travelTimeInSeconds;
    private String creationTimeStr;
    private int linkId;
    private List<LinkPoint> linkPoints;
    private String borough;
    private String description;

    @Override
    public Long getEntityId() {
        return this.getEventTime().getEpochSecond() ^ ((long) this.linkId << 31);
    }

    @JsonIgnore
    @Override
    public Instant getEventTime() {
        return LocalDateTime.parse(this.getCreationTimeStr(), DATE_TIME_FORMATTER).toInstant(ZoneOffset.UTC);
    }
}
