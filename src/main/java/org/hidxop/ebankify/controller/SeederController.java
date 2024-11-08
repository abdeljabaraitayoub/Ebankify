package org.hidxop.ebankify.controller;

import lombok.RequiredArgsConstructor;
import org.hidxop.ebankify.utils.Seeder;
import org.hidxop.ebankify.utils.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seed")
@RequiredArgsConstructor
public class SeederController {
    private final Seeder seederService;

    @PostMapping("/users")
    public ResponseEntity<String> seedUsers(
            @RequestParam(defaultValue = "10") int count
    ) {
        return ResponseEntity.ok(seederService.generateUsers(count));
    }

    @PostMapping("/accounts")
    public ResponseEntity<String> seedAccounts(
            @RequestParam(defaultValue = "2") int accountsPerUser
    ) {
        return ResponseEntity.ok(seederService.generateAccounts(accountsPerUser));
    }

    @PostMapping("/transactions")
    public ResponseEntity<String> seedTransactions(
            @RequestParam(defaultValue = "5") int transactionsPerAccount
    ) {
        return ResponseEntity.ok(seederService.generateTransactions(transactionsPerAccount));
    }

    @PostMapping("/loans")
    public ResponseEntity<String> seedLoans(
            @RequestParam(defaultValue = "1") int loansPerUser
    ) {
        return ResponseEntity.ok(seederService.generateLoans(loansPerUser));
    }

    @PostMapping("/all")
    public ResponseEntity<String> seedAll(
            @RequestParam(defaultValue = "100000") int userCount,
            @RequestParam(defaultValue = "2") int accountsPerUser,
            @RequestParam(defaultValue = "5") int transactionsPerAccount,
            @RequestParam(defaultValue = "1") int loansPerUser
    ) {
        return ResponseEntity.ok(seederService.generateAll(
                userCount,
                accountsPerUser,
                transactionsPerAccount,
                loansPerUser
        ));
    }

    @PostMapping("/fresh")
    public ResponseEntity<String> freshDatabase() {
        return ResponseEntity.ok(seederService.freshDatabase());
    }
}