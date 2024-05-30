package org.grupo4.practica_integradora_g4.service.impl;

import org.grupo4.practica_integradora_g4.model.entidades.Usuario;
import org.grupo4.practica_integradora_g4.repositories.UsuarioRepository;
import org.grupo4.practica_integradora_g4.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Override
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }


    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }


    public void deleteById(String id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public Optional<Usuario> findById(String id) {
        return usuarioRepository.findById(id);
    }


    public String bloquearUsuario(String usuarioId) {
        Optional<Usuario> usuarioOpt = findById(usuarioId);
        if (!usuarioOpt.isPresent()) {
            return "El usuario no existe";
        }
        Usuario usuario = usuarioOpt.get();
        boolean nuevoEstado = !usuario.isBloqueado();
        usuario.setBloqueado(nuevoEstado);
        if (nuevoEstado) {
            LocalDateTime fechaBloqueo = LocalDateTime.now().plusDays(1);
            usuario.setFechaDesbloqueo(fechaBloqueo);
            usuarioRepository.save(usuario);
            return "El usuario ha sido bloqueado hasta " + fechaBloqueo.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        }
        usuario.setFechaDesbloqueo(null);
        usuarioRepository.save(usuario);
        return "El usuario ha sido desbloqueado";
    }
}