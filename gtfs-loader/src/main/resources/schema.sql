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
	agency_id             INT NOT NULL PRIMARY KEY,
	name                  TEXT NOT NULL,
	url                   TEXT NOT NULL,
	timezone              TEXT NOT NULL,
	language              TEXT,
	phone_number          TEXT,
	created_at            TIMESTAMP NOT NULL
);

CREATE TABLE stops (
	stop_id               VARCHAR(32) NOT NULL PRIMARY KEY,
	stop_code             TEXT,
	name                  TEXT NOT NULL,
	description           TEXT,
	latitude              DOUBLE PRECISION NOT NULL,
	longitude             DOUBLE PRECISION NOT NULL,
	location_type         INT,
	parent_station        TEXT,
	wheelchair_boarding   BOOLEAN,
	platform_code         TEXT,
	zone_id               TEXT,
    created_at            TIMESTAMP NOT NULL
);

CREATE TABLE routes (
	route_id              VARCHAR(16) NOT NULL PRIMARY KEY,
	agency_id             INT,
	short_name            TEXT,
	long_name             TEXT,
	type                  INT,
	background_color      TEXT,
	text_color            TEXT,
	description           TEXT,
    created_at            TIMESTAMP NOT NULL,
	FOREIGN KEY (agency_id) REFERENCES agencies(agency_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE schedule_entries (
	schedule_entry_id     INT NOT NULL PRIMARY KEY,
	monday                BOOLEAN NOT NULL,
	tuesday               BOOLEAN NOT NULL,
	wednesday             BOOLEAN NOT NULL,
	thursday              BOOLEAN NOT NULL,
	friday                BOOLEAN NOT NULL,
	saturday              BOOLEAN NOT NULL,
	sunday                BOOLEAN NOT NULL,
	start_date            DATE NOT NULL,
	end_date              DATE NOT NULL,
    created_at            TIMESTAMP NOT NULL
);

CREATE TABLE schedule_exceptions (
	schedule_exception_id INT NOT NULL PRIMARY KEY,
	schedule_entry_id     INT NOT NULL,
	date                  DATE NOT NULL,
	exception_type        INT NOT NULL,
	created_at            TIMESTAMP NOT NULL,
	FOREIGN KEY (schedule_entry_id) REFERENCES schedule_entries(schedule_entry_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE shape_points (
	shape_point_id        INT NOT NULL PRIMARY KEY,
	shape_id              INT NOT NULL,
	latitude              DOUBLE PRECISION NOT NULL,
	longitude             DOUBLE PRECISION NOT NULL,
	sequence              INT NOT NULL,
	created_at            TIMESTAMP NOT NULL
);

CREATE HASH INDEX ix_shape_id ON shape_points(shape_id);

CREATE TABLE trips (
	trip_id               INT NOT NULL PRIMARY KEY,
	route_id              VARCHAR(16) NOT NULL,
	schedule_entry_id     INT NOT NULL,
	stop_headsign         TEXT NULL,
	short_name            TEXT NULL,
	direction_id          INT NULL,
	block_id              TEXT NULL,
	shape_id              INT NULL,
	wheelchair_accessible INT NULL,
	bikes_allowed         INT NULL,
	created_at            TIMESTAMP NOT NULL,
	FOREIGN KEY (route_id) REFERENCES routes(route_id) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (schedule_entry_id) REFERENCES schedule_entries(schedule_entry_id) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (shape_id) REFERENCES shape_points(shape_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE stop_times (
    stop_time_id          INT NOT NULL PRIMARY KEY,
	trip_id               INT NOT NULL,
	arrival_time          TIME NOT NULL,
	departure_time        TIME NOT NULL,
	stop_id               VARCHAR(32) NOT NULL,
    stop_sequence         INT NOT NULL,
    pickup_type           SMALLINT CHECK(pickup_type >= 0 and pickup_type <=3),
    drop_off_type         SMALLINT CHECK(drop_off_type >= 0 and drop_off_type <=3),
    stop_headsign         TEXT,
    created_at            TIMESTAMP NOT NULL,
    FOREIGN KEY (trip_id) REFERENCES trips(trip_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (stop_id) REFERENCES stops(stop_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE transfers (
    transfer_id           INT NOT NULL,
    from_stop_id          VARCHAR(32) NOT NULL,
    to_stop_id            VARCHAR(32) NOT NULL,
    transfer_type         INT NOT NULL,
    min_transfer_time     INT NOT NULL,
    from_route_id         VARCHAR(16) NOT NULL,
    to_route_id           VARCHAR(16) NOT NULL,
    from_trip_id          INT NOT NULL,
    to_trip_id            INT NOT NULL,
    created_at            TIMESTAMP NOT NULL,
    FOREIGN KEY (from_stop_id) REFERENCES stops(stop_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (to_stop_id) REFERENCES stops(stop_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (from_route_id) REFERENCES routes(route_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (to_route_id) REFERENCES routes(route_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (from_trip_id) REFERENCES trips(trip_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (to_trip_id) REFERENCES trips(trip_id) ON DELETE CASCADE ON UPDATE CASCADE
);