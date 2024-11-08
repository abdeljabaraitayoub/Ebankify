package org.hidxop.ebankify.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hidxop.ebankify.domain.entity.User;
import org.hidxop.ebankify.domain.enumeration.Role;
import org.hidxop.ebankify.dto.user.CreateAndUpdateRequestUserDto;
import org.hidxop.ebankify.dto.user.UserResponseDto;
import org.hidxop.ebankify.dto.user.UserMapper;
import org.hidxop.ebankify.exceptionHandling.exceptions.NotFoundException;
import org.hidxop.ebankify.repository.UserRepository;
import org.hidxop.ebankify.service.IUserService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Override
    public UserResponseDto create(CreateAndUpdateRequestUserDto userDto) {
        User user = mapper.toEntity(userDto);
        user.setRole(Role.USER);
        userRepository.save(user);
        return mapper.toDto(user);
    }

    @Override
    public UserResponseDto findById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User Not Found."));
        return mapper.toDto(user);
    }
}
