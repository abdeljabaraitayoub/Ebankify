package org.hidxop.ebankify.repository;

import org.hidxop.ebankify.domain.entity.Account;
import org.hidxop.ebankify.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    @Query("SELECT a FROM Account a JOIN FETCH a.user")
    List<Account> findAccountWithUsers();

    List<Account> findAccountByUserUuid(UUID user_uuid);


    @Modifying
    @Query("UPDATE Account a SET a.balance = a.balance + :amount WHERE a.uuid = :id")
    void increaseBalance(UUID id, double amount);

    @Modifying
    @Query("update Account a SET a.balance = a.balance - :amount where a.uuid = :id")
    void decreaseBalance(UUID id, double amount);
}
