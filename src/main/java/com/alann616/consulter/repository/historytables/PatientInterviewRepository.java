package com.alann616.consulter.repository.historytables;

import com.alann616.consulter.model.doctordocs.historytables.PatientInterview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientInterviewRepository extends JpaRepository<PatientInterview, Long> {

}
