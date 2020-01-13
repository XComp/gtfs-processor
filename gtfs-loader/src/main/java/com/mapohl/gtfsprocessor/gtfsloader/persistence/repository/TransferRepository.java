package com.mapohl.gtfsprocessor.gtfsloader.persistence.repository;

import com.mapohl.gtfsprocessor.gtfsloader.persistence.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<Transfer, Integer> {
}
