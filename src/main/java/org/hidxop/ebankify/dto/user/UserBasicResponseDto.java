package org.hidxop.ebankify.dto.user;

import org.hidxop.ebankify.domain.enumeration.Role;

import java.util.UUID;

public record UserBasicResponseDto(
        UUID uuid,
        String name,
        int age,
        Double monthlyIncome,
        int creditScore,
        Role role
) {
}
