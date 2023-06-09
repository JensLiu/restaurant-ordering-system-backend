package dev.jens.user.controller;

import dev.jens.user.Role;
import dev.jens.user.User;
import dev.jens.user.dto.UserDto;
import dev.jens.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
        User user = getUserOrElseThrow(authentication);
        return ResponseEntity.ok().body(new UserDto(user));
    }

    @PostMapping("/users/me")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto, Authentication authentication) {
        User user = getUserOrElseThrow(authentication);
        log.debug(user.getEmail() + " updated his/her information");
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
        User user = getUserOrElseThrow(authentication);
        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.badRequest().build();
        }
        List<User> users = userService.findAllUsers();
        log.info(user.getEmail() + "accessed all users");
        List<UserDto> responseUsers = users.stream().map(UserDto::new).toList();
        return ResponseEntity.ok().body(responseUsers);
    }

    private User getUserOrElseThrow(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (userDetails == null) {
            throw new RuntimeException("User not found");
        }
        String userEmail = userDetails.getUsername();
        return userService.findUserByEmail(userEmail).orElseThrow();
    }

}
