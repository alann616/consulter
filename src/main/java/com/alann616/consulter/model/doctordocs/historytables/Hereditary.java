package com.alann616.consulter.model.doctordocs.historytables;

import com.alann616.consulter.model.doctordocs.ClinicalHistory;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Hereditary {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hereditaryId;

    @Column(name = "diabetes_mellitus")
    private String diabetesMellitus;

    @Column(name = "hypertension")
    private String hypertension;

    @Column(name = "tuberculosis")
    private String tuberculosis;

    @Column(name = "neoplasms")
    private String neoplasms;

    @Column(name = "heart_conditions")
    private String heartConditions;

    @Column(name = "congenital_anomalies")
    private String congenitalAnomalies;

    @Column(name = "endocrine_disorders")
    private String endocrineDisorders;

    @Column(name = "other_hereditary_conditions")
    private String otherHereditaryConditions;

    @ToString.Exclude
    @OneToOne(mappedBy = "hereditary")
    @JsonBackReference
    private ClinicalHistory clinicalHistory;

    @Column(name = "timestamp", columnDefinition = "TIMESTAMP")
    @NotNull
    private LocalDateTime timestamp;

    @PrePersist
    public void prePersist() {
        timestamp = LocalDateTime.now();
    }
}
