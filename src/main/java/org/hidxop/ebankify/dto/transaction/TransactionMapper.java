package org.hidxop.ebankify.dto.transaction;

import org.hidxop.ebankify.domain.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TransactionMapper {
    @Mapping(target = "sourceAccount", source = "sourceAccount")
    @Mapping(target = "destinationAccount", source = "destinationAccount")
    TransactionBasicResponseDto toDto(Transaction transaction);

    List<TransactionBasicResponseDto> toDto(List<Transaction> transactions);
    
    @Mapping(target = "sourceAccount.uuid", source = "sourceAccount")
    @Mapping(target = "destinationAccount.uuid", source = "destinationAccount")
    Transaction toEntity(CreateTransactionRequestDto dto);

}
