package com.alann616.consulter.service;

import com.alann616.consulter.model.doctordocs.ClinicalHistory;
import com.alann616.consulter.repository.ClinicalHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ClinicalHistoryService {
    private final ClinicalHistoryRepository clinicalHistoryRepository;

    public ClinicalHistoryService(ClinicalHistoryRepository clinicalHistoryRepository) {
        this.clinicalHistoryRepository = clinicalHistoryRepository;
    }

    /**
     * Obtiene todas las historias clínicas.
     * @return Lista de todas las historias clínicas.
     */
    @Transactional(readOnly = true)
    public List<ClinicalHistory> getAllClinicalHistories() {
        return clinicalHistoryRepository.findAll();
    }

    /**
     * Obtiene todas las historias clínicas de un paciente específico.
     * @param patientId ID del paciente.
     * @return Lista de historias clínicas del paciente.
     */
    public List<ClinicalHistory> getClinicalHistoriesByPatientId(Long patientId) {
        return clinicalHistoryRepository.findByPatientPatientId(patientId);
    }

    /**
     * Obtiene una historia clínica por su ID.
     * @param id ID de la historia clínica.
     * @return Historia clínica encontrada.
     * @throws RuntimeException si no se encuentra la historia clínica.
     */
    public ClinicalHistory getClinicalHistoryById(Long id) {
        return clinicalHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Historia clínica no encontrada con ID: " + id));
    }

    /**
     * Guarda una historia clínica nueva o actualiza una existente.
     * Si la historia clínica tiene un ID, se actualiza; si no, se crea una nueva.
     * @param clinicalHistory Historia clínica a guardar.
     * @return Historia clínica guardada (con su ID si es nueva).
     */
    @Transactional
    public ClinicalHistory saveClinicalHistory(ClinicalHistory clinicalHistory) {
        if (clinicalHistory.getTimestamp() == null) {
            clinicalHistory.setTimestamp(LocalDateTime.now());
        }

        // Establecer relaciones inversas (owner side of relationship)
        // Esto es crucial para que CascadeType.ALL funcione correctamente al persistir
        // y para que las sub-entidades tengan la FK a ClinicalHistory.
        if (clinicalHistory.getHereditary() != null) clinicalHistory.getHereditary().setClinicalHistory(clinicalHistory);
        if (clinicalHistory.getNonPathological() != null) clinicalHistory.getNonPathological().setClinicalHistory(clinicalHistory);
        if (clinicalHistory.getPathological() != null) clinicalHistory.getPathological().setClinicalHistory(clinicalHistory);
        if (clinicalHistory.getGynecological() != null) clinicalHistory.getGynecological().setClinicalHistory(clinicalHistory);
        if (clinicalHistory.getPatientInterview() != null) clinicalHistory.getPatientInterview().setClinicalHistory(clinicalHistory);

        boolean isNew = clinicalHistory.getDocumentId() == null;
        ClinicalHistory savedHistory = clinicalHistoryRepository.save(clinicalHistory); // JPA guarda el padre y los hijos por cascada.

        if (isNew || savedHistory.getDocumentName() == null || savedHistory.getDocumentName().isEmpty()) {
            // Generar documentName después del primer guardado para tener el ID
            String documentName = String.format("HistoriaClinica_%d_%s_%s_%s",
                    savedHistory.getDocumentId(),
                    savedHistory.getTimestamp().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                    savedHistory.getPatient().getName().replaceAll("\\s+", ""),
                    savedHistory.getPatient().getLastName().replaceAll("\\s+", "")
            );
            savedHistory.setDocumentName(documentName);
            return clinicalHistoryRepository.save(savedHistory); // Guardar de nuevo con el nombre
        }
        return savedHistory;
    }

    /**
     * Elimina una historia clínica por su ID.
     * @param id ID de la historia clínica a eliminar.
     */
    public void deleteClinicalHistory(Long id) {
        if (!clinicalHistoryRepository.existsById(id)) {
            throw new RuntimeException("Historia clínica no encontrada con ID: " + id);
        }

        clinicalHistoryRepository.deleteById(id);
        System.out.println("Historia clínica eliminada con ID: " + id);
    }
}
