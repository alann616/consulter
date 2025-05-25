package com.alann616.consulter.service;

import com.alann616.consulter.model.Patient;
import com.alann616.consulter.model.doctordocs.ClinicalHistory;
import com.alann616.consulter.model.doctordocs.EvolutionNote;
import com.alann616.consulter.repository.ClinicalHistoryRepository;
import com.alann616.consulter.repository.EvolutionNoteRepository;
import com.alann616.consulter.repository.PatientRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {
    private final ObservableList<Patient> cachedPatients = FXCollections.observableArrayList();
    @Autowired private EvolutionNoteRepository evolutionNoteRepository;
    @Autowired private ClinicalHistoryRepository clinicalHistoryRepository;
    @Autowired private ClinicalHistoryService clinicalHistoryService;
    @Autowired private PatientRepository patientRepository;
    @PersistenceContext
    private EntityManager entityManager;

    public PatientService(PatientRepository patientRepository,
                          EvolutionNoteRepository evolutionNoteRepository, // Asegúrate de inyectar todos
                          ClinicalHistoryRepository clinicalHistoryRepository,
                          ClinicalHistoryService clinicalHistoryService) {
        this.patientRepository = patientRepository;
        this.evolutionNoteRepository = evolutionNoteRepository;
        this.clinicalHistoryRepository = clinicalHistoryRepository;
        this.clinicalHistoryService = clinicalHistoryService;
        this.refreshPatients(); // Carga inicial
    }

    @Cacheable(value = "patients", unless = "#result.isEmpty()")
    public ObservableList<Patient> getAllPatients() {
        System.out.println("getAllPatients called. Cache size: " + cachedPatients.size());
        if(cachedPatients.isEmpty()){ // Recarga si está vacía (podría pasar después de Evict)
            refreshPatients();
        }
        return cachedPatients;
    }

    @Transactional
    @CacheEvict(value = "patients", allEntries = true) // Invalida caché
    public void savePatient(Patient patient) {
        if (patient.getPatientId() != null) {
            // Actualizar paciente existente
            Patient existingPatient = patientRepository.findById(patient.getPatientId()).orElse(null);
            if (existingPatient != null) {
                // Actualizar solo los campos que pueden cambiar
                existingPatient.setName(patient.getName());
                existingPatient.setLastName(patient.getLastName());
                existingPatient.setSecondLastName(patient.getSecondLastName());
                existingPatient.setGender(patient.getGender());
                existingPatient.setBirthDate(patient.getBirthDate());
                existingPatient.setPhone(patient.getPhone());
                existingPatient.setEmail(patient.getEmail());
                existingPatient.setAddress(patient.getAddress());
                // Guardar el paciente actualizado
                patientRepository.save(existingPatient);
            }
        } else {
            // Guardar nuevo paciente
            patientRepository.save(patient);
        }
        refreshPatients();
    }


// En: main/java/com/alann616/consulter/service/PatientService.java

    @Transactional // Muy importante
    @CacheEvict(value = "patients", allEntries = true) // Invalida caché al salir del método (idealmente post-commit)
    public void deletePatient(Long patientId) throws RuntimeException { // Puedes declarar que lanza excepción
        System.out.println("Attempting to delete patient ID: " + patientId);
        try {
            // --- Paso 1: Eliminar Notas de Evolución ---
            List<EvolutionNote> notes = evolutionNoteRepository.findByPatient_PatientId(patientId);
            if (notes != null && !notes.isEmpty()) {
                System.out.println("Found " + notes.size() + " evolution notes to delete.");
                // *** Sugerencia 1: Probar con deleteAll en lugar de deleteAllInBatch ***
                // evolutionNoteRepository.deleteAllInBatch(notes); // Original
                evolutionNoteRepository.deleteAll(notes);      // Prueba con esto
                // entityManager.flush(); // Opcional: Forzar sincronización con BD aquí
                System.out.println("Deleted " + notes.size() + " evolution notes for patient ID: " + patientId);
            } else {
                System.out.println("No evolution notes found for patient ID: " + patientId);
            }

            // --- Paso 2: Eliminar Historias Clínicas ---
            // Esta parte llama a ClinicalHistoryService, que maneja la cascada interna
            List<ClinicalHistory> histories = clinicalHistoryRepository.findByPatientPatientId(patientId);
            if (histories != null && !histories.isEmpty()) {
                System.out.println("Found " + histories.size() + " clinical histories to delete.");
                for (ClinicalHistory history : histories) {
                    // La lógica de eliminar las tablas hijas está en este servicio
                    clinicalHistoryService.deleteClinicalHistory(history.getDocumentId());
                }
                // entityManager.flush(); // Opcional: Forzar sincronización con BD aquí
                System.out.println("Deleted " + histories.size() + " clinical histories for patient ID: " + patientId);
            } else {
                System.out.println("No clinical histories found for patient ID: " + patientId);
            }

            // --- Paso 3: Eliminar el Paciente ---
            System.out.println("Deleting patient entity...");
            Patient patientToDelete = patientRepository.findById(patientId)
                    .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + patientId + " for deletion."));
            patientRepository.delete(patientToDelete);
            entityManager.flush(); // Opcional: Forzar sincronización con BD aquí
            System.out.println("Patient ID: " + patientId + " deleted from repository.");

            refreshPatients();
        } catch (Exception e) { // Captura genérica está bien, pero revisa la causa raíz
            // *** Sugerencia 2: Mejorar el log de errores ***
            System.err.println("ERROR during deletion transaction for patient ID " + patientId + ": " + e.getMessage());
            // Imprimir la causa raíz si existe, puede dar más detalles de JPA/DB
            if (e.getCause() != null) {
                System.err.println("Cause: " + e.getCause().getMessage());
                e.getCause().printStackTrace(); // Stack trace de la causa raíz
            } else {
                e.printStackTrace(); // Stack trace de la excepción principal
            }
            // Lanzar excepción para que la transacción haga rollback y PatientCell sepa que falló
            throw new RuntimeException("Error al eliminar paciente ID " + patientId + ". Revise los logs para más detalles.", e);
        }
        System.out.println("Deletion logic completed for patient ID: " + patientId + ". Transaction commit pending.");
        // NO llames a refreshPatients() aquí, la caché se invalida por @CacheEvict
    }

    // Este método es el que REALMENTE carga de la BD
    private void refreshPatients() {
        System.out.println("Refreshing patient list from DB...");
        try {
            List<Patient> patientsFromDB = patientRepository.findAll();
            System.out.println("Found " + patientsFromDB.size() + " patients in DB.");
            // Asegurarse que la actualización se haga en el hilo de UI si es necesario
            // Platform.runLater(() -> cachedPatients.setAll(patientsFromDB));
            cachedPatients.setAll(patientsFromDB); // Si no es UI directamente, está bien así.
            System.out.println("Patient cache updated.");
        } catch (Exception e) {
            System.err.println("ERROR during refreshPatients: " + e.getMessage());
            e.printStackTrace();
            // Podrías limpiar la caché aquí en caso de error?
            // cachedPatients.clear();
        }
    }
}
