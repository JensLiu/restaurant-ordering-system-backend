package com.jensdev.user.service;

import com.jensdev.user.modal.User;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface UserService {
    User findUserByEmail(String userEmail);
    void deleteUserByEmail(String userEmail);

    void updateUser(User user);
    List<User> findAllUsers();

    User getUser(Authentication authentication);

}
