package org.hidxop.ebankify.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hidxop.ebankify.dto.transaction.CreateTransactionRequestDto;
import org.hidxop.ebankify.dto.transaction.TransactionBasicResponseDto;
import org.hidxop.ebankify.service.ITransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("v1/transaction")
public class TransactionController {
    private final ITransactionService transactionService;


    @GetMapping
    public ResponseEntity<List<TransactionBasicResponseDto>> findAll() {
        return ResponseEntity.ok(transactionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionBasicResponseDto> findById(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(transactionService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TransactionBasicResponseDto> deleteById(@Valid @PathVariable UUID id) {
        transactionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<TransactionBasicResponseDto> create(@Valid @RequestBody CreateTransactionRequestDto transactionDto) {
        TransactionBasicResponseDto transactionResponseDto = transactionService.create(transactionDto);
        return new ResponseEntity<>(transactionResponseDto, HttpStatus.CREATED);
    }
}
