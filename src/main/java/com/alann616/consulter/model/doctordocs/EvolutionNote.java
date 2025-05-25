package com.alann616.consulter.model.doctordocs;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class EvolutionNote extends DoctorDocument{
    @Column(name = "treatment_plan", columnDefinition = "TEXT")
    private String treatmentPlan;

    @Column(name = "laboratory_results", columnDefinition = "TEXT")
    private String laboratoryResults;
}
