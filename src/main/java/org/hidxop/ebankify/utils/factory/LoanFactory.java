package org.hidxop.ebankify.utils.factory;

import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.hidxop.ebankify.domain.entity.Loan;
import org.hidxop.ebankify.domain.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component

public class LoanFactory {
    private final Faker faker;

    public LoanFactory(Faker faker) {
        this.faker = faker;
    }

    public Loan createFakeLoan(User user) {
        return Loan.builder()
                .principal(faker.number().randomDouble(2, 1000, 50000))
                .interestRate((float) faker.number().randomDouble(2, 3, 15))
                .termMonths(faker.number().numberBetween(12, 60))
                .isApproved(faker.bool().bool())
                .user(user)
                .build();
    }

    public List<Loan> createMany(User user, int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createFakeLoan(user))
                .collect(Collectors.toList());
    }
}
