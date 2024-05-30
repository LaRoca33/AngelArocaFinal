package org.grupo4.practica_integradora_g4.service;

import org.grupo4.practica_integradora_g4.model.entidades.Usuario;
import org.grupo4.practica_integradora_g4.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public interface UsuarioService {
    Optional<Usuario> findByEmail(String email);
    Usuario save(Usuario usuario);

    List<Usuario> findAll();
    void deleteById(String id);
}
