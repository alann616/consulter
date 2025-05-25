package com.alann616.consulter.service;

import com.alann616.consulter.model.doctordocs.EvolutionNote;
import com.alann616.consulter.repository.EvolutionNoteRepository;
import com.alann616.consulter.util.DocumentSummary;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EvolutionNoteService {
    @Autowired
    private EvolutionNoteRepository evolutionNoteRepository;

    public ObservableList<DocumentSummary> getEvolutionNotesSummary(Long patientId) {
        ObservableList<DocumentSummary> evolutionNotesSummary = FXCollections.observableArrayList();

        // Obtener solo los atributos necesarios de las notas de evolución
        List<DocumentSummary> results = evolutionNoteRepository.findDocumentsByPatientId(patientId);
        evolutionNotesSummary.addAll(results);  // Ahora los tipos coinciden

        return evolutionNotesSummary;
    }


    public EvolutionNote saveEvolutionNote(EvolutionNote evolutionNote) {
        return evolutionNoteRepository.save(evolutionNote);
    }

    public EvolutionNote getEvolutionNoteById(Long id) {
        return evolutionNoteRepository.findById(id).orElse(null);
    }

    public void deleteEvolutionNote(Long documentId) {
        EvolutionNote evolutionNote = getEvolutionNoteById(documentId);
        if (evolutionNote != null) {
            evolutionNoteRepository.delete(evolutionNote);
        }
    }
}

