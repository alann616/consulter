package com.alann616.consulter.repository;

import com.alann616.consulter.model.doctordocs.EvolutionNote;
import com.alann616.consulter.util.DocumentSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvolutionNoteRepository extends JpaRepository<EvolutionNote, Long> {
    @Query("SELECT new com.alann616.consulter.util.DocumentSummary(e.timestamp, e.documentId, e.documentType, e.documentName, e.doctor.name) " +
            "FROM EvolutionNote e WHERE e.patient.patientId = ?1")
    List<DocumentSummary> findDocumentsByPatientId(Long patientId);

    @Query("SELECT e FROM EvolutionNote e WHERE e.patient.patientId = ?1")
    List<EvolutionNote> findByPatient_PatientId(Long patientId);
}
