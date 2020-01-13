package com.mapohl.gtfsprocessor.gtfsloader.persistence.repository;

import com.mapohl.gtfsprocessor.gtfsloader.persistence.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, Integer> {
}
