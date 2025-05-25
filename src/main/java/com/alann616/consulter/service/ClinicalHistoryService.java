package com.alann616.consulter.service;

import com.alann616.consulter.model.doctordocs.ClinicalHistory;
import com.alann616.consulter.repository.ClinicalHistoryRepository;
import com.alann616.consulter.repository.historytables.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // ✨ Importante

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClinicalHistoryService {
    @Autowired private ClinicalHistoryRepository clinicalHistoryRepository;

    // Repositorios de tablas relacionadas (one-to-one)
    private final HereditaryRepository hereditaryRepository;
    private final NonPathologicalRepository nonPathologicalRepository;
    private final PathologicalRepository pathologicalRepository;
    private final GynecologicalRepository gynecologicalRepository;
    private final PatientInterviewRepository patientInterviewRepository;

    private ObservableList<ClinicalHistory> clinicalHistoryList;

    @Transactional // ✨ Asegura que todo se haga o nada
    public ClinicalHistory saveClinicalHistory(ClinicalHistory clinicalHistory) {
        // Establecer relaciones inversas
        clinicalHistory.getHereditary().setClinicalHistory(clinicalHistory);
        clinicalHistory.getNonPathological().setClinicalHistory(clinicalHistory);
        clinicalHistory.getPathological().setClinicalHistory(clinicalHistory);
        clinicalHistory.getGynecological().setClinicalHistory(clinicalHistory);
        clinicalHistory.getPatientInterview().setClinicalHistory(clinicalHistory);

        // Guardar primero las entidades hijas
        hereditaryRepository.save(clinicalHistory.getHereditary());
        nonPathologicalRepository.save(clinicalHistory.getNonPathological());
        pathologicalRepository.save(clinicalHistory.getPathological());
        gynecologicalRepository.save(clinicalHistory.getGynecological());
        patientInterviewRepository.save(clinicalHistory.getPatientInterview());

        // Luego guardar la historia clínica principal
        return clinicalHistoryRepository.save(clinicalHistory);
    }

    public ObservableList<ClinicalHistory> getClinicalHistory() {
        List<ClinicalHistory> histories = clinicalHistoryRepository.findAll();
        return FXCollections.observableArrayList(histories);
    }

    public ObservableList<ClinicalHistory> getHistoriesByPatient(Long patientId) {
        List<ClinicalHistory> histories = clinicalHistoryRepository.findByPatientPatientId(patientId);
        return FXCollections.observableArrayList(histories);
    }

    public ClinicalHistory getClinicalHistoryById(Long id) {
        return clinicalHistoryRepository.findById(id).orElse(null);
    }

    @Transactional // Asegura atomicidad para esta operación específica
    public void deleteClinicalHistory(Long selectedHistory) {
        // Usamos findById para obtener la entidad administrada por JPA
        ClinicalHistory clinicalHistory = clinicalHistoryRepository.findById(selectedHistory)
                .orElse(null); // Encuentra la historia o devuelve null

        if (clinicalHistory != null) {
            System.out.println("Deleting related entities for ClinicalHistory ID: " + selectedHistory);
            // La cascada ALL *debería* manejar esto, pero la eliminación explícita es más segura
            // Si confías en CascadeType.ALL y ON DELETE CASCADE en BD, podrías omitir esto.
            // Por seguridad, mantenemos la eliminación explícita por ahora:
            if (clinicalHistory.getHereditary() != null) hereditaryRepository.delete(clinicalHistory.getHereditary());
            if (clinicalHistory.getNonPathological() != null) nonPathologicalRepository.delete(clinicalHistory.getNonPathological());
            if (clinicalHistory.getPathological() != null) pathologicalRepository.delete(clinicalHistory.getPathological());
            if (clinicalHistory.getGynecological() != null) gynecologicalRepository.delete(clinicalHistory.getGynecological());
            if (clinicalHistory.getPatientInterview() != null) patientInterviewRepository.delete(clinicalHistory.getPatientInterview());

            System.out.println("Deleting ClinicalHistory entity ID: " + selectedHistory);
            // Luego eliminar la historia clínica principal
            clinicalHistoryRepository.delete(clinicalHistory);
            System.out.println("ClinicalHistory ID: " + selectedHistory + " deleted.");
        } else {
            System.out.println("ClinicalHistory ID: " + selectedHistory + " not found for deletion.");
        }
    }
}
