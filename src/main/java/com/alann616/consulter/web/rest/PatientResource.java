package com.alann616.consulter.web.rest;

import com.alann616.consulter.model.Patient;
import com.alann616.consulter.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientResource {
    private final PatientService patientService;

    public PatientResource(PatientService patientService) {
        this.patientService = patientService;
    }

    /**
     * GET /api/patients/{id}
     * Obtiene la lista de todos los pacientes.
     */
    @GetMapping
    public List<Patient> getAllPatients() {
        return patientService.getAllPatients();
    }

    /**
     * GET /api/patients/{id}
     * Obtiene un paciente por su ID.
     *
     * @param id el ID del paciente
     * @return el paciente con el ID especificado, o 404 Not Found si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        return patientService.getPatientById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
        patient.setPatientId(null); // Asegurarse de que el ID sea nulo para la creación
        Patient savedPatient = patientService.savePatient(patient);

        return new ResponseEntity<>(savedPatient, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @RequestBody Patient patient) {
        try {
            Patient existingPatient = patientService.getPatientByIdOrThrow(id);

            existingPatient.setName(patient.getName());
            existingPatient.setLastName(patient.getLastName());
            existingPatient.setSecondLastName(patient.getSecondLastName());
            existingPatient.setGender(patient.getGender());
            existingPatient.setBirthDate(patient.getBirthDate());
            existingPatient.setAllergies(patient.getAllergies());
            existingPatient.setPhone(patient.getPhone());
            existingPatient.setEmail(patient.getEmail());

            Patient updatedPatient = patientService.savePatient(existingPatient);
            return ResponseEntity.ok(updatedPatient);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente no encontrado con ID: " + id, e);
        }
    }

    @DeleteMapping("/delete/{id}")
    public  ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        try {
            patientService.getPatientByIdOrThrow(id);
            patientService.deletePatient(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente no encontrado con ID: " + id, e);
        }
    }
}
