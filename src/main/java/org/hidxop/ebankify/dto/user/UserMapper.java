package org.hidxop.ebankify.dto.user;

import org.hidxop.ebankify.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {


    User toEntity(CreateAndUpdateRequestUserDto userDto);

    UserResponseDto toDto(User user);

}