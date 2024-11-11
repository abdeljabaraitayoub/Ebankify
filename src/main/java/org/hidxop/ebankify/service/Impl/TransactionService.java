package org.hidxop.ebankify.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hidxop.ebankify.domain.entity.Account;
import org.hidxop.ebankify.domain.entity.Transaction;
import org.hidxop.ebankify.domain.entity.User;
import org.hidxop.ebankify.domain.enumeration.AccountStatus;
import org.hidxop.ebankify.domain.enumeration.TransactionStatus;
import org.hidxop.ebankify.dto.transaction.CreateTransactionRequestDto;
import org.hidxop.ebankify.dto.transaction.TransactionBasicResponseDto;
import org.hidxop.ebankify.dto.transaction.TransactionMapper;
import org.hidxop.ebankify.exceptionHandling.exceptions.InvalidStateException;
import org.hidxop.ebankify.exceptionHandling.exceptions.NotFoundException;
import org.hidxop.ebankify.repository.AccountRepository;
import org.hidxop.ebankify.repository.TransactionRepository;
import org.hidxop.ebankify.repository.UserRepository;
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
    private final UserRepository userRepository;
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

    @Override
    public List<TransactionBasicResponseDto> findByUserId(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User Not Found."));
        List<Transaction> transactions = transactionRepository.findTransactionBySourceAccount_User(user);
        return transactionMapper.toDto(transactions);
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

        Account sourceAccount = getAccount(transactionDto.sourceAccount(), "Source");
        Account destinationAccount = getAccount(transactionDto.destinationAccount(), "Destination");
        validateTransaction(sourceAccount, destinationAccount, transactionDto.amount());

        return switch (transactionDto.type()) {
            case INSTANT -> handleInstantTransaction(transactionDto, sourceAccount, destinationAccount);
            case STANDARD -> handleStandardTransaction(transactionDto, sourceAccount, destinationAccount);
            case PERMANENT -> handlePermanentTransaction(transactionDto, sourceAccount, destinationAccount);
        };
    }

    private Account getAccount(UUID accountId, String accountType) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException(accountType + " account not found"));
    }

    private TransactionBasicResponseDto handleInstantTransaction(
            CreateTransactionRequestDto transactionDto,
            Account sourceAccount,
            Account destinationAccount
    ) {
        Transaction transaction = createTransaction(
                transactionDto,
                sourceAccount,
                destinationAccount,
                TransactionStatus.COMPLETED
        );
        processTransaction(sourceAccount, destinationAccount, transactionDto.amount());
        Transaction savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toDto(savedTransaction);
    }

    private TransactionBasicResponseDto handleStandardTransaction(
            CreateTransactionRequestDto transactionDto,
            Account sourceAccount,
            Account destinationAccount
    ) {
        Transaction transaction = createTransaction(
                transactionDto,
                sourceAccount,
                destinationAccount,
                TransactionStatus.PENDING
        );
        Transaction savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toDto(savedTransaction);
    }

    private TransactionBasicResponseDto handlePermanentTransaction(
            CreateTransactionRequestDto transactionDto,
            Account sourceAccount,
            Account destinationAccount
    ) {
        // TODO: Implement permanent transaction logic
        throw new UnsupportedOperationException("Permanent transactions not yet implemented");
    }

    private Transaction createTransaction(
            CreateTransactionRequestDto dto,
            Account sourceAccount,
            Account destinationAccount,
            TransactionStatus status
    ) {
        Transaction transaction = transactionMapper.toEntity(dto);
        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(destinationAccount);
        transaction.setStatus(status);
        return transaction;
    }

    private void validateTransaction(Account sourceAccount, Account destinationAccount, Double amount) {
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
        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);
    }

    @Transactional
    @Override
    public TransactionBasicResponseDto accept(UUID id) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new NotFoundException("Transaction Not Found ."));
        if (!transaction.getStatus().equals(TransactionStatus.PENDING)) {
            throw new InvalidStateException("this transaction cannot be Accepted.");
        }

        Account sourceAccount = getAccount(transaction.getSourceAccount().getUuid(), "Source");
        Account destinationAccount = getAccount(transaction.getDestinationAccount().getUuid(), "Destination");
        validateTransaction(sourceAccount, destinationAccount, transaction.getAmount());


        processTransaction(sourceAccount, destinationAccount, transaction.getAmount());
        transaction.setStatus(TransactionStatus.COMPLETED);
        return transactionMapper.toDto(transaction);
    }

    @Transactional
    @Override
    public TransactionBasicResponseDto reject(UUID id) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new NotFoundException("Transaction Not Found ."));
        if (!transaction.getStatus().equals(TransactionStatus.PENDING)) {
            throw new InvalidStateException("this transaction cannot be rejected.");
        }

        Account sourceAccount = getAccount(transaction.getSourceAccount().getUuid(), "Source");
        Account destinationAccount = getAccount(transaction.getDestinationAccount().getUuid(), "Destination");

        transaction.setStatus(TransactionStatus.REJECTED);
        return transactionMapper.toDto(transaction);
    }

    private void additionalFees() {

    }


}
