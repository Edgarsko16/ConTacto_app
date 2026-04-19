package com.contacto.repository;

import com.contacto.model.Necesidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NecesidadRepository extends JpaRepository<Necesidad, Long> {
    List<Necesidad> findByActivaTrueOrderByIdAsc();
    Optional<Necesidad> findByNombreIgnoreCase(String nombre);
}
