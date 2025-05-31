package com.alann616.consulter.service;

import com.alann616.consulter.model.Patient;
import com.alann616.consulter.repository.PatientRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {
    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    /**
     * Obtiene todos los pacientes.
     * @return Lista de todos los pacientes.
     */
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    /**
     * Obtiene un paciente por su ID.
     * @param id ID del paciente.
     * @return Paciente encontrado.
     * @throws RuntimeException si no se encuentra el paciente.
     */
    @Transactional(readOnly = true)
    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    /**
     * Obtiene un paciente por su ID o lanza una excepción si no se encuentra.
     * @param id ID del paciente.
     * @return Paciente encontrado.
     * @throws RuntimeException si no se encuentra el paciente.
     */
    public Patient getPatientByIdOrThrow(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + id));
    }

    /**
     * Guarda un paciente nuevo o actualiza uno existente.
     * Si el paciente tiene un ID, se actualiza; si no, se crea uno nuevo.
     * @param patient Paciente a guardar.
     * @return Paciente guardado (con su ID si es nuevo).
     */
    @Transactional
    @CacheEvict(value = "patients", allEntries = true)
    public Patient savePatient(Patient patient) {
        return patientRepository.save(patient);
    }

    /**
     * Elimina un paciente por su ID.
     * @param id ID del paciente a eliminar.
     */
    @Transactional
    @CacheEvict(value = "patients", allEntries = true)
    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new RuntimeException("Paciente no encontrado con ID: " + id);
        }

        patientRepository.deleteById(id);
        System.out.println("Paciente eliminado con ID: " + id);
    }
}
