package org.hidxop.ebankify.service;

import org.hidxop.ebankify.dto.Loan.LoanBasicResponseDto;
import org.hidxop.ebankify.dto.Loan.LoanCreateRequestDto;

import java.util.List;
import java.util.UUID;

public interface ILoanService {
    List<LoanBasicResponseDto> findAll();

    LoanBasicResponseDto finById(UUID id);

    LoanBasicResponseDto create(LoanCreateRequestDto loanDto);
}
