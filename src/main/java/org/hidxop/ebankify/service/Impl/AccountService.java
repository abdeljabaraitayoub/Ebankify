package org.hidxop.ebankify.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.hidxop.ebankify.domain.entity.Account;
import org.hidxop.ebankify.domain.entity.User;
import org.hidxop.ebankify.domain.enumeration.AccountStatus;
import org.hidxop.ebankify.dto.account.AccountBasicResponseDto;
import org.hidxop.ebankify.dto.account.AccountMapper;
import org.hidxop.ebankify.dto.account.AccountResponseDto;
import org.hidxop.ebankify.dto.account.CreateAccountRequestDto;
import org.hidxop.ebankify.exceptionHandling.exceptions.InvalidStateException;
import org.hidxop.ebankify.exceptionHandling.exceptions.NotFoundException;
import org.hidxop.ebankify.repository.AccountRepository;
import org.hidxop.ebankify.repository.UserRepository;
import org.hidxop.ebankify.service.IAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AccountService implements IAccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountMapper accountMapper;

    @Override
    public List<AccountResponseDto> findAll() {
        List<Account> accounts = accountRepository.findAccountWithUsers();
        return accountMapper.toDto(accounts);
    }

    @Override
    public AccountResponseDto findById(UUID id) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new NotFoundException("Account Not Found"));
        return accountMapper.toDto(account);
    }

    @Override
    public List<AccountBasicResponseDto> filterByUser(UUID id) {
        List<Account> accounts = accountRepository.findAccountByUserUuid(id);
        return accountMapper.toBasicDto(accounts);
    }

    @Override
    @Transactional
    public void delete(Account account) {
        try {
            accountRepository.delete(account);
        } catch (Exception e) {
            log.error("Error deleting account: {}", account.getUuid(), e);
            throw new RuntimeException("Failed to delete account", e);
        }
    }

    @Transactional
    @Override
    public void deleteById(UUID id) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new NotFoundException("Account not found"));
        accountRepository.delete(account);
    }

    @Override
    @Transactional
    public AccountResponseDto create(CreateAccountRequestDto accountDto) {
        User user = userRepository.findById(accountDto.userId()).orElseThrow(() -> new NotFoundException("User Not Found"));
        Account account = Account.builder()
                .user(user)
                .status(AccountStatus.ACTIVE)
                .balance(0.00)
                .build();
        return accountMapper.toDto(accountRepository.save(account));
    }

    @Transactional
    @Override
    public AccountResponseDto activate(UUID id) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new NotFoundException("Account Not Found"));
        if (account.getStatus().equals(AccountStatus.ACTIVE)) {
            throw new InvalidStateException("Account is already active");
        }
        account.setStatus(AccountStatus.ACTIVE);
        return accountMapper.toDto(account);
    }

    @Transactional
    @Override
    public AccountResponseDto block(UUID id) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new NotFoundException("Account Not Found"));
        if (account.getStatus().equals(AccountStatus.BLOCKED)) {
            throw new InvalidStateException("Account is already Blocked");
        }
        account.setStatus(AccountStatus.BLOCKED);
        return accountMapper.toDto(account);
    }
}
