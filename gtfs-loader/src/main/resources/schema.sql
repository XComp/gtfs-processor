DROP TABLE IF EXISTS agencies;
DROP TABLE IF EXISTS stops;
DROP TABLE IF EXISTS routes;
DROP TABLE IF EXISTS schedules;
DROP TABLE IF EXISTS schedule_exceptions;
DROP TABLE IF EXISTS shape_points;
DROP TABLE IF EXISTS trips;
DROP TABLE IF EXISTS stop_times;
DROP TABLE IF EXISTS transfers;

CREATE TABLE agencies (
	agency_id             INTEGER NOT NULL PRIMARY KEY,
	name                  TEXT NOT NULL,
	url                   TEXT NOT NULL,
	timezone              TEXT NOT NULL,
	lang                  TEXT NULL,
	phone_number          TEXT NULL
);

CREATE TABLE stops (
	stop_id               TEXT NOT NULL PRIMARY KEY,
	stop_code             TEXT,
	name                  TEXT NOT NULL,
	description           TEXT,
	latitude              DOUBLE PRECISION NOT NULL,
	longitude             DOUBLE PRECISION NOT NULL,
	location_type         INTEGER,
	parent_station        TEXT,
	wheelchair_boarding   BOOLEAN,
	platform_code         TEXT,
	zone_id               TEXT,
);

CREATE TABLE routes (
	route_id              TEXT NOT NULL PRIMARY KEY,
	agency_id             INTEGER,
	short_name            TEXT,
	long_name             TEXT,
	type                  INTEGER,
	background_color      TEXT,
	text_color            TEXT,
	description           TEXT,
	FOREIGN KEY (agency_id) REFERENCES agencies(agency_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE schedule_entries (
	schedule_entry_id     INTEGER NOT NULL PRIMARY KEY,
	monday                BOOLEAN NOT NULL,
	tuesday               BOOLEAN NOT NULL,
	wednesday             BOOLEAN NOT NULL,
	thursday              BOOLEAN NOT NULL,
	friday                BOOLEAN NOT NULL,
	saturday              BOOLEAN NOT NULL,
	sunday                BOOLEAN NOT NULL,
	start_date            DATE NOT NULL,
	end_date              DATE NOT NULL
);

CREATE TABLE schedule_exceptions (
	schedule_exception_id INTEGER NOT NULL PRIMARY KEY,
	schedule_entry_id     INTEGER NOT NULL,
	date                  DATE NOT NULL,
	exception_type        INTEGER NOT NULL,
	FOREIGN KEY (schedule_entry_id) REFERENCES schedule_entries(schedule_entry_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE shape_points (
	shape_point_id        INTEGER NOT NULL PRIMARY KEY,
	shape_id              TEXT NOT NULL,
	latitude              DOUBLE PRECISION NOT NULL,
	longitude             DOUBLE PRECISION NOT NULL,
	sequence              INTEGER NOT NULL
);

CREATE TABLE trips (
	trip_id               INTEGER NOT NULL PRIMARY KEY,
	route_id              TEXT NOT NULL,
	schedule_entry_id     INTEGER NOT NULL,
	stop_headsign         TEXT NULL,
	short_name            TEXT NULL,
	direction_id          INTEGER NULL,
	block_id              TEXT NULL,
	shape_id              TEXT NULL,
	wheelchair_accessible INTEGER NULL,
	bikes_allowed         INTEGER NULL,
	FOREIGN KEY (route_id) REFERENCES routes(route_id) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (schedule_entry_id) REFERENCES schedule_entries(schedule_entry_id) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (shape_id) REFERENCES shape_points(shape_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE stop_times (
	trip_id               TEXT NOT NULL PRIMARY KEY,
	arrival_time          TIME NOT NULL,
	departure_time        TIME NOT NULL,
	stop_id               TEXT NOT NULL,
    stop_sequence         INTEGER NOT NULL,
    pickup_type           SMALLINT CHECK(pickup_type >= 0 and pickup_type <=3),
    drop_off_type         SMALLINT CHECK(drop_off_type >= 0 and drop_off_type <=3),
    stop_headsign         TEXT,
    FOREIGN KEY (stop_id) REFERENCES stops(stop_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE transfers (
    transfer_id         INTEGER NOT NULL,
    from_stop_id        TEXT NOT NULL,
    to_stop_id          TEXT NOT NULL,
    transfer_type       INTEGER NOT NULL,
    min_transfer_time   INTEGER NOT NULL,
    from_route_id       TEXT NOT NULL,
    to_route_id         TEXT NOT NULL,
    from_trip_id        INTEGER NOT NULL,
    to_trip_id          INTEGER NOT NULL,
    FOREIGN KEY (from_stop_id) REFERENCES stops(stop_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (to_stop_id) REFERENCES stops(stop_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (from_route_id) REFERENCES routes(route_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (to_route_id) REFERENCES routes(route_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (from_trip_id) REFERENCES trips(trip_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (to_trip_id) REFERENCES trips(trip_id) ON DELETE CASCADE ON UPDATE CASCADE
);