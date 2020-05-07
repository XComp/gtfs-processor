package com.mapohl.nyckafka.genericproducer.utils;

import com.google.common.base.Preconditions;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Duration;
import java.time.Instant;

@Getter
@ToString
@EqualsAndHashCode
public class TimePeriod {

    private final Instant inclusiveStartTime;
    private final Instant exclusiveEndTime;

    public TimePeriod(Instant inclusiveStartTime, Instant exclusiveEndTime) {
        Preconditions.checkNotNull(inclusiveStartTime);
        Preconditions.checkNotNull(exclusiveEndTime);

        Preconditions.checkArgument(inclusiveStartTime.isBefore(exclusiveEndTime), "Start time '" + inclusiveStartTime + "' is not before '" + exclusiveEndTime + "'.");

        this.inclusiveStartTime = inclusiveStartTime;
        this.exclusiveEndTime = exclusiveEndTime;
    }

    public TimePeriod(Instant inclusiveStartTime, Duration duration) {
        this(inclusiveStartTime, inclusiveStartTime.plus(duration));
    }

    public boolean timeIsBeforeTimePeriod(Instant time) {
        return this.inclusiveStartTime.isAfter(time);
    }

    public boolean timeIsAfterTimePeriod(Instant time) {
        return !this.exclusiveEndTime.isAfter(time);
    }

    public Duration getLength() {
        return Duration.between(this.getInclusiveStartTime(), this.getExclusiveEndTime());
    }

    public TimePeriod next() {
        return new TimePeriod(this.getExclusiveEndTime(), this.getLength());
    }
}
