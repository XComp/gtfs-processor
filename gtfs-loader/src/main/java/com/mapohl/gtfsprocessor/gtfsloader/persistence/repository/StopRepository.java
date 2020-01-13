package com.mapohl.gtfsprocessor.gtfsloader.persistence.repository;

import com.mapohl.gtfsprocessor.gtfsloader.persistence.model.Stop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StopRepository extends JpaRepository<Stop, String> {
}
