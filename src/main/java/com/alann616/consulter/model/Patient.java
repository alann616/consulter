package com.alann616.consulter.model;

import com.alann616.consulter.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
public class Patient {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long patientId;

    @Column(nullable = false, length = 100)
    @NotNull
    private String name;

    @Column(nullable = false, length = 100)
    @NotNull
    private String lastName;

    @Column(nullable = false, length = 100)
    @NotNull
    private String secondLastName;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Gender gender;

    @Column(name = "birth_date", columnDefinition = "DATE")
    @NotNull
    private LocalDate birthDate;

    @Column(name = "address")
    private String address;

    @Column(name = "phone", columnDefinition = "CHAR(10)")
    private String phone;

    @Column(name = "email")
    @Email
    private String email;

    @Column(name = "timestamp", columnDefinition = "TIMESTAMP")
    @NotNull
    private LocalDateTime timestamp;

    @PrePersist
    public void prePersist() {
        timestamp = LocalDateTime.now();
    }
}
