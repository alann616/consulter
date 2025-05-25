package com.alann616.consulter.model.doctordocs;

import com.alann616.consulter.enums.DocumentType;
import com.alann616.consulter.model.Document;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@MappedSuperclass
public abstract class DoctorDocument extends Document {
    @Enumerated(EnumType.STRING) // Para almacenar y recuperar como String
    @Column(name = "document_type", columnDefinition = "ENUM('CLINICAL_HISTORY', 'EVOLUTION_NOTE')")
    private DocumentType documentType;

    @Column(name = "respiratory_rate")
    private int respiratoryRate;

    @Column(name = "current_condition", columnDefinition = "TEXT")
    private String currentCondition;

    @Column(name = "general_inspection", columnDefinition = "TEXT")
    private String generalInspection;

    @Column(name = "prognosis", columnDefinition = "TEXT")
    private String prognosis;
}
