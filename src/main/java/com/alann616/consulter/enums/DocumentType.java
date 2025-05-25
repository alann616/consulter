package com.alann616.consulter.enums;

import lombok.Getter;

@Getter
public enum DocumentType {
    CLINICAL_HISTORY("Historia Clínica"),
    EVOLUTION_NOTE("Nota de Evolución");

    private final String description;

    DocumentType(String description) {
        this.description = description;
    }
}
