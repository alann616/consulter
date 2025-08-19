package com.alann616.consulter.service;

import com.alann616.consulter.dto.DocumentSummaryDTO;
import com.alann616.consulter.model.doctordocs.ClinicalHistory;
import com.alann616.consulter.model.doctordocs.EvolutionNote;
import com.alann616.consulter.repository.ClinicalHistoryRepository;
import com.alann616.consulter.repository.EvolutionNoteRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DocumentService {

    private final EvolutionNoteRepository evolutionNoteRepository;
    private final ClinicalHistoryRepository clinicalHistoryRepository;

    public DocumentService(EvolutionNoteRepository evolutionNoteRepository, ClinicalHistoryRepository clinicalHistoryRepository) {
        this.evolutionNoteRepository = evolutionNoteRepository;
        this.clinicalHistoryRepository = clinicalHistoryRepository;
    }

    public List<DocumentSummaryDTO> getAllDocuments() {
        // Mapeamos las notas de evolución
        Stream<DocumentSummaryDTO> evolutionNotesStream = evolutionNoteRepository.findAll()
                .stream()
                .map(note -> new DocumentSummaryDTO(
                        note.getDocumentId(),
                        note.getTimestamp(),
                        note.getDocumentType(),
                        "Nota de Evolución",
                        note.getDoctor().getName(),
                        note.getPatient().getName() // Añadimos el nombre del paciente
                ));

        // Mapeamos las historias clínicas
        Stream<DocumentSummaryDTO> clinicalHistoriesStream = clinicalHistoryRepository.findAll()
                .stream()
                .map(history -> new DocumentSummaryDTO(
                        history.getDocumentId(),
                        history.getTimestamp(),
                        history.getDocumentType(),
                        "Historia Clínica",
                        history.getDoctor().getName(),
                        history.getPatient().getName() // Añadimos el nombre del paciente
                ));

        // Combinamos ambas listas
        List<DocumentSummaryDTO> combinedList = Stream.concat(evolutionNotesStream, clinicalHistoriesStream)
                .collect(Collectors.toList());

        // Ordenamos la lista combinada por fecha (la más reciente primero)
        combinedList.sort(Comparator.comparing(DocumentSummaryDTO::getTimestamp).reversed());

        return combinedList;
    }

    public List<DocumentSummaryDTO> getDocumentHistoryForPatient(Long patientId) {
        // Obtenemos todas las notas de evolución y las mapeamos a DTOs
        List<DocumentSummaryDTO> evolutionNotes = evolutionNoteRepository.findByPatientPatientId(patientId)
                .stream()
                .map(note -> new DocumentSummaryDTO(
                        note.getDocumentId(),
                        note.getTimestamp(),
                        note.getDocumentType(),
                        "Nota de Evolución", // Nombre genérico
                        note.getDoctor().getName(),
                        note.getPatient().getName()
                ))
                .collect(Collectors.toList());

        // Obtenemos todas las historias clínicas y las mapeamos a DTOs
        List<DocumentSummaryDTO> clinicalHistories = clinicalHistoryRepository.findByPatientPatientId(patientId)
                .stream()
                .map(history -> new DocumentSummaryDTO(
                        history.getDocumentId(),
                        history.getTimestamp(),
                        history.getDocumentType(),
                        "Historia Clínica", // Nombre genérico
                        history.getDoctor().getName(),
                        history.getPatient().getName()
                ))
                .collect(Collectors.toList());

        // Combinamos ambas listas en una sola
        List<DocumentSummaryDTO> combinedList = new ArrayList<>();
        combinedList.addAll(evolutionNotes);
        combinedList.addAll(clinicalHistories);
        // Aquí añadirías las recetas en el futuro...

        // Ordenamos la lista combinada por fecha, de la más reciente a la más antigua
        combinedList.sort(Comparator.comparing(DocumentSummaryDTO::getTimestamp).reversed());

        return combinedList;
    }
}