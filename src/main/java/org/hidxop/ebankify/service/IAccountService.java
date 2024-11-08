package org.hidxop.ebankify.service;

import org.hidxop.ebankify.domain.entity.Account;
import org.hidxop.ebankify.domain.entity.User;
import org.hidxop.ebankify.dto.account.AccountBasicResponseDto;
import org.hidxop.ebankify.dto.account.AccountResponseDto;
import org.hidxop.ebankify.dto.account.CreateAccountRequestDto;

import java.util.List;
import java.util.UUID;

public interface IAccountService {
    List<AccountResponseDto> findAll();

    AccountResponseDto findById(UUID id);

    List<AccountBasicResponseDto> filterByUser(UUID id);

    void delete(Account account);

    void deleteById(UUID id);

    AccountResponseDto create(CreateAccountRequestDto accountDto);

    AccountResponseDto activate(UUID id);

    AccountResponseDto block(UUID id);

}
