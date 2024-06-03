package org.grupo4.practica_integradora_g4.controller;

// Importaciones necesarias para el funcionamiento del controlador
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.grupo4.practica_integradora_g4.model.entidades.Usuario;
import org.grupo4.practica_integradora_g4.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller  // Indica que esta clase es un controlador de Spring MVC
public class ControladorLoginUsuarioAdministrador {

    @Autowired  // Inyección de dependencias para el servicio de usuario
    private UsuarioService usuarioService;

    @GetMapping("/loginAdmin")  // Mapeo del método HTTP GET a la URL "/loginAdmin"
    public String login() {
        return "loginAdmin/loginAdmin";  // Devuelve el nombre de la vista para el formulario de login de administrador
    }

    @PostMapping("/loginAdmin")  // Mapeo del método HTTP POST a la URL "/loginAdmin"
    public String loginUser(@RequestParam String email, @RequestParam String clave, HttpSession session, HttpServletResponse response, Model model) {
        // Busca al usuario por su email en la base de datos
        Optional<Usuario> optionalUser = usuarioService.findByEmail(email);
        if (optionalUser.isPresent()) {  // Si el usuario existe
            Usuario usuario = optionalUser.get();  // Obtiene el usuario
            if (clave.equals(usuario.getClave())) {  // Verifica que la clave proporcionada coincida con la del usuario
                // Crea una cookie con el email del usuario
                Cookie cookie = new Cookie("usuarioEmail", usuario.getEmail());
                // Crea una cookie con el número de accesos del usuario
                Cookie accesoCookie = new Cookie("contAccesos", String.valueOf(usuario.getNumeroAccesos()));
                cookie.setMaxAge(60 * 60 * 24 * 7);  // Establece que la cookie es válida por 7 días
                accesoCookie.setMaxAge(60 * 60 * 24 * 7);  // Establece que la cookie es válida por 7 días
                response.addCookie(cookie);  // Añade la cookie a la respuesta HTTP
                response.addCookie(accesoCookie);  // Añade la cookie a la respuesta HTTP
                session.setAttribute("usuario", usuario);  // Guarda el usuario en la sesión
                return "redirect:/administrador/inicio";  // Redirige al inicio del administrador
            } else {
                model.addAttribute("errorClave", "Contraseña y/o Usuario no válido");  // Añade un mensaje de error al modelo
                return "loginAdmin/loginAdmin";  // Devuelve la vista del formulario de login
            }
        } else {
            model.addAttribute("errorUsu", "Contraseña y/o Usuario no válido");  // Añade un mensaje de error al modelo
            return "loginAdmin/loginAdmin";  // Devuelve la vista del formulario de login
        }
    }

    @GetMapping("/logout")  // Mapeo del método HTTP GET a la URL "/logout"
    public String logout(HttpSession session) {
        session.invalidate();  // Invalida la sesión actual, cerrando la sesión del usuario
        return "redirect:/loginAdmin";  // Redirige a la página de login de administrador
    }

}
