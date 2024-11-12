package org.hidxop.ebankify.repository;

import org.hidxop.ebankify.domain.entity.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, UUID> {
}