package com.mapohl.gtfsprocessor.gtfsloader;

import com.mapohl.gtfsprocessor.gtfsloader.persistence.model.Agency;
import com.mapohl.gtfsprocessor.gtfsloader.persistence.model.Route;
import com.mapohl.gtfsprocessor.gtfsloader.persistence.model.ScheduleEntry;
import com.mapohl.gtfsprocessor.gtfsloader.persistence.model.ScheduleException;
import com.mapohl.gtfsprocessor.gtfsloader.persistence.model.ShapePoint;
import com.mapohl.gtfsprocessor.gtfsloader.persistence.model.Stop;
import com.mapohl.gtfsprocessor.gtfsloader.persistence.model.StopTime;
import com.mapohl.gtfsprocessor.gtfsloader.persistence.model.Transfer;
import com.mapohl.gtfsprocessor.gtfsloader.persistence.model.Trip;
import com.mapohl.gtfsprocessor.gtfsloader.persistence.repository.AgencyRepository;
import com.mapohl.gtfsprocessor.gtfsloader.persistence.repository.RouteRepository;
import com.mapohl.gtfsprocessor.gtfsloader.persistence.repository.ScheduleEntryRepository;
import com.mapohl.gtfsprocessor.gtfsloader.persistence.repository.StopRepository;
import com.mapohl.gtfsprocessor.gtfsloader.persistence.repository.TripRepository;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;
import static java.time.temporal.ChronoField.*;

public class GtfsMapper {

    private static final DateTimeFormatter LOCAL_DATE_FORMATTER = new DateTimeFormatterBuilder()
                .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
                .appendValue(MONTH_OF_YEAR, 2)
                .appendValue(DAY_OF_MONTH, 2)
                .toFormatter();

    private static final DateTimeFormatter LOCAL_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendValue(HOUR_OF_DAY, 1, 2, SignStyle.NEVER)
            .appendLiteral(":")
            .appendValue(MINUTE_OF_HOUR, 2)
            .appendLiteral(":")
            .appendValue(SECOND_OF_MINUTE, 2)
            .toFormatter();

    public static Agency toAgency(String[] values) {
        return Agency.builder()
                .id(convertToInt(values[0]))
                .name(values[1])
                .url(values[2])
                .timezone(values[3])
                .language(values[4])
                .phoneNumber(values[5])
                .build();
    }

    public static Stop toStop(String[] values) {
        return Stop.builder()
                .id(values[0])
                .stopCode(values[1])
                .name(values[2])
                .description(values[3])
                .latitude(convertToDouble(values[4]))
                .longitude(convertToDouble(values[5]))
                .locationType(convertToInt(values[6]))
                .parentStation(values[7])
                .wheelchairBoarding(convertToBoolean(values[8]))
                .platformCode(values[9])
                .zoneId(values[10])
                .build();
    }

    public static Route toRoute(String[] values, AgencyRepository agencyRepository) {
        return Route.builder()
                .id(values[0])
                .agency(loadAgency(values[1], agencyRepository))
                .shortName(values[2])
                .longName(values[3])
                .type(convertToInt(values[4]))
                .backgroundColor(values[5])
                .textColor(values[6])
                .description(values[7])
                .build();
    }

    public static ScheduleEntry toScheduleEntry(String[] values) {
        return ScheduleEntry.builder()
                .id(convertToInt(values[0]))
                .monday(convertToBoolean(values[1]))
                .tuesday(convertToBoolean(values[2]))
                .wednesday(convertToBoolean(values[3]))
                .thursday(convertToBoolean(values[4]))
                .friday(convertToBoolean(values[5]))
                .saturday(convertToBoolean(values[6]))
                .sunday(convertToBoolean(values[7]))
                .startDate(convertToLocalDate(values[8]))
                .endDate(convertToLocalDate(values[9]))
                .build();
    }

    public static ScheduleException toScheduleException(String[] values, ScheduleEntryRepository scheduleEntryRepository) {
        return ScheduleException.builder()
                .id(RandomUtils.nextInt())
                .scheduleEntry(loadScheduleEntry(values[0], scheduleEntryRepository))
                .date(convertToLocalDate(values[1]))
                .exceptionType(convertToInt(values[2]))
                .build();
    }

