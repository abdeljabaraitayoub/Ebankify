package org.hidxop.ebankify.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hidxop.ebankify.domain.entity.Account;
import org.hidxop.ebankify.dto.account.AccountResponseDto;
import org.hidxop.ebankify.dto.account.CreateAccountRequestDto;
import org.hidxop.ebankify.service.IAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/v1/account")
public class AccountController {
    private final IAccountService accountService;

    @GetMapping
    public ResponseEntity<List<AccountResponseDto>> findAll() {
        return new ResponseEntity<>(accountService.findAll(), HttpStatus.ACCEPTED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDto> findById(@Valid @PathVariable UUID id) {
        return new ResponseEntity<>(accountService.findById(id), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@Valid @PathVariable UUID id) {
        accountService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<AccountResponseDto> create(
            @Valid @RequestBody CreateAccountRequestDto accountDto
    ) {
        return new ResponseEntity<>(accountService.create(accountDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<AccountResponseDto> activate(@Valid @PathVariable UUID id) {
        return new ResponseEntity<>(accountService.activate(id), HttpStatus.ACCEPTED);

    }

    @PatchMapping("/{id}/block")
    public ResponseEntity<AccountResponseDto> block(@Valid @PathVariable UUID id) {
        return new ResponseEntity<>(accountService.block(id), HttpStatus.ACCEPTED);

    }

}
