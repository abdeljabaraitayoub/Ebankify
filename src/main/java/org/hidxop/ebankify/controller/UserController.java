package org.hidxop.ebankify.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hidxop.ebankify.dto.user.CreateAndUpdateRequestUserDto;
import org.hidxop.ebankify.dto.user.UserResponseDto;
import org.hidxop.ebankify.service.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController()
@RequestMapping("/v1/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> findById(@Valid @PathVariable UUID id) {
        return new ResponseEntity<>(userService.findById(id), HttpStatus.ACCEPTED);
    }

    @PostMapping("")
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody CreateAndUpdateRequestUserDto userDto) {
        return new ResponseEntity<>(userService.create(userDto), HttpStatus.CREATED);
    }


}
