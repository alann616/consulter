package com.alann616.consulter.dto;

import com.alann616.consulter.enums.DocumentType;
import com.fasterxml.jackson.annotation.JsonProperty; // <-- IMPORTANT: Import this
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EvolutionNoteDTO {
    // Tell Jackson exactly which JSON key to use for this field
    @JsonProperty("doctor_license")
    private Long doctorLicense;

    // And do the same for the patient ID
    @JsonProperty("patient_id")
    private Long patientId;

    // The rest of the fields should map correctly if they already use camelCase
    private String documentName;
    private double weight;
    private double height;
    private double bodyTemp;
    private int oxygenSaturation;
    private int heartRate;
    private int systolicBP;
    private int diastolicBP;
    private String treatment;
    private String diagnosticImpression;
    private String instructions;
    private LocalDateTime timestamp;

    @JsonProperty("document_type")
    private DocumentType documentType;

    @JsonProperty("respiratory_rate")
    private int respiratoryRate;

    @JsonProperty("current_condition")
    private String currentCondition;

    @JsonProperty("general_inspection")
    private String generalInspection;

    private String prognosis;

    @JsonProperty("treatment_plan")
    private String treatmentPlan;

    @JsonProperty("laboratory_results")
    private String laboratoryResults;
}