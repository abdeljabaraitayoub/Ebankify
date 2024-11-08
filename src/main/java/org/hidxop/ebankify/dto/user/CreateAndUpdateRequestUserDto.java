package org.hidxop.ebankify.dto.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Valid
public record CreateAndUpdateRequestUserDto(
        @NotBlank String name,
        @Min(18) @Max(100) int age
) {
}
