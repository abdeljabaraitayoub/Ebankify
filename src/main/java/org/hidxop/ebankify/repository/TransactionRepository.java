package org.hidxop.ebankify.repository;

import org.hidxop.ebankify.domain.entity.Transaction;
import org.hidxop.ebankify.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    @Query("select t from Transaction t join fetch t.destinationAccount join fetch t.sourceAccount")
    List<Transaction> findTransactionsWithAccounts();

    List<Transaction> findTransactionBySourceAccount_User(User sourceAccount_user);
}
