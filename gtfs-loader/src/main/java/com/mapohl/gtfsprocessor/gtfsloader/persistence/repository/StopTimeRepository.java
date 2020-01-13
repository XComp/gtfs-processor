package com.mapohl.gtfsprocessor.gtfsloader.persistence.repository;

import com.mapohl.gtfsprocessor.gtfsloader.persistence.model.StopTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StopTimeRepository extends JpaRepository<StopTime, Integer> {
}
