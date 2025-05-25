package com.alann616.consulter.repository;

import com.alann616.consulter.model.doctordocs.ClinicalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClinicalHistoryRepository extends JpaRepository<ClinicalHistory, Long> {
    List<ClinicalHistory> findByPatientPatientId(Long patientId);
}
