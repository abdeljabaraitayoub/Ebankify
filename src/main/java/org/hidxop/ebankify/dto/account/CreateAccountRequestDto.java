package org.hidxop.ebankify.dto.account;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateAccountRequestDto(
        @Valid
        @NotNull(message = "User ID is required")
        UUID userId
) {
}