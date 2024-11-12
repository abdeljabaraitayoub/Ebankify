package org.hidxop.ebankify.dto.Loan;

import org.hidxop.ebankify.domain.enumeration.AccountStatus;
import org.hidxop.ebankify.domain.enumeration.Bank;

import java.io.Serializable;
import java.util.UUID;


public record LoanBasicResponseDto(UUID uuid, Double principal, float interestRate, int termMonths, boolean isApproved,
                                   AccountDto account, UserDto user) implements Serializable {

    public record AccountDto(UUID uuid, Double balance, AccountStatus status, Bank bank) implements Serializable {
    }

    public record UserDto(UUID uuid, String name, int age, Double monthlyIncome,
                          int creditScore) implements Serializable {
    }
}