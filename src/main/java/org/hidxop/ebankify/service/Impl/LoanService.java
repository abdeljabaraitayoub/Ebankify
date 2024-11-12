package org.hidxop.ebankify.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hidxop.ebankify.domain.entity.Account;
import org.hidxop.ebankify.domain.entity.Loan;
import org.hidxop.ebankify.domain.entity.User;
import org.hidxop.ebankify.domain.enumeration.AccountStatus;
import org.hidxop.ebankify.dto.Loan.LoanBasicResponseDto;
import org.hidxop.ebankify.dto.Loan.LoanCreateRequestDto;
import org.hidxop.ebankify.dto.Loan.LoanMapper;
import org.hidxop.ebankify.exceptionHandling.exceptions.InvalidStateException;
import org.hidxop.ebankify.exceptionHandling.exceptions.NotFoundException;
import org.hidxop.ebankify.repository.AccountRepository;
import org.hidxop.ebankify.repository.LoanRepository;
import org.hidxop.ebankify.repository.UserRepository;
import org.hidxop.ebankify.service.ILoanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ServerErrorException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LoanService implements ILoanService {
    private final LoanMapper loanMapper;
    private final LoanRepository loanRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Override
    public List<LoanBasicResponseDto> findAll() {
        List<Loan> loans = loanRepository.findAll();
        return loanMapper.toDto(loans);
    }

    @Override
    public LoanBasicResponseDto finById(UUID id) {
        Loan loan = loanRepository.findById(id).orElseThrow(() -> new NotFoundException("Loan Not Found."));
        return loanMapper.toDto(loan);
    }

    @Transactional
    @Override
    public LoanBasicResponseDto create(LoanCreateRequestDto loanDto) {
        Account account = accountRepository.findById(loanDto.accountId()).orElseThrow(() -> new NotFoundException("Account Not Found"));
        User user = userRepository.findById(loanDto.userId()).orElseThrow(() -> new NotFoundException("User Not Found"));
        if (account.getStatus().equals(AccountStatus.BLOCKED)) {
            throw new InvalidStateException("This Account is Blocked.");
        }
        Loan loan = loanMapper.toEntity(loanDto);
        loan.setAccount(account);
        loan.setUser(user);
        loanRepository.save(loan);

        return loanMapper.toDto(loan);
    }

}
