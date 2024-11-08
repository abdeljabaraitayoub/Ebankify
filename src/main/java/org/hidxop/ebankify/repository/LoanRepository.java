package org.hidxop.ebankify.repository;

import org.hidxop.ebankify.domain.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {
}
