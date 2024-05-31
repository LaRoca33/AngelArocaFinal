package org.grupo4.practica_integradora_g4.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.grupo4.practica_integradora_g4.extras.Colecciones;
import org.grupo4.practica_integradora_g4.model.entidades.Usuario;
import org.grupo4.practica_integradora_g4.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class ControladorLoginUsuario {

    @Autowired
    private UsuarioService usuarioService;
    private static final int MAX_INTENTOS_FALLIDOS = 3;

    // Paso 1: Pedir el email del usuario
    @GetMapping("/loginUsuario/email")
    public String mostrarFormularioEmail(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "loginUsuario/loginPaso1";
    }
    @PostMapping("/loginUsuario/email")
    public String verificarEmail(@ModelAttribute Usuario usuario, HttpSession session, Model model) {
        Optional<Usuario> usuarioExistente = usuarioService.findByEmail(usuario.getEmail());
        if (usuarioExistente.isPresent()) {
            Usuario usuarioEncontrado = usuarioExistente.get();
            if (usuarioEncontrado.isBloqueado()) {
                model.addAttribute("usuario_bloqueado", true);
                String fechaDesbloqueoFormateada = usuarioEncontrado.getFechaDesbloqueo().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                model.addAttribute("fechaDesbloqueo", fechaDesbloqueoFormateada);
                return "loginUsuario/loginPaso1";
            }
            session.setAttribute("usuarioTemporal", usuarioEncontrado);
            return "redirect:/loginUsuario/clave";
        } else {
            model.addAttribute("error", "El correo no está registrado.");
            return "loginUsuario/loginPaso1";
        }
    }


    // Paso 2: Pedir la contraseña del usuario
    @GetMapping("/loginUsuario/clave")
    public String mostrarFormularioPassword(HttpSession session, Model model) {
        if (session.getAttribute("usuarioTemporal") == null) {
            return "redirect:/loginUsuario/email";
        }
        model.addAttribute("usuario", new Usuario());
        return "loginUsuario/loginPaso2";
    }

    @PostMapping("/loginUsuario/clave")
    public String verificarPassword(@ModelAttribute Usuario usuario, HttpSession session, Model model) {
        if (session.getAttribute("usuarioTemporal") == null) {
            return "administrador/errorAcceso";
        }
        Usuario usuarioTemporal = (Usuario) session.getAttribute("usuarioTemporal");
        if (usuarioTemporal == null) {
            return "redirect:/loginUsuario/email";
        }
        if (!usuarioTemporal.getClave().equals(usuario.getClave())) {
            usuarioTemporal.setIntentosFallidos(usuarioTemporal.getIntentosFallidos() + 1);
            if (usuarioTemporal.getIntentosFallidos() >= MAX_INTENTOS_FALLIDOS) {
                usuarioTemporal.setBloqueado(true);
                usuarioTemporal.setFechaDesbloqueo(LocalDateTime.now().plusDays(1)); // Bloqueo por 1 día
                model.addAttribute("usuario_bloqueado", true);
                String fechaDesbloqueoFormateada = usuarioTemporal.getFechaDesbloqueo().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                model.addAttribute("fechaDesbloqueo", fechaDesbloqueoFormateada);
                return "loginUsuario/loginPaso1";
            }
            usuarioService.save(usuarioTemporal);
            model.addAttribute("error", "Contraseña incorrecta. Intentos fallidos: " + usuarioTemporal.getIntentosFallidos());
            return "loginUsuario/loginPaso2";
        }
        usuarioTemporal.setIntentosFallidos(0); // Resetear los intentos fallidos
        usuarioService.save(usuarioTemporal);
        session.setAttribute("usuarioAutenticado", usuarioTemporal);
        session.removeAttribute("usuarioTemporal");
        return "redirect:/registro/paso1";
    }

    @GetMapping("/registroUsuario")
    public String mostrarFormularioDeRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "loginUsuario/registrarUsuario";
    }

    @PostMapping("/registroUsuario")
    public String registrarUsuario(@Valid @ModelAttribute("usuario") Usuario usuario, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "loginUsuario/registrarUsuario";
        }

        if (usuarioService.findByEmail(usuario.getEmail()).isPresent()) {
            model.addAttribute("error", "El correo ya está registrado.");
            return "loginUsuario/registrarUsuario";
        }

        usuarioService.save(usuario);
        return "redirect:/loginUsuario/email";
    }

}
