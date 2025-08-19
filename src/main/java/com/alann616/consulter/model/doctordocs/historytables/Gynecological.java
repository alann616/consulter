package com.alann616.consulter.model.doctordocs.historytables;

import com.alann616.consulter.model.doctordocs.ClinicalHistory;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity

public class Gynecological {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gynecological_id")
    private Long gynecologicalId;

    @Column(name = "menarche_age")
    private int menarcheAge;

    @Column(name = "menstrual_cycle_regularity")
    private String menstrualCycleRegularity;

    @Column(name = "sexual_activity_start_age")
    private int sexualActivityStartAge;

    @Column(name = "last_menstrual_period")
    private LocalDate lastMenstrualPeriod;

    @Column(name = "number_of_pregnancies")
    private int numberOfPregnancies;

    @Column(name = "number_of_births")
    private int numberOfBirths;

    @Column(name = "number_of_abortions")
    private int numberOfAbortions;

    @Column(name = "number_of_cesarean_sections")
    private int numberOfCesareanSections;

    @Column(name = "uterine_curettage")
    private String uterineCurettage;

    @Column(name = "last_delivery_date")
    private LocalDate lastDeliveryDate;

    @Column(name = "macrosomic_children")
    private int macrosomicChildren;

    @Column(name = "low_birth_weight_children")
    private int lowBirthWeightChildren;

    @Column(name = "last_pap_smear_date")
    private LocalDate lastPapSmearDate;

    @Column(name = "family_planning_method")
    private String familyPlanningMethod;

    @Column(name = "contraceptive_usage_duration", columnDefinition = "VARCHAR(255)")
    private String contraceptiveUsageDuration;

    @ToString.Exclude
    @OneToOne(mappedBy = "gynecological")
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
