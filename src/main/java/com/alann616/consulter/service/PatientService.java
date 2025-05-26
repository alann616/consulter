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
    @CacheEvict(value = "patients", allEntries = true)
    public void savePatient(Patient patient) {
        patientRepository.save(patient);
        refreshPatients();
    }

    @Transactional
    @CacheEvict(value = "patients", allEntries = true)
    public void deletePatient(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new RuntimeException("Paciente no encontrado con ID: " + patientId);
        }
        // Con CascadeType.ALL y orphanRemoval=true en la entidad Patient,
        // JPA se encargará de borrar todas las notas e historias asociadas.
        patientRepository.deleteById(patientId);
        refreshPatients();
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
