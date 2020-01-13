package com.mapohl.gtfsprocessor.gtfsloader.persistence.repository;

import com.mapohl.gtfsprocessor.gtfsloader.persistence.model.ShapePoint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShapePointRepository extends JpaRepository<ShapePoint, Integer> {
}
