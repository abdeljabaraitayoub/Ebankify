package org.hidxop.ebankify.dto.transaction;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hidxop.ebankify.domain.enumeration.TransactionType;

import java.util.UUID;

@Valid
public record CreateTransactionRequestDto(
        @Positive
        @NotNull
        Double amount,
        @NotNull
        TransactionType type,
        @NotNull
        UUID sourceAccount,
        @NotNull
        UUID destinationAccount
) {
}
