package org.hidxop.ebankify.dto.transaction;

import jakarta.persistence.*;
import org.hidxop.ebankify.domain.entity.Account;
import org.hidxop.ebankify.domain.enumeration.PermanentTransactionFrequencyType;
import org.hidxop.ebankify.domain.enumeration.TransactionStatus;
import org.hidxop.ebankify.domain.enumeration.TransactionType;
import org.hidxop.ebankify.dto.account.AccountBasicResponseDto;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public record TransactionBasicResponseDto(
        UUID uuid,
        Double amount,
        TransactionType type,
        TransactionStatus status,
        AccountBasicResponseDto sourceAccount,
        AccountBasicResponseDto destinationAccount,
        Date createdAt, PermanentTransactionFrequencyType frequencyType, boolean isActive) implements Serializable {
}
