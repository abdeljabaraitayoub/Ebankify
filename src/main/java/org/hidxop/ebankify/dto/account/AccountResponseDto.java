package org.hidxop.ebankify.dto.account;

import org.hidxop.ebankify.domain.entity.User;
import org.hidxop.ebankify.domain.enumeration.AccountStatus;
import org.hidxop.ebankify.dto.user.UserBasicResponseDto;
import org.hidxop.ebankify.dto.user.UserResponseDto;

import java.util.UUID;

public record AccountResponseDto(
        UUID uuid,
        Double balance,
        AccountStatus status,
        UserBasicResponseDto user
) {
}
