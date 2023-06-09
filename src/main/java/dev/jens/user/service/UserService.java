package dev.jens.user.service;

import dev.jens.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> findUserByEmail(String userEmail);
    void deleteUserByEmail(String userEmail);

    void updateUser(User user);
    List<User> findAllUsers();

    User getUserOrElseThrow(Authentication authentication);

}
