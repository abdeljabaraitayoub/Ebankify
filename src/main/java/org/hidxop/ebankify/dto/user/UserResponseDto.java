package org.hidxop.ebankify.dto.user;

import org.hidxop.ebankify.domain.entity.Account;
import org.hidxop.ebankify.domain.entity.Invoice;
import org.hidxop.ebankify.domain.entity.Loan;
import org.hidxop.ebankify.domain.enumeration.Role;

import java.util.Set;
import java.util.UUID;

public record UserResponseDto(
        UUID uuid,
        String name,
        int age,
        Double monthlyIncome,
        int creditScore,
        Role role,
        Set<Loan> loans,
        Set<Account> accounts,
        Set<Invoice> invoices
) {
}
