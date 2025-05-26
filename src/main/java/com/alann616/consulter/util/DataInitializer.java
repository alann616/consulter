package com.alann616.consulter.util;

import com.alann616.consulter.model.User;
import com.alann616.consulter.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepository) {
        return args -> {
            // Verificar si los usuarios ya existen para no insertarlos duplicados
            // Esto es útil si cambias de 'create' a 'update' en el futuro.
            if (userRepository.count() == 0) {
                System.out.println("Base de datos vacía. Creando usuarios iniciales...");

                // Usuario 1: Carlos
                User carlos = new User();
                carlos.setDoctorLicense(11810523L);
                carlos.setName("Carlos Eduardo Martínez Vielma");
                carlos.setPhone("7711090545");
                carlos.setSpeciality("Médico Cirujano");

                // Usuario 2: Vicente
                User vicente = new User();
                vicente.setDoctorLicense(729376L);
                vicente.setName("Vicente Martínez Fragoso");
                vicente.setPhone("7711998725");
                vicente.setSpeciality("Médico Cirujano");

                userRepository.save(carlos);
                userRepository.save(vicente);

                System.out.println("Usuarios creados:");
                userRepository.findAll().forEach(user -> System.out.println(user.getName()));
            } else {
                System.out.println("La base de datos ya contiene datos. No se requiere inicialización.");
            }
        };
    }
}