package com.alann616.consulter.web.rest;

import com.alann616.consulter.dto.ClinicalHistoryDTO;
import com.alann616.consulter.model.doctordocs.ClinicalHistory;
import com.alann616.consulter.service.ClinicalHistoryService;
import com.alann616.consulter.service.PatientService;
import com.alann616.consulter.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/clinical-histories")
public class ClinicalHistoryResource {

    private final ClinicalHistoryService clinicalHistoryService;

    public ClinicalHistoryResource(ClinicalHistoryService clinicalHistoryService, PatientService patientService, UserService userService) {
        this.clinicalHistoryService = clinicalHistoryService;
    }

    @PostMapping("/save")
    //  CAMBIO: Recibimos el DTO en lugar de la entidad JPA.
    public ResponseEntity<ClinicalHistory> saveClinicalHistory(@RequestBody ClinicalHistoryDTO clinicalHistoryDTO) throws URISyntaxException {
        // El service se encargará de la conversión.
        ClinicalHistory result = clinicalHistoryService.saveClinicalHistoryFromDTO(clinicalHistoryDTO);

        if (clinicalHistoryDTO.getDocumentId() == null) {
            return ResponseEntity.created(new URI("/api/clinical-histories/" + result.getDocumentId())).body(result);
        } else {
            return ResponseEntity.ok().body(result);
        }
    }

    @GetMapping("/by-patient")
    public ResponseEntity<ClinicalHistory> getClinicalHistoryByPatientId(@RequestParam Long patientId) {
        List<ClinicalHistory> histories = clinicalHistoryService.getClinicalHistoriesByPatientId(patientId);
        // Devuelve la primera o notFound si no hay ninguna
        return histories.stream().findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}