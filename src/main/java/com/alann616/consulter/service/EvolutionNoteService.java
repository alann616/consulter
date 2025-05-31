package com.alann616.consulter.service;

import com.alann616.consulter.model.doctordocs.EvolutionNote;
import com.alann616.consulter.repository.EvolutionNoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EvolutionNoteService {
    EvolutionNoteRepository evolutionNoteRepository;

    public EvolutionNoteService(EvolutionNoteRepository evolutionNoteRepository) {
        this.evolutionNoteRepository = evolutionNoteRepository;
    }

    /**
     * Obtiene todas las notas de evolución.
     * @return Lista de todas las notas de evolución.
     */
    public List<EvolutionNote> getAllEvolutionNotes() {
        return evolutionNoteRepository.findAll();
    }

    /**
     * Obtiene todas las notas de evolución de un paciente específico.
     * @param patientId ID del paciente.
     * @return Lista de notas de evolución del paciente.
     */
    public List<EvolutionNote> getEvolutionNotesByPatientId(Long patientId) {
        return evolutionNoteRepository.findByPatientPatientId(patientId);
    }

    /**
     * Obtiene una nota de evolución por su ID.
     * @param id ID de la nota de evolución.
     * @return Nota de evolución encontrada.
     * @throws RuntimeException si no se encuentra la nota de evolución.
     */
    public EvolutionNote getEvolutionNoteById(Long id) {
        return evolutionNoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nota de evolución no encontrada con ID: " + id));
    }

    /**
     * Guarda una nota de evolución nueva o actualiza una existente.
     * Si la nota de evolución tiene un ID, se actualiza; si no, se crea una nueva.
     * @param evolutionNote Nota de evolución a guardar.
     * @return Nota de evolución guardada (con su ID si es nueva).
     */
    @Transactional
    public EvolutionNote saveEvolutionNote(EvolutionNote evolutionNote) {
        if (evolutionNote.getTimestamp() == null) { // Asegurar timestamp si no viene del cliente
            evolutionNote.setTimestamp(LocalDateTime.now());
        }
        // Si es una nueva nota (sin ID), o si el documentName no está seteado aún.
        boolean isNew = evolutionNote.getDocumentId() == null;

        EvolutionNote savedNote = evolutionNoteRepository.save(evolutionNote);

        if (isNew || savedNote.getDocumentName() == null || savedNote.getDocumentName().isEmpty()) {
            // Generar documentName después del primer guardado para tener el ID
            String documentName = String.format("NotaEvolucion_%d_%s_%s_%s",
                    savedNote.getDocumentId(),
                    savedNote.getTimestamp().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")),
                    savedNote.getPatient().getName().replaceAll("\\s+", ""),
                    savedNote.getPatient().getLastName().replaceAll("\\s+", "")
            );
            savedNote.setDocumentName(documentName);
            return evolutionNoteRepository.save(savedNote); // Guardar de nuevo con el nombre
        }
        return savedNote; // Si es actualización y ya tiene nombre, solo devuelve la nota guardada.
    }

    /**
     * Elimina una nota de evolución por su ID.
     * @param id ID de la nota de evolución a eliminar.
     */
    public void deleteEvolutionNote(Long id) {
        if (!evolutionNoteRepository.existsById(id)) {
            throw new RuntimeException("Nota de evolución no encontrada con ID: " + id);
        }

        evolutionNoteRepository.deleteById(id);
        System.out.println("Nota de evolución eliminada con ID: " + id);
    }
}

