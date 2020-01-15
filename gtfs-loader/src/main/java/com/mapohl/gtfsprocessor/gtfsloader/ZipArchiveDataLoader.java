package com.mapohl.gtfsprocessor.gtfsloader;

import com.google.common.base.Preconditions;
import com.mapohl.gtfsprocessor.gtfsloader.persistence.repository.AgencyRepository;
import com.mapohl.gtfsprocessor.gtfsloader.persistence.repository.RouteRepository;
import com.mapohl.gtfsprocessor.gtfsloader.persistence.repository.ScheduleEntryRepository;
import com.mapohl.gtfsprocessor.gtfsloader.persistence.repository.ScheduleExceptionRepository;
import com.mapohl.gtfsprocessor.gtfsloader.persistence.repository.ShapePointRepository;
import com.mapohl.gtfsprocessor.gtfsloader.persistence.repository.StopRepository;
import com.mapohl.gtfsprocessor.gtfsloader.persistence.repository.StopTimeRepository;
import com.mapohl.gtfsprocessor.gtfsloader.persistence.repository.TransferRepository;
import com.mapohl.gtfsprocessor.gtfsloader.persistence.repository.TripRepository;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.mapohl.gtfsprocessor.gtfsloader.GtfsMapper.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class ZipArchiveDataLoader implements CommandLineRunner {

    private final CSVParser csvParser = new CSVParserBuilder()
            .withSeparator(',')
            .withQuoteChar('"')
            .withEscapeChar('\\')
            .withFieldAsNull(CSVReaderNullFieldIndicator.BOTH)
            .withStrictQuotes(false)
            .withIgnoreQuotations(false)
            .build();

    @Autowired
    private final AgencyRepository agencyRepository;

    @Autowired
    private final StopRepository stopRepository;

    @Autowired
    private final RouteRepository routeRepository;

    @Autowired
    private final ScheduleEntryRepository scheduleEntryRepository;

    @Autowired
    private final ScheduleExceptionRepository scheduleExceptionRepository;

    @Autowired
    private final ShapePointRepository shapePointRepository;

    @Autowired
    private final TripRepository tripRepository;

    @Autowired
    private final StopTimeRepository stopTimeRepository;

    @Autowired
    private final TransferRepository transferRepository;

    @Override
    public void run(String[] args) throws Exception {
        Preconditions.checkArgument(args.length >= 1, "No zip archive was passed.");

        try (ZipFile zipArchive = new ZipFile(args[0])) {
            load(zipArchive, "agency.txt", v -> agencyRepository.save(toAgency(v)));
            load(zipArchive, "stops.txt", v -> stopRepository.save(toStop(v)));
            load(zipArchive, "routes.txt", v -> routeRepository.save(toRoute(v, agencyRepository)));
            load(zipArchive, "calendar.txt", v -> scheduleEntryRepository.save(toScheduleEntry(v)));
            load(zipArchive, "calendar_dates.txt", v -> scheduleExceptionRepository.save(toScheduleException(v, scheduleEntryRepository)));
//            load(zipArchive, "shapes.txt", v -> shapePointRepository.save(toShapePoint(v)));
            load(zipArchive, "trips.txt", v -> tripRepository.save(toTrip(v, routeRepository, scheduleEntryRepository)));
            load(zipArchive, "stop_times.txt", v -> stopTimeRepository.save(toStopTime(v, tripRepository, stopRepository)));
            load(zipArchive, "transfers.txt", v -> transferRepository.save(toTransfer(v, stopRepository, routeRepository, tripRepository)));

            log.info("{} agencies saved in database.", agencyRepository.count());
            log.info("{} stops saved in database.", stopRepository.count());
            log.info("{} routes saved in database.", routeRepository.count());
            log.info("{} schedule entries saved in database.", scheduleEntryRepository.count());
            log.info("{} schedule exceptions saved in database.", scheduleExceptionRepository.count());
//            log.info("{} shape points saved in database.", shapePointRepository.count());
            log.info("{} trips saved in database.", tripRepository.count());
            log.info("{} stop times saved in database.", stopTimeRepository.count());
            log.info("{} transfers saved in database.", transferRepository.count());
        }
    }

    private void load(ZipFile zipArchive, String entryName, Consumer<String[]> saveData) throws IOException {
        log.info("Starts loading data from {}.", entryName);
        ZipEntry zipEntry = zipArchive.getEntry(entryName);

        try (CSVReader csvReader = createCSVReader(zipArchive.getInputStream(zipEntry))) {
            csvReader.skip(1);

            int count = 0;
            String[] values = csvReader.readNext();
            while (values != null) {
                saveData.accept(values);
                values = csvReader.readNext();

                if (++count % 1000 == 0) {
                    log.info("{} entries loaded from {}.", count, entryName);
                }
            }

            log.info("{} entries loaded from {}.", count, entryName);
        } catch (CsvValidationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private CSVReader createCSVReader(InputStream inputStream) {
        return new CSVReaderBuilder(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8)
        ).withCSVParser(csvParser).build();
    }
}
