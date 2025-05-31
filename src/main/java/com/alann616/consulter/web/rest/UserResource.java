package com.alann616.consulter.web.rest;

import com.alann616.consulter.model.User;
import com.alann616.consulter.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
public class UserResource {
    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    /**
     * GET /api/users/login/{license}
     * Endpoint para "iniciar sesión" obteniendo los datos del usuario por su licencia.
     *
     * @param license La licencia del doctor.
     * @return ResponseEntity con el objeto User si se encuentra, o 404 Not Found si no.
     */
    @GetMapping("/login/{license}")
    public ResponseEntity<User> loginUserByLicense(@PathVariable Long license) {
        try {
            User user = userService.getUserById(license); //
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) { // Idealmente, UserNotFoundException
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con licencia: " + license, e);
        }
    }

    /**
     * PUT /api/users/{license}
     * Endpoint para actualizar el perfil de un usuario existente.
     *
     * @param license La licencia del doctor cuyo perfil se va a actualizar.
     * @param userDetails Un objeto User con los nuevos detalles (nombre, teléfono, especialidad).
     * @return ResponseEntity con el objeto User actualizado.
     */
    @PutMapping("/{license}")
    public ResponseEntity<User> updateUserProfile(@PathVariable Long license, @RequestBody User userDetails) {
        try {
            User existingUser = userService.getUserById(license); // Verifica que el usuario exista

            existingUser.setName(userDetails.getName());
            existingUser.setPhone(userDetails.getPhone());
            existingUser.setSpeciality(userDetails.getSpeciality());
            // La licencia (ID) no debería cambiarse.

            User updatedUser = userService.saveUser(existingUser); // saveUser debe manejar la actualización
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) { // Idealmente, UserNotFoundException
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado para actualizar con licencia: " + license, e);
        }
    }
}
