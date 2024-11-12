package org.hidxop.ebankify.service.Impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hidxop.ebankify.domain.entity.Account;
import org.hidxop.ebankify.domain.entity.Transaction;
import org.hidxop.ebankify.domain.entity.TransactionHistory;
import org.hidxop.ebankify.domain.entity.User;
import org.hidxop.ebankify.domain.enumeration.AccountStatus;
import org.hidxop.ebankify.domain.enumeration.PermanentTransactionFrequencyType;
import org.hidxop.ebankify.domain.enumeration.TransactionStatus;
import org.hidxop.ebankify.domain.enumeration.TransactionType;
import org.hidxop.ebankify.dto.transaction.CreateTransactionRequestDto;
import org.hidxop.ebankify.dto.transaction.TransactionBasicResponseDto;
import org.hidxop.ebankify.dto.transaction.TransactionMapper;
import org.hidxop.ebankify.exceptionHandling.exceptions.InvalidStateException;
import org.hidxop.ebankify.exceptionHandling.exceptions.NotFoundException;
import org.hidxop.ebankify.repository.AccountRepository;
import org.hidxop.ebankify.repository.TransactionHistoryRepository;
import org.hidxop.ebankify.repository.TransactionRepository;
import org.hidxop.ebankify.repository.UserRepository;
import org.hidxop.ebankify.service.ITransactionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class TransactionService implements ITransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionMapper transactionMapper;

    private final TransactionHistoryRepository transactionHistoryRepository;

    @Value("${transaction.standard_bank_transfer_fee}")
    private Double standardBankTransferFee;

    @Value("${transaction.permanent_bank_transfer_fee}")
    private Double permanentBankTransferFee;

    @Value("${transaction.instant_bank_transfer_fee}")
    private Double instantBankTransferFee;

    @Value("${transaction.cross_bank_transfer_fee}")
    private Double crossBankTransferFee;

    @Value("${transaction.min_transaction_amount}")
    private Double minTransactionAmount;

    @Value("${transaction.max_instant_transfer_amount}")
    private Double maxInstantTransferAmount;
    @Value("${transaction.fee_account}")
    private UUID feeAccountId;


    private Account feeAccount;

    @PostConstruct
    public void init() {
        feeAccount = accountRepository.findById(feeAccountId).orElseThrow(() -> new NotFoundException("Fee account Not Found;"));
    }

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
        destinationAccount.setBalance(destinationAccount.getBalance() - amount);
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
        Transaction transaction = createTransaction(
                transactionDto,
                sourceAccount,
                destinationAccount,
                TransactionStatus.PENDING
        );
        Transaction savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toDto(savedTransaction);
    }


    // ┌───────────── second (0-59)
// │ ┌───────────── minute (0 - 59)
// │ │ ┌───────────── hour (0 - 23)
// │ │ │ ┌───────────── day of the month (1 - 31)
// │ │ │ │ ┌───────────── month (1 - 12) (or JAN-DEC)
// │ │ │ │ │ ┌───────────── day of the week (0 - 7)
// │ │ │ │ │ │          (or MON-SUN -- 0 or 7 is Sunday)
// │ │ │ │ │ │
// * * * * * *
// * means match any
// */X means "every X"
    @Transactional
    @Scheduled(cron = "*/10 * * * * *") // Runs every 10 seconds
    public void handlePermanentTransaction() {
        List<Transaction> permanentTransactions = transactionRepository
                .findTransactionByIsActiveAndTypeIsAndStatusIs(
                        true,
                        TransactionType.PERMANENT,
                        TransactionStatus.COMPLETED
                );

        Date now = new Date();

        for (Transaction transaction : permanentTransactions) {

            Date latestExecutionDate = transaction.getHistories().stream()
                    .max(Comparator.comparing(TransactionHistory::getCreatedAt))
                    .map(TransactionHistory::getCreatedAt)
                    .orElse(transaction.getCreatedAt());

            Calendar nextExecution = Calendar.getInstance();
            nextExecution.setTime(latestExecutionDate);

            switch (transaction.getFrequencyType()) {
                case YEARLY -> nextExecution.add(Calendar.YEAR, 1);
                case MONTHLY -> nextExecution.add(Calendar.MONTH, 1);
                case WEEKLY -> nextExecution.add(Calendar.WEEK_OF_YEAR, 1);
                case DAILY -> nextExecution.add(Calendar.DAY_OF_YEAR, 1);
            }


            if (now.after(nextExecution.getTime())) {
                Account sourceAccount = transaction.getSourceAccount();
                Account destinationAccount = transaction.getDestinationAccount();
                Double amount = transaction.getAmount();

                if (sourceAccount.getBalance() < amount) {
                    throw new InvalidStateException("Insufficient balance for permanent transaction");
                }

                sourceAccount.setBalance(sourceAccount.getBalance() - amount);
                destinationAccount.setBalance(destinationAccount.getBalance() + amount);

                TransactionHistory history = TransactionHistory.builder()
                        .transaction(transaction)
                        .createdAt(now)
                        .build();


                accountRepository.save(sourceAccount);
                accountRepository.save(destinationAccount);
                transactionHistoryRepository.save(history);
                transactionRepository.save(transaction);

                log.info("Permanent transaction {} executed successfully", transaction.getUuid());
            }

        }
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
        if (transaction.getType() == TransactionType.PERMANENT) {
            transaction.setFrequencyType(dto.frequencyType());
        }
        Double fees = calculateFees(transaction);
        handleFees(transaction, fees);
        return transaction;
    }

    private void validateTransaction(Account sourceAccount, Account destinationAccount, Double amount) {
        if (amount < minTransactionAmount) {
            throw new InvalidStateException("You can't send less than 0.01");
        }
        if (amount > maxInstantTransferAmount) {
            throw new InvalidStateException("You can't send more than 10000");
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

    private Double calculateFees(Transaction transaction) {
        Double fees = 0.0;

        if (transaction.getType().equals(TransactionType.INSTANT)) {
            fees += instantBankTransferFee;
        } else if (transaction.getType().equals(TransactionType.STANDARD)) {
            fees += standardBankTransferFee;
        } else if (transaction.getType().equals(TransactionType.PERMANENT)) {
            fees += permanentBankTransferFee;

        }

        if (!transaction.getSourceAccount().getBank().equals(transaction.getDestinationAccount().getBank())) {
            fees += crossBankTransferFee;
        }
        return fees;
    }


    private void handleFees(Transaction transaction, Double fees) {
        transaction.getSourceAccount().setBalance(transaction.getSourceAccount().getBalance() - fees);
        feeAccount.setBalance(feeAccount.getBalance() + fees);
    }

    private void refundFees(Transaction transaction, Double fees) {
        transaction.getSourceAccount().setBalance(transaction.getSourceAccount().getBalance() + fees);
        feeAccount.setBalance(feeAccount.getBalance() - fees);
    }
}
