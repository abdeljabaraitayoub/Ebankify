package org.hidxop.ebankify.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hidxop.ebankify.domain.entity.Account;
import org.hidxop.ebankify.domain.entity.Transaction;
import org.hidxop.ebankify.domain.enumeration.AccountStatus;
import org.hidxop.ebankify.domain.enumeration.TransactionStatus;
import org.hidxop.ebankify.dto.transaction.CreateTransactionRequestDto;
import org.hidxop.ebankify.dto.transaction.TransactionBasicResponseDto;
import org.hidxop.ebankify.dto.transaction.TransactionMapper;
import org.hidxop.ebankify.exceptionHandling.exceptions.InvalidStateException;
import org.hidxop.ebankify.exceptionHandling.exceptions.NotFoundException;
import org.hidxop.ebankify.repository.AccountRepository;
import org.hidxop.ebankify.repository.TransactionRepository;
import org.hidxop.ebankify.service.ITransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class TransactionService implements ITransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public List<TransactionBasicResponseDto> findAll() {
        List<Transaction> transactions = transactionRepository.findTransactionsWithAccounts();
        return transactionMapper.toDto(transactions);
    }

    @Override
    public TransactionBasicResponseDto findById(UUID id) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new NotFoundException("Transaction Not Found"));
        return transactionMapper.toDto(transaction);
    }


    @Transactional
    @Override
    public void deleteById(UUID id) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new NotFoundException("Transaction Not Found"));
        validateRefund(transaction.getSourceAccount(), transaction.getDestinationAccount(), transaction.getAmount());
        refundTransaction(transaction.getSourceAccount(), transaction.getDestinationAccount(), transaction.getAmount());
        transactionRepository.delete(transaction);
    }

    private void refundTransaction(Account sourceAccount, Account destinationAccount, Double amount) {
        sourceAccount.setBalance(sourceAccount.getBalance() + amount);
        destinationAccount.setBalance(sourceAccount.getBalance() - amount);
    }

    private void validateRefund(Account sourceAccount, Account destinationAccount, Double amount) {

        if (sourceAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new InvalidStateException("Source account is not active");
        }

        if (destinationAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new InvalidStateException("Destination account is not active");
        }

    }

    @Transactional
    @Override
    public TransactionBasicResponseDto create(CreateTransactionRequestDto transactionDto) {
        Account sourceAccount = accountRepository.findById(transactionDto.sourceAccount())
                .orElseThrow(() -> new NotFoundException("Source account not found"));

        Account destinationAccount = accountRepository.findById(transactionDto.destinationAccount())
                .orElseThrow(() -> new NotFoundException("Destination account not found"));

        validateTransaction(sourceAccount, destinationAccount, transactionDto.amount());

        Transaction transaction = transactionMapper.toEntity(transactionDto);
        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(destinationAccount);
        transaction.setStatus(TransactionStatus.PENDING);

        processTransaction(sourceAccount, destinationAccount, transactionDto.amount());

        Transaction savedTransaction = transactionRepository.save(transaction);

        return transactionMapper.toDto(savedTransaction);
    }

    private void validateTransaction(Account sourceAccount, Account destinationAccount, Double amount) {

        if (sourceAccount.getUuid().equals(destinationAccount.getUuid())) {
            throw new InvalidStateException("You can't make transaction to your account");
        }
        if (sourceAccount.getBalance() < amount) {
            throw new InvalidStateException("Insufficient balance in source account");
        }

        if (sourceAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new InvalidStateException("Source account is not active");
        }

        if (destinationAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new InvalidStateException("Destination account is not active");
        }

    }

    private void processTransaction(Account sourceAccount, Account destinationAccount, Double amount) {
        sourceAccount.setBalance(sourceAccount.getBalance() - amount);
        destinationAccount.setBalance(destinationAccount.getBalance() + amount);
    }
}
