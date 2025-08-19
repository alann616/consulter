package com.alann616.consulter.service;

import com.alann616.consulter.dto.EvolutionNoteDTO;
import com.alann616.consulter.model.Patient;
import com.alann616.consulter.model.User;
import com.alann616.consulter.model.doctordocs.EvolutionNote;
import com.alann616.consulter.repository.EvolutionNoteRepository;
import com.alann616.consulter.repository.PatientRepository; // <-- Import PatientRepository
import com.alann616.consulter.repository.UserRepository; // <-- Import UserRepository
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EvolutionNoteService {
    private final EvolutionNoteRepository evolutionNoteRepository;
    private final PatientRepository patientRepository; // <-- Add PatientRepository
    private final UserRepository userRepository; // <-- Add UserRepository

    // MODIFIED CONSTRUCTOR
    public EvolutionNoteService(EvolutionNoteRepository evolutionNoteRepository,
                                PatientRepository patientRepository,
                                UserRepository userRepository) {
        this.evolutionNoteRepository = evolutionNoteRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }

    // ... (Your existing methods like getAllEvolutionNotes, etc. remain the same) ...
    public List<EvolutionNote> getAllEvolutionNotes() { return evolutionNoteRepository.findAll(); }
    public List<EvolutionNote> getEvolutionNotesByPatientId(Long patientId) { return evolutionNoteRepository.findByPatientPatientId(patientId); }
    public EvolutionNote getEvolutionNoteById(Long id) { return evolutionNoteRepository.findById(id).orElseThrow(() -> new RuntimeException("Note not found")); }
    public void deleteEvolutionNote(Long id) { evolutionNoteRepository.deleteById(id); }


    // NEW METHOD TO HANDLE THE DTO
    @Transactional
    public EvolutionNote saveEvolutionNoteFromDTO(EvolutionNoteDTO dto) {
        // 1. Fetch the related User and Patient objects from the database using the IDs
        User doctor = userRepository.findById(dto.getDoctorLicense())
                .orElseThrow(() -> new RuntimeException("Doctor not found with license: " + dto.getDoctorLicense()));

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + dto.getPatientId()));

        // 2. Create a new EvolutionNote entity and map the fields from the DTO
        EvolutionNote note = new EvolutionNote();
        note.setDoctor(doctor); // Set the full object
        note.setPatient(patient); // Set the full object

        // Map all other fields from DTO to the entity
        note.setWeight(dto.getWeight());
        note.setHeight(dto.getHeight());
        note.setBodyTemp(dto.getBodyTemp());
        note.setOxygenSaturation(dto.getOxygenSaturation());
        note.setHeartRate(dto.getHeartRate());
        note.setSystolicBP(dto.getSystolicBP());
        note.setDiastolicBP(dto.getDiastolicBP());
        note.setTreatment(dto.getTreatment());
        note.setDiagnosticImpression(dto.getDiagnosticImpression());
        note.setInstructions(dto.getInstructions());
        note.setRespiratoryRate(dto.getRespiratoryRate());
        note.setCurrentCondition(dto.getCurrentCondition());
        note.setGeneralInspection(dto.getGeneralInspection());
        note.setPrognosis(dto.getPrognosis());
        note.setTreatmentPlan(dto.getTreatmentPlan());
        note.setLaboratoryResults(dto.getLaboratoryResults());
        note.setDocumentType(dto.getDocumentType());

        // Set the timestamp
        if (dto.getTimestamp() == null) {
            note.setTimestamp(LocalDateTime.now());
        } else {
            note.setTimestamp(dto.getTimestamp());
        }

        // 3. Save the fully constructed entity
        EvolutionNote savedNote = evolutionNoteRepository.save(note);

        // Generate and set the document name after saving to get the ID
        String documentName = String.format("NotaEvolucion_%d_%s",
                savedNote.getDocumentId(),
                savedNote.getTimestamp().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"))
        );
        savedNote.setDocumentName(documentName);

        // Save again to update the document name
        return evolutionNoteRepository.save(savedNote);
    }
}