package com.alann616.consulter.enums;

import lombok.Getter;

@Getter
public enum Gender {
    M("Masculino"),
    F("Femenino");

    private final String description;

    Gender(String description) {
        this.description = description;
    }
}
