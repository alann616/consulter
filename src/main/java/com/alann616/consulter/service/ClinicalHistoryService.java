package com.alann616.consulter.service;

import com.alann616.consulter.dto.ClinicalHistoryDTO;
import com.alann616.consulter.enums.DocumentType;
import com.alann616.consulter.model.Patient;
import com.alann616.consulter.model.User;
import com.alann616.consulter.model.doctordocs.ClinicalHistory;
import com.alann616.consulter.repository.ClinicalHistoryRepository;
import com.alann616.consulter.repository.PatientRepository;
import com.alann616.consulter.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class ClinicalHistoryService {
    private final ClinicalHistoryRepository clinicalHistoryRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public ClinicalHistoryService(ClinicalHistoryRepository clinicalHistoryRepository,
                                  PatientRepository patientRepository,
                                  UserRepository userRepository) {
        this.clinicalHistoryRepository = clinicalHistoryRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<ClinicalHistory> getAllClinicalHistories() {
        return clinicalHistoryRepository.findAll();
    }

    public List<ClinicalHistory> getClinicalHistoriesByPatientId(Long patientId) {
        return clinicalHistoryRepository.findByPatientPatientId(patientId);
    }

    public ClinicalHistory getClinicalHistoryById(Long id) {
        return clinicalHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Historia clínica no encontrada con ID: " + id));
    }

    @Transactional
    public ClinicalHistory saveClinicalHistoryFromDTO(ClinicalHistoryDTO dto) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + dto.getPatientId()));

        User doctor = userRepository.findById(dto.getDoctorLicense())
                .orElseThrow(() -> new EntityNotFoundException("User not found with license: " + dto.getDoctorLicense()));

        // Busca la historia por paciente. Usamos Optional para manejar el caso de que no exista.
        Optional<ClinicalHistory> existingHistoryOpt = clinicalHistoryRepository.findByPatientPatientId(dto.getPatientId()).stream().findFirst();

        ClinicalHistory history = existingHistoryOpt.orElse(new ClinicalHistory()); // Si existe la usa, si no, crea una nueva.

        history.setPatient(patient);
        history.setDoctor(doctor);

        // ======================= LA CORRECCIÓN ESTÁ AQUÍ =======================
        // Se añaden verificaciones de nulidad antes de asignar cada valor.

        // --- Campos de Signos Vitales y Antropometría ---
        if (dto.getWeight() != null) history.setWeight(dto.getWeight());
        if (dto.getHeight() != null) history.setHeight(dto.getHeight());
        if (dto.getBodyTemp() != null) history.setBodyTemp(dto.getBodyTemp());
        if (dto.getOxygenSaturation() != null) history.setOxygenSaturation(dto.getOxygenSaturation());
        if (dto.getHeartRate() != null) history.setHeartRate(dto.getHeartRate());
        if (dto.getSystolicBP() != null) history.setSystolicBP(dto.getSystolicBP());
        if (dto.getDiastolicBP() != null) history.setDiastolicBP(dto.getDiastolicBP());
        if (dto.getRespiratoryRate() != null) history.setRespiratoryRate(dto.getRespiratoryRate());
        if (dto.getBodyMassIndex() != null) history.setBodyMassIndex(dto.getBodyMassIndex());
        if (dto.getCapillaryGlycemia() != null) history.setCapillaryGlycemia(dto.getCapillaryGlycemia());
        if (dto.getCephalicPerimeter() != null) history.setCephalicPerimeter(dto.getCephalicPerimeter());
        if (dto.getAbdominalPerimeter() != null) history.setAbdominalPerimeter(dto.getAbdominalPerimeter());

        // --- Campos de texto grandes ---
        history.setCurrentCondition(dto.getCurrentCondition());
        history.setDiagnosticImpression(dto.getDiagnosticImpression());
        history.setTreatment(dto.getTreatment());
        history.setPrognosis(dto.getPrognosis());
        history.setGeneralInspection(dto.getGeneralInspection());
        history.setInstructions(dto.getInstructions());

        // --- Sub-entidades ---
        history.setHereditary(dto.getHereditary());
        history.setNonPathological(dto.getNonPathological());
        history.setPathological(dto.getPathological());
        history.setGynecological(dto.getGynecological());
        history.setPatientInterview(dto.getPatientInterview());
        // ======================= FIN DE LA CORRECCIÓN =======================

        return saveClinicalHistory(history);
    }

    @Transactional
    public ClinicalHistory saveClinicalHistory(ClinicalHistory clinicalHistory) {
        if (clinicalHistory.getTimestamp() == null) {
            clinicalHistory.setTimestamp(LocalDateTime.now());
        }

        clinicalHistory.setDocumentType(DocumentType.CLINICAL_HISTORY);

        if (clinicalHistory.getHereditary() != null) clinicalHistory.getHereditary().setClinicalHistory(clinicalHistory);
        if (clinicalHistory.getNonPathological() != null) clinicalHistory.getNonPathological().setClinicalHistory(clinicalHistory);
        if (clinicalHistory.getPathological() != null) clinicalHistory.getPathological().setClinicalHistory(clinicalHistory);
        if (clinicalHistory.getGynecological() != null) clinicalHistory.getGynecological().setClinicalHistory(clinicalHistory);
        if (clinicalHistory.getPatientInterview() != null) clinicalHistory.getPatientInterview().setClinicalHistory(clinicalHistory);

        boolean isNew = clinicalHistory.getDocumentId() == null;
        ClinicalHistory savedHistory = clinicalHistoryRepository.save(clinicalHistory);

        if (isNew || savedHistory.getDocumentName() == null || savedHistory.getDocumentName().isEmpty()) {
            String documentName = String.format("HistoriaClinica_%d_%s_%s_%s",
                    savedHistory.getDocumentId(),
                    savedHistory.getTimestamp().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                    savedHistory.getPatient().getName().replaceAll("\\s+", ""),
                    savedHistory.getPatient().getLastName().replaceAll("\\s+", "")
            );
            savedHistory.setDocumentName(documentName);
            return clinicalHistoryRepository.save(savedHistory);
        }
        return savedHistory;
    }

    public void deleteClinicalHistory(Long id) {
        if (!clinicalHistoryRepository.existsById(id)) {
            throw new RuntimeException("Historia clínica no encontrada con ID: " + id);
        }
        clinicalHistoryRepository.deleteById(id);
    }
}