package com.alann616.consulter.service;

import com.alann616.consulter.model.User;
import com.alann616.consulter.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Métodos de CRUD
    public User getUserById(Long license){
        return userRepository.findById(license).orElseThrow(() -> new RuntimeException("Usuario no encontrado con licencia: " + license));
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
