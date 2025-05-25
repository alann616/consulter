package com.alann616.consulter.repository.historytables;

import com.alann616.consulter.model.doctordocs.historytables.NonPathological;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NonPathologicalRepository extends JpaRepository<NonPathological, Long> {
}
