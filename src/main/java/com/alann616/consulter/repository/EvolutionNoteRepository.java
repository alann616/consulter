package com.alann616.consulter.repository;

import com.alann616.consulter.model.doctordocs.EvolutionNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvolutionNoteRepository extends JpaRepository<EvolutionNote, Long> {
    List<EvolutionNote> findByPatientPatientId(Long patientId);
}
