package com.contacto.service;

import com.contacto.model.User;
import com.contacto.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean authenticate(User user) {
        return authenticateUser(user).isPresent();
    }

    public Optional<User> authenticateUser(User user) {
        if (user == null) {
            return Optional.empty();
        }
        String username = user.getUsername() == null ? "" : user.getUsername().trim();
        String password = user.getPassword() == null ? "" : user.getPassword().trim();
        return userRepository.findByUsernameAndPassword(username, password);
    }
}
