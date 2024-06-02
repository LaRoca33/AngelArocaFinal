package org.grupo4.practica_integradora_g4.repositories;

import org.grupo4.practica_integradora_g4.model.entidades.Pais;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaisRepository extends JpaRepository <Pais,String> {
    Pais findBySiglas(String siglaPais);

    Optional<Pais> findByNombre(String name);

}
