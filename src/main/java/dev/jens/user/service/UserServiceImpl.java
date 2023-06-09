package dev.jens.user.service;

import dev.jens.user.User;
import dev.jens.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findUserByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail);
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
    public User getUserOrElseThrow(Authentication authentication) {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (userDetails == null) {
            throw new RuntimeException("User not found");
        }
        String userEmail = userDetails.getUsername();
        return findUserByEmail(userEmail).orElseThrow();

    }
}
