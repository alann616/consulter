package com.alann616.consulter.repository;

import com.alann616.consulter.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    /**
     * Encuentra el publicId más alto que coincida con el patrón 'PAC-AÑO-%'.
     * Esto nos permite encontrar el último número de secuencia para un año determinado.
     * @param pattern El patrón a buscar, ej: "PAC-2025-%"
     * @return Un Optional que contiene el publicId más alto encontrado.
     */
    @Query(value = "SELECT public_id FROM patient WHERE public_id LIKE ?1 ORDER BY public_id DESC LIMIT 1", nativeQuery = true)
    Optional<String> findTopByPublicIdStartingWithOrderByPublicIdDesc(String pattern);
}
