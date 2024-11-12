package org.hidxop.ebankify.dto.Loan;

import org.hidxop.ebankify.domain.entity.Loan;
import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface LoanMapper {

    //BasicResponseDto
    List<LoanBasicResponseDto> toDto(List<Loan> loan);

    LoanBasicResponseDto toDto(Loan loan);


    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Loan partialUpdate(LoanCreateRequestDto loanCreateRequestDto, @MappingTarget Loan loan);

    //CreateCreateRequest
    @Mapping(source = "userId", target = "user.uuid")
    @Mapping(source = "accountId", target = "account.uuid")
    Loan toEntity(LoanCreateRequestDto loanCreateRequestDto);


}