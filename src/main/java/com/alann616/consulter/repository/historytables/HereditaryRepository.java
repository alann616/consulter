package com.alann616.consulter.repository.historytables;

import com.alann616.consulter.model.doctordocs.historytables.Hereditary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HereditaryRepository extends JpaRepository<Hereditary, Long> {

}
