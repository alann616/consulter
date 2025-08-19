package com.alann616.consulter.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@MappedSuperclass
public abstract class Document {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long documentId;

    @ManyToOne
    @JoinColumn(name = "doctor_license", nullable = false)
    private User doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonIgnoreProperties({"evolutionNotes", "clinicalHistories"})
    private Patient patient;

    @Column(name = "document_name")
    private String documentName;

    @Column(name = "weight")
    private double weight;

    @Column(name = "height")
    private double height;

    @Column(name = "body_temp")
    private double bodyTemp;

    @Column(name = "oxygen_saturation")
    private int oxygenSaturation;

    @Column(name = "heart_rate")
    private int heartRate;

    @Column(name = "systolic_BP")
    private int systolicBP;

    @Column(name = "diastolic_BP")
    private int diastolicBP;

    @Column(name = "treatment", columnDefinition = "TEXT")
    private String treatment;

    @Column(name = "diagnostic_impression", columnDefinition = "TEXT")
    private String diagnosticImpression;

    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;

    @Column(name = "timestamp", columnDefinition = "TIMESTAMP")
    @NotNull
    private LocalDateTime timestamp;

    @PrePersist
    public void prePersist() {
        timestamp = LocalDateTime.now();
    }
}

