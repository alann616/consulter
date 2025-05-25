package com.alann616.consulter.service;

import com.alann616.consulter.model.doctordocs.ClinicalHistory;
import com.alann616.consulter.model.doctordocs.DoctorDocument;
import com.alann616.consulter.model.doctordocs.EvolutionNote;
import com.alann616.consulter.repository.ClinicalHistoryRepository;
import com.alann616.consulter.repository.EvolutionNoteRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {
    @Autowired private EvolutionNoteRepository evolutionNoteRepository;
    @Autowired private ClinicalHistoryRepository clinicalHistoryRepository;

    private final ObservableList<DoctorDocument> documents = FXCollections.observableArrayList();

    public DocumentService(ClinicalHistoryRepository clinicalHistoryRepository,
                            EvolutionNoteRepository evolutionNoteRepository) {
        this.clinicalHistoryRepository = clinicalHistoryRepository;
        this.evolutionNoteRepository = evolutionNoteRepository;
    }

    public ClinicalHistory saveClinicalHistory(ClinicalHistory clinicalHistory) {
        return clinicalHistoryRepository.save(clinicalHistory);
    }

    public void saveEvolutionNote(EvolutionNote evolutionNote) {
        evolutionNoteRepository.save(evolutionNote);
    }


}
