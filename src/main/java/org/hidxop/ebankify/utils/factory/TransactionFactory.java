package org.hidxop.ebankify.utils.factory;

import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.hidxop.ebankify.domain.entity.Account;
import org.hidxop.ebankify.domain.entity.Transaction;
import org.hidxop.ebankify.domain.enumeration.TransactionStatus;
import org.hidxop.ebankify.domain.enumeration.TransactionType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class TransactionFactory {
    private final Faker faker;

    public TransactionFactory(Faker faker) {
        this.faker = faker;
    }

    public Transaction createFakeTransaction(Account sourceAccount, Account destinationAccount) {
        double amount = faker.number().randomDouble(2, 10, 1000);

        return Transaction.builder()
                .type(faker.options().option(TransactionType.class))
                .amount(amount)
                .status(TransactionStatus.COMPLETED)
                .sourceAccount(sourceAccount)
                .destinationAccount(destinationAccount)
                .build();
    }

    public List<Transaction> createMany(Account sourceAccount, Account destinationAccount, int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createFakeTransaction(sourceAccount, destinationAccount))
                .collect(Collectors.toList());
    }
}
