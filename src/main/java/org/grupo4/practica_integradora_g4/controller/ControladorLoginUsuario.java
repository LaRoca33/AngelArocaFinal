package org.grupo4.practica_integradora_g4.controller;

// Importaciones necesarias
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.grupo4.practica_integradora_g4.model.entidades.Usuario;
import org.grupo4.practica_integradora_g4.service.ClienteService;
import org.grupo4.practica_integradora_g4.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller // Anotación que indica que esta clase es un controlador de Spring
public class ControladorLoginUsuario {

    @Autowired // Inyección de dependencia del servicio UsuarioService
    private UsuarioService usuarioService;

    @Autowired // Inyección de dependencia del servicio ClienteService
    private ClienteService clienteService;

    private static final int MAX_INTENTOS_FALLIDOS = 3; // Número máximo de intentos fallidos permitidos

    // Paso 1: Pedir el email del usuario
    @GetMapping("/loginUsuario/email")
    public String mostrarFormularioEmail(Model model) {
        model.addAttribute("usuario", new Usuario()); // Añade un objeto Usuario vacío al modelo
        return "loginUsuario/loginPaso1"; // Devuelve la vista para ingresar el email
    }

    @PostMapping("/loginUsuario/email")
    public String verificarEmail(@ModelAttribute Usuario usuario, HttpSession session, Model model) {
        Optional<Usuario> usuarioExistente = usuarioService.findByEmail(usuario.getEmail()); // Verifica si el email existe
        if (usuarioExistente.isPresent()) {
            Usuario usuarioEncontrado = usuarioExistente.get();
            if (usuarioEncontrado.isBloqueado()) { // Verifica si el usuario está bloqueado
                model.addAttribute("usuario_bloqueado", true);
                String fechaDesbloqueoFormateada = usuarioEncontrado.getFechaDesbloqueo().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                model.addAttribute("fechaDesbloqueo", fechaDesbloqueoFormateada);
                model.addAttribute("error", "Usuario bloqueado");
                return "loginUsuario/loginPaso1"; // Devuelve la vista indicando que el usuario está bloqueado
            }
            session.setAttribute("usuarioTemporal", usuarioEncontrado); // Almacena temporalmente el usuario en sesión
            return "redirect:/loginUsuario/clave"; // Redirige al paso 2 para ingresar la contraseña
        } else {
            model.addAttribute("error", "El correo no está registrado."); // Muestra error si el email no está registrado
            return "loginUsuario/loginPaso1"; // Devuelve la vista para ingresar el email
        }
    }

    // Paso 2: Pedir la contraseña del usuario
    @GetMapping("/loginUsuario/clave")
    public String mostrarFormularioPassword(HttpSession session, Model model) {
        if (session.getAttribute("usuarioTemporal") == null) {
            return "redirect:/loginUsuario/email"; // Redirige al paso 1 si no hay usuario temporal en sesión
        }
        model.addAttribute("usuario", new Usuario()); // Añade un objeto Usuario vacío al modelo
        return "loginUsuario/loginPaso2"; // Devuelve la vista para ingresar la contraseña
    }

    @PostMapping("/loginUsuario/clave")
    public String verificarPassword(@ModelAttribute Usuario usuario, HttpSession session, HttpServletResponse response, Model model) {
        if (session.getAttribute("usuarioTemporal") == null) {
            return "administrador/errorAcceso"; // Devuelve vista de error de acceso si no hay usuario temporal en sesión
        }
        Usuario usuarioTemporal = (Usuario) session.getAttribute("usuarioTemporal");
        if (usuarioTemporal == null) {
            return "redirect:/loginUsuario/email"; // Redirige al paso 1 si no hay usuario temporal en sesión
        }
        if (!usuarioTemporal.getClave().equals(usuario.getClave())) { // Verifica si la contraseña es incorrecta
            usuarioTemporal.setIntentosFallidos(usuarioTemporal.getIntentosFallidos() + 1); // Incrementa los intentos fallidos
            if (usuarioTemporal.getIntentosFallidos() >= MAX_INTENTOS_FALLIDOS) {
                usuarioTemporal.setBloqueado(true); // Bloquea el usuario si se supera el máximo de intentos fallidos
                usuarioTemporal.setFechaDesbloqueo(LocalDateTime.now().plusDays(1)); // Establece la fecha de desbloqueo

                model.addAttribute("usuario_bloqueado", true);
                String fechaDesbloqueoFormateada = usuarioTemporal.getFechaDesbloqueo().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                model.addAttribute("fechaDesbloqueo", fechaDesbloqueoFormateada);
                return "loginUsuario/loginPaso1"; // Devuelve la vista indicando que el usuario está bloqueado
            }
            usuarioService.save(usuarioTemporal); // Guarda los cambios del usuario
            model.addAttribute("error", "Contraseña incorrecta. Intentos fallidos: " + usuarioTemporal.getIntentosFallidos());
            return "loginUsuario/loginPaso2"; // Devuelve la vista para ingresar la contraseña con el error
        }
        // Incrementar el contador de accesos y establecer la cookie
        usuarioTemporal.setNumeroAccesos(usuarioTemporal.getNumeroAccesos() + 1); // Incrementa el número de accesos
        usuarioService.save(usuarioTemporal); // Guarda los cambios del usuario

        Cookie cookie = new Cookie("usuarioEmail", usuarioTemporal.getEmail()); // Crea una cookie para el email del usuario
        Cookie accesoCookie = new Cookie("contAccesos", String.valueOf(usuarioTemporal.getNumeroAccesos())); // Crea una cookie para el contador de accesos
        cookie.setMaxAge(60 * 60 * 24 * 7); // Cookie válida por 7 días
        accesoCookie.setMaxAge(60 * 60 * 24 * 7); // Cookie válida por 7 días
        response.addCookie(cookie); // Añade la cookie de email a la respuesta
        response.addCookie(accesoCookie); // Añade la cookie de contador de accesos a la respuesta

        session.setAttribute("usuarioAutenticado", usuarioTemporal); // Establece el usuario autenticado en sesión
        session.removeAttribute("usuarioTemporal"); // Elimina el usuario temporal de la sesión
        if (clienteService.findByUsuario(usuarioTemporal) != null) {
            return "redirect:http://localhost:8081/tienda"; // Redirige a la tienda si es un cliente
        } else {
            return "redirect:/registro/paso1"; // Redirige al registro si no es un cliente
        }
    }

