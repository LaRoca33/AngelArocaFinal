package org.grupo4.practica_integradora_g4.repositories;

import org.grupo4.practica_integradora_g4.model.entidades.Genero;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GeneroRepository extends JpaRepository<Genero,String> {
    Genero findBySiglas(String cadenaRotaGenero);

    Optional<Genero> findByGen(String name);
}
