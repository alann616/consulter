package com.alann616.consulter.model;

import com.alann616.consulter.enums.Gender;
import com.alann616.consulter.model.doctordocs.ClinicalHistory;
import com.alann616.consulter.model.doctordocs.EvolutionNote;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Patient {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long patientId;

    @Column(name = "public_id", nullable = false, unique = true, length = 20)
    @NotNull
    private String publicId;

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

    @Column(name = "allergies", columnDefinition = "varchar(255)")
    private String allergies;

    @Column(name = "phone", columnDefinition = "CHAR(10)")
    private String phone;

    @Column(name = "email")
    @Email
    private String email;

    @JsonIgnoreProperties("patient")
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EvolutionNote> evolutionNotes = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ClinicalHistory> clinicalHistories;

    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP")
    @NotNull
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
