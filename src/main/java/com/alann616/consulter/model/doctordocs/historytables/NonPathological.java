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

public class NonPathological {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nonPathologicalId;

    @Column(name = "marital_status")
    private String maritalStatus;

    @Column(name = "overcrowding", columnDefinition = "TINYINT(1)")
    private boolean overcrowding;

    @Column(name = "promiscuity", columnDefinition = "TINYINT(1)")
    private boolean promiscuity;

    @Column(name = "religion")
    private String religion;

    @Column(name = "occupation")
    private String occupation;

    @Column(name = "floor_material")
    private String floorMaterial;

    @Column(name = "wall_material")
    private String wallMaterial;

    @Column(name = "services")
    private String services;

    @Column(name = "is_fully_vaccinated", columnDefinition = "TINYINT(1)")
    private boolean isFullyVaccinated;

    @Column(name = "substance_use")
    private String substanceUse;

    @Column(name = "is_smoker", columnDefinition = "varchar(255)")
    private String isSmoker;

    @Column(name = "is_drinker", columnDefinition = "varchar(255)")
    private String isDrinker;

    @ToString.Exclude
    @OneToOne(mappedBy = "nonPathological")
    private ClinicalHistory clinicalHistory;

    @Column(name = "timestamp", columnDefinition = "TIMESTAMP")
    @NotNull
    private LocalDateTime timestamp;

    @PrePersist
    public void prePersist() {
        timestamp = LocalDateTime.now();
    }
}
