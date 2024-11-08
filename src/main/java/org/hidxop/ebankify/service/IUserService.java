package org.hidxop.ebankify.service;

import org.hidxop.ebankify.dto.user.CreateAndUpdateRequestUserDto;
import org.hidxop.ebankify.dto.user.UserResponseDto;

import java.util.UUID;

public interface IUserService {
    UserResponseDto create(CreateAndUpdateRequestUserDto userDto);

    UserResponseDto findById(UUID id);
}