    public static ShapePoint toShapePoint(String[] values) {
        return ShapePoint.builder()
                .id(RandomUtils.nextInt())
                .shapeId(convertToInt(values[0]))
                .latitude(convertToDouble(values[1]))
                .longitude(convertToDouble(values[2]))
                .order(convertToInt(values[3]))
                .build();
    }

    public static Trip toTrip(String[] values, RouteRepository routeRepository, ScheduleEntryRepository scheduleEntryRepository) {
        return Trip.builder()
                .id(convertToInt(values[2]))
                .route(loadRoute(values[0], routeRepository))
                .scheduleEntry(loadScheduleEntry(values[1], scheduleEntryRepository))
                .headsign(values[3])
                .shortName(values[4])
                .directionId(convertToInt(values[5]))
                .blockId(values[6])
                .shape(convertToInt(values[7]))
                .wheelchairAccessible(convertToBoolean(values[8]))
                .bikesAllowed(convertToBoolean(values[9]))
                .build();
    }

    public static StopTime toStopTime(String[] values, TripRepository tripRepository, StopRepository stopRepository) {
        return StopTime.builder()
                .id(RandomUtils.nextInt())
                .trip(loadTrip(values[0], tripRepository))
                .arrivalTime(convertToLocalTime(values[1]))
                .departureTime(convertToLocalTime(values[2]))
                .stop(loadStop(values[3], stopRepository))
                .stopSequence(convertToInt(values[4]))
                .pickupType(convertToInt(values[5]))
                .dropoffType(convertToInt(values[6]))
                .stopHeadsign(values[7])
                .build();
    }

    public static Transfer toTransfer(String[] values, StopRepository stopRepository, RouteRepository routeRepository, TripRepository tripRepository) {
        return Transfer.builder()
                .id(RandomUtils.nextInt())
                .fromStop(loadStop(values[0], stopRepository))
                .toStop(loadStop(values[1], stopRepository))
                .type(convertToInt(values[2]))
                .minimalTransferTime(convertToInt(values[3]))
                .fromRoute(loadRoute(values[4], routeRepository))
                .toRoute(loadRoute(values[5], routeRepository))
                .fromTrip(loadTrip(values[6], tripRepository))
                .toTrip(loadTrip(values[7], tripRepository))
                .build();
    }

    private static Agency loadAgency(String id, AgencyRepository agencyRepository) {
        return agencyRepository.findById(Integer.parseInt(id)).orElseThrow(() -> new IllegalStateException("No agency found for id " + id + "."));
    }

    private static ScheduleEntry loadScheduleEntry(String id, ScheduleEntryRepository scheduleEntryRepository) {
        return scheduleEntryRepository.findById(Integer.parseInt(id)).orElseThrow(() -> new IllegalStateException("No schedule entry found for id " + id + "."));
    }

    private static Route loadRoute(String id, RouteRepository routeRepository) {
        return routeRepository.findById(id).orElseThrow(() -> new IllegalStateException("No route found for id " + id + "."));
    }

    private static Trip loadTrip(String id, TripRepository tripRepository) {
        return tripRepository.findById(convertToInt(id)).orElseThrow(() -> new IllegalStateException("No trip found for id " + id + "."));
    }

    private static Stop loadStop(String id, StopRepository stopRepository) {
        return stopRepository.findById(id).orElseThrow(() -> new IllegalStateException("No stop found for id " + id + "."));
    }

    private static double convertToDouble(String value) {
        return Double.parseDouble(value);
    }

    private static int convertToInt(String value) {
        return Integer.parseInt(value);
    }

    private static LocalDate convertToLocalDate(String value) {
        return LocalDate.parse(value, LOCAL_DATE_FORMATTER);
    }

    private static LocalTime convertToLocalTime(String value) {
        String[] values = value.split(":");
        int hour = Integer.parseInt(values[0]) % 24;
        String timeStr = String.format("%01d:%s:%s", hour, values[1], values[2]);

        return LocalTime.parse(timeStr, LOCAL_TIME_FORMATTER);
    }

    private static boolean convertToBoolean(String value) {
        if (value == null) {
            return false;
        }

        switch (value) {
            case "true":
            case "1":
                return true;
            case "false":
            case "0":
            default:
                return false;
        }
    }
}
