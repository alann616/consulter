package com.alann616.consulter.service;

import com.alann616.consulter.model.User;
import com.alann616.consulter.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Obtiene todos los usuarios.
     * @return Lista de todos los usuarios.
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Obtiene un usuario por su ID.
     * @param license ID del usuario.
     * @return Usuario encontrado.
     * @throws RuntimeException si no se encuentra el usuario.
     */
    public User getUserById(Long license){
        return userRepository.findById(license)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con licencia: " + license));
    }


    /**
     * Guarda un usuario nuevo o actualiza uno existente.
     * Si el usuario tiene un ID, se actualiza; si no, se crea uno nuevo.
     * @param user Usuario a guardar.
     * @return Usuario guardado (con su ID si es nuevo).
     */
    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
