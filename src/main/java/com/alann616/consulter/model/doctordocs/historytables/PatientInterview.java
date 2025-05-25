package com.alann616.consulter.model.doctordocs.historytables;

import com.alann616.consulter.model.doctordocs.ClinicalHistory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity

public class PatientInterview {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long patientInterviewId;

    @Column(name = "review_of_systems")
    private String reviewOfSystems;

    @Column(name = "general_symptoms")
    private String generalSimptoms;

    @Column(name = "head")
    private String head;

    @Column(name = "neck")
    private String neck;

    @Column(name = "thorax")
    private String thorax;

    @Column(name = "abdomen")
    private String abdomen;

    @Column(name = "backbone")
    private String backbone;

    @Column(name = "external_genitalia")
    private String externalGenitalia;

    @Column(name = "rectal_touch")
    private String rectalTouch;

    @Column(name = "vaginal_touch")
    private String vaginalTouch;

    @Column(name = "limbs")
    private String limbs;

    @ToString.Exclude
    @OneToOne(mappedBy = "patientInterview")
    private ClinicalHistory clinicalHistory;

    @Column(name = "timestamp", columnDefinition = "TIMESTAMP")
    @NotNull
    private LocalDateTime timestamp;

    @PrePersist
    public void prePersist() {
        timestamp = LocalDateTime.now();
    }
}
