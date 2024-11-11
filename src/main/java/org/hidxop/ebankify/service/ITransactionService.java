package org.hidxop.ebankify.service;


import org.hidxop.ebankify.domain.entity.Transaction;
import org.hidxop.ebankify.dto.transaction.CreateTransactionRequestDto;
import org.hidxop.ebankify.dto.transaction.TransactionBasicResponseDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface ITransactionService {
    List<TransactionBasicResponseDto> findAll();

    TransactionBasicResponseDto findById(UUID id);


    List<TransactionBasicResponseDto> findByUserId(UUID userId);

    void deleteById(UUID id);

    TransactionBasicResponseDto create(CreateTransactionRequestDto transactionDto);

//    TransactionBasicResponseDto accepteByid(UUID id);

    @Transactional
    TransactionBasicResponseDto accept(UUID id);

    @Transactional
    TransactionBasicResponseDto reject(UUID id);

//    AccountResponseDto create(CreateAccountRequestDto accountDto);
//
//    AccountResponseDto activate(UUID id);
//
//    AccountResponseDto block(UUID id);

}
