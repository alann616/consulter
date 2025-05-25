package com.alann616.consulter.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class User {
    @Id
    @Column(name = "doctor_license", nullable = false, unique = true)
    private Long doctorLicense;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone", nullable = false, length = 10)
    private String phone;

    @Column(name = "speciality", nullable = false, length = 80)
    private String speciality;
}
