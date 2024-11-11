package org.hidxop.ebankify.utils.factory;

import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.hidxop.ebankify.domain.entity.Account;
import org.hidxop.ebankify.domain.entity.User;
import org.hidxop.ebankify.domain.enumeration.AccountStatus;
import org.hidxop.ebankify.domain.enumeration.Bank;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class AccountFactory {
    private Faker faker;

    public AccountFactory(Faker faker) {
        this.faker = faker;
    }

    public Account createFakeAccount(User user) {
        return Account.builder()
                .balance(faker.number().randomDouble(2, 0, 100000))
                .status(faker.options().option(AccountStatus.class))
                .user(user)
                .outgoingTransactions(new HashSet<>())
                .incomingTransactions(new HashSet<>())
                .bank(faker.options().option(Bank.class))
                .build();
    }

    public List<Account> createMany(User user, int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createFakeAccount(user))
                .collect(Collectors.toList());
    }
}
