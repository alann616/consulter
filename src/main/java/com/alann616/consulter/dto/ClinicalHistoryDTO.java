package com.alann616.consulter.dto;

import com.alann616.consulter.model.doctordocs.historytables.*;
import lombok.Data;

@Data
public class ClinicalHistoryDTO {

    private Long documentId;
    private Long patientId;
    private Long doctorLicense;

    // --- CAMPOS PRIMARIOS AÑADIDOS ---
    private Double weight;
    private Double height;
    private Double bodyTemp;
    private Integer oxygenSaturation;
    private Integer heartRate;
    private Integer systolicBP;
    private Integer diastolicBP;
    private String treatment;
    private String diagnosticImpression;
    private String instructions;
    private Integer respiratoryRate;
    private String currentCondition;
    private String generalInspection;
    private String prognosis;
    private Double bodyMassIndex;
    private Double capillaryGlycemia;
    private Double cephalicPerimeter;
    private Double abdominalPerimeter;

    // --- SUB-ENTIDADES (SIN CAMBIOS) ---
    private Hereditary hereditary;
    private NonPathological nonPathological;
    private Pathological pathological;
    private Gynecological gynecological;
    private PatientInterview patientInterview;
}