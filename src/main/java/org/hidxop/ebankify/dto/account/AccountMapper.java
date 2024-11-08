package org.hidxop.ebankify.dto.account;

import org.hidxop.ebankify.domain.entity.Account;
import org.hidxop.ebankify.domain.entity.User;
import org.hidxop.ebankify.dto.user.UserBasicResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AccountMapper {
    @Mapping(target = "user", source = "user")
    AccountResponseDto toDto(Account account);

    List<AccountResponseDto> toDto(List<Account> accounts);

    AccountBasicResponseDto toBasicDto(Account account);

    List<AccountBasicResponseDto> toBasicDto(List<Account> account);


    @Mapping(target = "uuid", source = "uuid")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "age", source = "age")
    @Mapping(target = "monthlyIncome", source = "monthlyIncome")
    @Mapping(target = "creditScore", source = "creditScore")
    @Mapping(target = "role", source = "role")
    UserBasicResponseDto toUserBasicDto(User user);
}