    @GetMapping("/recuperarContraseña")
    public String mostrarFormularioRecuperacion(HttpSession session, Model model) {
        Usuario usuarioTemporal = (Usuario) session.getAttribute("usuarioTemporal");
        if (usuarioTemporal == null) {
            return "redirect:/loginUsuario/email"; // Redirige al paso 1 si no hay usuario temporal en sesión
        }
        model.addAttribute("preguntaRecuperacion", usuarioTemporal.getPregRec()); // Añade la pregunta de recuperación al modelo
        return "loginUsuario/recuperarContraseña"; // Devuelve la vista para recuperar la contraseña
    }
//En teoría, cualquier función puede llamar a un método que funcione con AJAX, siempre que se sigan las reglas y prácticas establecidas para comunicarse con el servidor.
// En el caso de AJAX, generalmente se envía una solicitud HTTP asíncrona al servidor y se maneja la respuesta obtenida.
// Esto permite que una función en el cliente interactúe con el servidor sin necesidad de recargar la página completa.
    @PostMapping("/verificarRespuestaRecuperacion")
    @ResponseBody // Indica que el valor devuelto será el cuerpo de la respuesta HTTP
    public Map<String, String> verificarRespuestaRecuperacion(HttpSession session, @RequestParam String respRec) {
        // Map<String, String>: Es el tipo de retorno del método. Indica que este método devuelve un mapa
        // (una colección que asocia claves a valores). En este caso, tanto las claves como los valores son de tipo String.
        Map<String, String> response = new HashMap<>(); // Crea un mapa para la respuesta
        Usuario usuarioTemporal = (Usuario) session.getAttribute("usuarioTemporal");
        if (usuarioTemporal == null) {
            response.put("error", "Sesión expirada. Intente de nuevo."); // Añade un error si la sesión ha expirado
            return response;
        }
        if (usuarioTemporal.getRespRec().equalsIgnoreCase(respRec)) {
            response.put("clave", usuarioTemporal.getClave()); // Añade la contraseña al mapa si la respuesta de recuperación es correcta
        } else {
            response.put("error", "Respuesta de recuperación incorrecta."); // Añade un error si la respuesta de recuperación es incorrecta
        }
        return response; // Devuelve el mapa de respuesta
    }

    @GetMapping("/registroUsuario")
    public String mostrarFormularioDeRegistro(Model model) {
        model.addAttribute("usuario", new Usuario()); // Añade un objeto Usuario vacío al modelo
        return "loginUsuario/registrarUsuario"; // Devuelve la vista para registrar un nuevo usuario
    }

    @PostMapping("/registroUsuario")
    public String registrarUsuario(@Valid @ModelAttribute("usuario") Usuario usuario, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "loginUsuario/registrarUsuario"; // Devuelve la vista con los errores si hay errores de validación
        }

        if (usuarioService.findByEmail(usuario.getEmail()).isPresent()) {
            model.addAttribute("error", "El correo ya está registrado."); // Muestra error si el email ya está registrado
            return "loginUsuario/registrarUsuario"; // Devuelve la vista para registrar un nuevo usuario
        }

        usuarioService.save(usuario); // Guarda el nuevo usuario
        return "redirect:/loginUsuario/email"; // Redirige al paso 1 del login
    }
}
