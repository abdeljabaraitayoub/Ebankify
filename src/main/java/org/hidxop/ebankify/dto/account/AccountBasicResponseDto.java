package org.hidxop.ebankify.dto.account;

import org.hidxop.ebankify.domain.enumeration.AccountStatus;

import java.util.UUID;

public record AccountBasicResponseDto(
        UUID uuid,
        Double balance,
        AccountStatus status
) {
}
