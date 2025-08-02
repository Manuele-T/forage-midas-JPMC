package com.jpmc.midascore.repository;

import com.jpmc.midascore.entity.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for persisting and querying TransactionRecord entities.
 */
@Repository
public interface TransactionRepository extends JpaRepository<TransactionRecord, Long> {
}
