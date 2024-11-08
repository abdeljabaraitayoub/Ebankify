package org.hidxop.ebankify.utils;

import lombok.RequiredArgsConstructor;
import org.hidxop.ebankify.utils.factory.AccountFactory;
import org.hidxop.ebankify.utils.factory.LoanFactory;
import org.hidxop.ebankify.utils.factory.TransactionFactory;
import org.hidxop.ebankify.utils.factory.UserFactory;
import org.hidxop.ebankify.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class Seeder {
    private final UserFactory userFactory;
    private final AccountFactory accountFactory;
    private final TransactionFactory transactionFactory;
    private final LoanFactory loanFactory;

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final LoanRepository loanRepository;

    @Transactional
    public String generateUsers(int count) {
        var users = userFactory.createMany(count);
        userRepository.saveAll(users);
        return String.format("Generated %d users successfully", count);
    }

    @Transactional
    public String generateAccounts(int accountsPerUser) {
        var users = userRepository.findAll();
        int totalAccounts = 0;

        for (var user : users) {
            var accounts = accountFactory.createMany(user, accountsPerUser);
            accountRepository.saveAll(accounts);
            totalAccounts += accounts.size();
        }

        return String.format("Generated %d accounts for %d users", totalAccounts, users.size());
    }

    @Transactional
    public String generateTransactions(int transactionsPerAccount) {
        var accounts = accountRepository.findAll();
        int totalTransactions = 0;

        for (int i = 0; i < accounts.size() - 1; i++) {
            var transactions = transactionFactory.createMany(
                    accounts.get(i),
                    accounts.get(i + 1),
                    transactionsPerAccount
            );
            transactionRepository.saveAll(transactions);
            totalTransactions += transactions.size();
        }

        return String.format("Generated %d transactions", totalTransactions);
    }

    @Transactional
    public String generateLoans(int loansPerUser) {
        var users = userRepository.findAll();
        int totalLoans = 0;

        for (var user : users) {
            var loans = loanFactory.createMany(user, loansPerUser);
            loanRepository.saveAll(loans);
            totalLoans += loans.size();
        }

        return String.format("Generated %d loans for %d users", totalLoans, users.size());
    }

    @Transactional
    public String generateAll(
            int userCount,
            int accountsPerUser,
            int transactionsPerAccount,
            int loansPerUser
    ) {
        generateUsers(userCount);
        generateAccounts(accountsPerUser);
        generateTransactions(transactionsPerAccount);
        generateLoans(loansPerUser);

        return "Generated all fake data successfully";
    }

    @Transactional
    public String freshDatabase() {
        // Clear all data
        transactionRepository.deleteAll();
        loanRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.deleteAll();

        // Regenerate
        return generateAll(10, 2, 5, 1);
    }
}