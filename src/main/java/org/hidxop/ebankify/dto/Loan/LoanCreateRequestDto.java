package org.hidxop.ebankify.dto.Loan;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Range;
import org.hidxop.ebankify.domain.entity.Loan;

import java.io.Serializable;
import java.util.UUID;

public record LoanCreateRequestDto(@NotNull @Positive @Range(min = 500, max = 100000) Double principal,
                                   float interestRate,
                                   @Min(3) @Positive int termMonths,
                                   UUID accountId,
                                   UUID userId) implements Serializable {
}