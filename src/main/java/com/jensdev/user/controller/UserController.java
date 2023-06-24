package com.jensdev.user.controller;

import com.jensdev.user.modal.Role;
import com.jensdev.user.modal.User;
import com.jensdev.user.dto.UserDto;
import com.jensdev.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Log4j2
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/users/me")
    public ResponseEntity<UserDto> getPersonalInformation(Authentication authentication) {
        User user = userService.getUser(authentication);
        return ResponseEntity.ok().body(new UserDto(user));
    }

    @PostMapping("/users/me")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto, Authentication authentication) {
        User user = userService.getUser(authentication);
        log.debug(user.getEmail() + " updated his/her/their information");
        if (userDto.getFirstname() != null) {
            user.setFirstname(userDto.getFirstname());
        }
        if (userDto.getLastname() != null) {
            user.setLastname(userDto.getLastname());
        }
        if (userDto.getPassword() != null) {
            user.setHashedPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        userService.updateUser(user);
        return ResponseEntity.ok().body(new UserDto(user));
    }
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getUsers(Authentication authentication) {
        User user = userService.getUser(authentication);
        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.badRequest().build();
        }
        List<User> users = userService.findAllUsers();
        log.info(user.getEmail() + "accessed all users");
        List<UserDto> responseUsers = users.stream().map(UserDto::new).toList();
        return ResponseEntity.ok().body(responseUsers);
    }

}
