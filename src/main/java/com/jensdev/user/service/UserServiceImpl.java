package com.jensdev.user.service;

import com.jensdev.common.exceptions.AuthException;
import com.jensdev.common.exceptions.BusinessException;
import com.jensdev.user.modal.User;
import com.jensdev.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findUserByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail).orElseThrow(
                () -> new BusinessException("User with email " + userEmail + " does not exist")
        );
    }

    @Override
    public void deleteUserByEmail(String userEmail) {
        userRepository.deleteByEmail(userEmail);
    }

    @Override
    public void updateUser(User user) {
        userRepository.save(user);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUser(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (userDetails == null) {
            throw new AuthException("Unauthorized user");
        }
        String userEmail = userDetails.getUsername();
        return findUserByEmail(userEmail);
    }
}
