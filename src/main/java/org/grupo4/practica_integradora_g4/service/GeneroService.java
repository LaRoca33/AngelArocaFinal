package org.grupo4.practica_integradora_g4.service;

import org.grupo4.practica_integradora_g4.model.entidades.Genero;
import org.grupo4.practica_integradora_g4.repositories.GeneroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GeneroService {
    @Autowired
    private GeneroRepository generoRepository;
    public void save(Genero genero){
        generoRepository.save(genero);
    }
}
