package com.mapohl.gtfsprocessor.gtfsloader.persistence.repository;

import com.mapohl.gtfsprocessor.gtfsloader.persistence.model.Agency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgencyRepository extends JpaRepository<Agency, Integer> {
}
