package org.hidxop.ebankify.utils.factory;


import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.hidxop.ebankify.domain.entity.User;
import org.hidxop.ebankify.domain.enumeration.Bank;
import org.hidxop.ebankify.domain.enumeration.Role;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class UserFactory {

    private final Faker faker;

    public UserFactory(Faker faker) {
        this.faker = faker;
    }

    private User createFakeUser() {
        return User.builder()
                .name(faker.name().fullName())
                .age(faker.number().numberBetween(18, 80))
                .monthlyIncome(faker.number().randomDouble(2, 2000, 10000))
                .creditScore(faker.number().numberBetween(300, 850))
                .email(faker.internet().emailAddress())
                .password(faker.internet().password())
                .role(faker.options().option(Role.class))
                .build();
    }

    public List<User> createMany(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createFakeUser())
                .collect(Collectors.toList());
    }

}
