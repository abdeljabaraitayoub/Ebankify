package org.hidxop.ebankify.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hidxop.ebankify.domain.entity.Loan;
import org.hidxop.ebankify.dto.Loan.LoanBasicResponseDto;
import org.hidxop.ebankify.dto.Loan.LoanCreateRequestDto;
import org.hidxop.ebankify.repository.LoanRepository;
import org.hidxop.ebankify.service.ILoanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/v1/loan")
public class LoanController {
    private final ILoanService loanService;

    @GetMapping
    public ResponseEntity<List<LoanBasicResponseDto>> findAll() {
        List<LoanBasicResponseDto> loans = loanService.findAll();
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanBasicResponseDto> findById(@PathVariable @Valid UUID id) {
        LoanBasicResponseDto loan = loanService.finById(id);
        return ResponseEntity.ok(loan);
    }

    @PostMapping
    public ResponseEntity<LoanBasicResponseDto> Create(@RequestBody @Valid LoanCreateRequestDto loanDto) {
        LoanBasicResponseDto loan = loanService.create(loanDto);
        return new ResponseEntity<>(loan, HttpStatus.CREATED);
    }


}
