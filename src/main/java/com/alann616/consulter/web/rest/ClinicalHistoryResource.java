package com.alann616.consulter.web.rest;

import com.alann616.consulter.service.ClinicalHistoryService;
import com.alann616.consulter.service.PatientService;
import com.alann616.consulter.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clinical-histories")
public class ClinicalHistoryResource {
    private final ClinicalHistoryService clinicalHistoryService;
    private final PatientService patientService;
    private final UserService userService;

    public ClinicalHistoryResource(ClinicalHistoryService clinicalHistoryService, PatientService patientService, UserService userService) {
        this.clinicalHistoryService = clinicalHistoryService;
        this.patientService = patientService;
        this.userService = userService;
    }


}
