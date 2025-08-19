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

public class Pathological {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pathologicalId;

    @Column(name = "surgical_history")
    private String surgicalHistory;

    @Column(name = "traumatic_history")
    private String traumaticHistory;

    @Column(name = "allergic_history")
    private String allergicHistory;

    @Column(name = "transfusion_history")
    private String transfusionHistory;

    @Column(name = "coombs_test")
    private String coombsTest;

    @Column(name = "hypertension")
    private String hypertension;

    @Column(name = "diabetes")
    private String diabetes;

    @Column(name = "other_pathological_conditions")
    private String otherPathologicalConditions;

    @ToString.Exclude
    @OneToOne(mappedBy = "pathological")
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
