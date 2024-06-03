package org.grupo4.practica_integradora_g4.controller;

// Importaciones necesarias para el funcionamiento del controlador
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.grupo4.practica_integradora_g4.model.entidades.Cliente;
import org.grupo4.practica_integradora_g4.model.entidades.Genero;
import org.grupo4.practica_integradora_g4.model.entidades.Pais;
import org.grupo4.practica_integradora_g4.model.entidades.Usuario;
import org.grupo4.practica_integradora_g4.model.mongo.Categoria;
import org.grupo4.practica_integradora_g4.model.mongo.Producto;
import org.grupo4.practica_integradora_g4.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

// Indica que esta clase es un controlador Spring MVC
@Controller
@RequestMapping(value = "administrador") // Define la ruta base para todas las solicitudes en este controlador
public class ControladorAdmin {

    // Inyección de dependencias para los servicios necesarios
    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private GeneroService generoService;

    @Autowired
    private PaisService paisService;

    // Maneja la solicitud GET para la ruta /administrador/inicio
    @GetMapping("/inicio")
    public String getAdministracion(HttpSession session, Model model) {
        // Verifica si el usuario está en sesión, si no, redirige a la página de error de acceso
        if (session.getAttribute("usuario") == null) {
            return "administrador/errorAcceso";
        }
        // Obtiene todos los clientes
        List<Cliente> clientes = clienteService.findAll();
        // Añade los clientes y el usuario actual al modelo
        model.addAttribute("clientes", clientes);
        model.addAttribute("usuario", session.getAttribute("usuario"));
        // Devuelve la vista de administración
        return "loginAdmin/administracion";
    }

    // Maneja la solicitud POST para eliminar un cliente por ID
    @PostMapping("/inicio/eliminar/{id}")
    public String eliminarCliente(@PathVariable("id") String id) {
        // Convierte el ID de String a UUID
        UUID uuid = UUID.fromString(id);
        // Elimina el cliente usando el servicio
        clienteService.deleteById(uuid);
        // Redirige a la página de inicio de administración
        return "redirect:/administrador/inicio";
    }

    // Maneja la solicitud POST para buscar clientes con parámetros específicos
    @PostMapping("/inicio/buscar-clientes")
    public String buscarCliente(
            @RequestParam(value = "usuarioEmail.email", required = false) String email,
            @RequestParam(value = "pais.nombre", required = false) List<String> pais,
            @RequestParam(value = "fechaInicio", required = false) LocalDate fechaInicio,
            @RequestParam(value = "fechaFin", required = false) LocalDate fechaFin,
            @RequestParam(value = "salario1", required = false) Integer salario1,
            @RequestParam(value = "salario2", required = false) Integer salario2,
            HttpSession session,
            Model modelo) {
        // Añade los parámetros de búsqueda al modelo
        modelo.addAttribute("usuarioEmail.email", email);
        modelo.addAttribute("pais.nombre", pais);
        modelo.addAttribute("fechaInicio", fechaInicio);
        modelo.addAttribute("fechaFin", fechaFin);
        modelo.addAttribute("salario1", salario1);
        modelo.addAttribute("salario2", salario2);
        // Inicializa los valores predeterminados si son nulos
        if (pais == null) {
            pais = new ArrayList<>();
        }
        if (fechaInicio == null) {
            fechaInicio = LocalDate.of(1000, 1, 1);
        }
        if (fechaFin == null) {
            fechaFin = LocalDate.of(3000, 1, 1);
        }
        if (salario1 == null) {
            salario1 = 0;
        }
        if (salario2 == null) {
            salario2 = 1000000000;
        }
        // Busca los clientes con los parámetros dados
        List<Cliente> clientes = clienteService.buscarParam(email, pais, fechaInicio, fechaFin, salario1, salario2);
        // Añade los clientes y el usuario actual al modelo
        modelo.addAttribute("clientes", clientes);
        modelo.addAttribute("usuario", session.getAttribute("usuario"));
        // Devuelve la vista de administración
        return "loginAdmin/administracion";
    }

    // Maneja la solicitud GET para listar usuarios
    @GetMapping("/usuarios")
    public String getUsuarios(HttpSession session, Model model) {
        // Verifica si el usuario está en sesión, si no, redirige a la página de error de acceso
        if (session.getAttribute("usuario") == null) {
            return "administrador/errorAcceso";
        }
        // Obtiene todos los usuarios
        List<Usuario> usuarios = usuarioService.findAll();
        // Añade los usuarios y el usuario actual al modelo
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("usuario", session.getAttribute("usuario"));
        // Devuelve la vista de usuarios
        return "administrador/usuario";
    }

    // Maneja la solicitud POST para eliminar un usuario por ID
    @PostMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable("id") String id) {
        // Elimina el usuario usando el servicio
        usuarioService.deleteById(id);
        // Redirige a la página de usuarios
        return "redirect:/administrador/usuarios";
    }

    // Maneja la solicitud POST para bloquear un usuario por ID
    @PostMapping("/bloquear-usuario/{id}")
    @ResponseBody // La anotación @ResponseBody en Spring MVC indica que el valor devuelto de un método de
                  // controlador se convertirá directamente en una respuesta HTTP en lugar de resolver un nombre de vista.
                  // el tipo de resultado debe escribirse directamente en el cuerpo de la respuesta en cualquier formato que especifique, como JSON o XML.
    public ResponseEntity<String> bloquearUsuario(@PathVariable("id") String usuario_id) {
        //ResponseEntity es una clase de Spring que representa toda la respuesta HTTP:
        // el código de estado, los encabezados y el cuerpo.
        //Código de estado HTTP: Puedes especificar el código de estado que deseas devolver, como 200 (OK), 404 (Not Found), 500 (Internal Server Error), etc.
        //Encabezados HTTP: Puedes agregar encabezados adicionales a la respuesta.
        //Cuerpo de la respuesta: Contenido que deseas enviar al cliente (puede ser un mensaje, un objeto JSON, etc.).
        try {
            // Intenta bloquear el usuario usando el servicio
            String mensajeOperacion = usuarioService.bloquearUsuario(usuario_id);
            // Si el mensaje indica que el usuario no existe, responde con un estado 404 (Not Found)
            if (mensajeOperacion.equals("El usuario no existe")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajeOperacion);
            }
            // Si el bloqueo fue exitoso, responde con un estado 200 (OK) y el mensaje de la operación
            return ResponseEntity.ok().body(mensajeOperacion);
        } catch (Exception e) {
            // Si ocurre una excepción, responde con un estado 500 (Internal Server Error) y el mensaje de error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al bloquear el usuario: " + e.getMessage());
        }
    }

    // Maneja la solicitud POST para actualizar un cliente por ID
    @PostMapping("/actualizar-cliente/{id}")
    @ResponseBody
    public ResponseEntity<String> actualizarCliente(@PathVariable("id") UUID clienteId, @RequestBody Map<String, String> updateData) {
        //ResponseEntity<String>, que es una clase proporcionada por Spring para representar toda la respuesta HTTP
        // (incluyendo cuerpo, cabeceras y estado).
        try {
            // Obtiene el campo y el valor a actualizar
            String field = updateData.get("field");
            String value = updateData.get("value");
            System.out.println("Field: " + field + ", Value: " + value);  // Log the field and value

            // Busca el cliente por ID
            Optional<Cliente> clienteOpt = clienteService.findById(clienteId);
            if (clienteOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente no encontrado");
            }

            Cliente cliente = clienteOpt.get();

            // Actualiza el campo correspondiente del cliente
            switch (field) {
                case "nombre":
                    cliente.setNombre(value);
                    break;
                case "apellidos":
                    cliente.setApellidos(value);
                    break;
                case "email":
                    Usuario usuario = cliente.getUsuarioEmail();
                    usuario.setEmail(value);
                    usuarioService.save(usuario); // Guardar los cambios del usuario
                    break;
                case "pais":
                    Optional<Pais> paisOpt = paisService.findByName(value);
                    if (paisOpt.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("País no válido");
                    }
                    cliente.setPais(paisOpt.get());
                    break;
                case "genero":
                    Optional<Genero> generoOpt = generoService.findByName(value);
                    if (generoOpt.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Género no válido");
                    }
                    cliente.setGenero(generoOpt.get());
                    break;
                case "telefonoMovil":
                    cliente.setTelefonoMovil(value);
                    break;
                case "tipoDocumentoCliente":
                    cliente.setTipoDocumentoCliente(value);
                    break;
                case "documento":
                    cliente.setDocumento(value);
                    break;
                case "comentarios":
                    cliente.setComentarios(value);
                    break;
                default:
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Campo no válido");
            }

            // Guarda el cliente actualizado
            clienteService.save(cliente);
            return ResponseEntity.ok("Cliente actualizado correctamente");
        } catch (Exception e) {
            e.printStackTrace();  // Log the exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el cliente: " + e.getMessage());
        }
    }

    // Maneja la solicitud GET para listar productos con opciones de ordenación
    @GetMapping("/productos")
    public String listarProductos(HttpSession session, Model model,
                                  @RequestParam(required = false) String sortField,
                                  @RequestParam(required = false) String sortDir) {
        // Verifica si el usuario está en sesión, si no, redirige a la página de error de acceso
        if (session.getAttribute("usuario") == null) {
            return "administrador/errorAcceso";
        }
        // Configura la ordenación predeterminada por cantidad en almacén
        Sort sort = Sort.by("cantidadAlmacen"); // default sort
        // Configura la ordenación según los parámetros recibidos
        if (sortField != null && sortDir != null) {
            sort = sortDir.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        }
        // Añade los productos, categorías y opciones de ordenación al modelo
        model.addAttribute("productos", productoService.findAll(sort));
        model.addAttribute("categorias", categoriaService.findAll());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);

        model.addAttribute("usuario", session.getAttribute("usuario"));
        // Devuelve la vista de productos
        return "administrador/productos";
    }

    // Maneja la solicitud GET para mostrar el formulario de agregar producto
    @GetMapping("/productos/add")
    public String getAgregarProducto(HttpSession session, Model model) {
        // Verifica si el usuario está en sesión, si no, redirige a la página de error de acceso
        if (session.getAttribute("usuario") == null) {
            return "administrador/errorAcceso";
        }
        // Añade un nuevo producto y las categorías al modelo
        model.addAttribute("nuevoProducto", new Producto());
        model.addAttribute("categorias", categoriaService.findAll());
        model.addAttribute("usuario", session.getAttribute("usuario"));
        // Devuelve la vista para agregar producto
        return "administrador/addProducto";
    }

    // Maneja la solicitud POST para agregar un nuevo producto
    @PostMapping("/productos")
    public String agregarProducto(HttpSession session, @Valid @ModelAttribute("nuevoProducto") Producto producto, BindingResult result, Model model) {
        // Verifica si el usuario está en sesión, si no, redirige a la página de error de acceso
        if (session.getAttribute("usuario") == null) {
            return "administrador/errorAcceso";
        }
        // Si hay errores de validación, muestra nuevamente el formulario
        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaService.findAll());
            model.addAttribute("usuario", session.getAttribute("usuario"));
            return "administrador/addProducto";
        }
        // Guarda el nuevo producto
        productoService.save(producto);

        model.addAttribute("usuario", session.getAttribute("usuario"));
        // Redirige a la lista de productos
        return "redirect:/administrador/productos";
    }

    // Maneja la solicitud GET para eliminar un producto por ID
    @GetMapping("/productos/eliminar")
    public String eliminarProducto(HttpSession session, Model model, @RequestParam String id) {
        // Verifica si el usuario está en sesión, si no, redirige a la página de error de acceso
        if (session.getAttribute("usuario") == null) {
            return "administrador/errorAcceso";
        }
        // Elimina el producto usando el servicio
        productoService.deleteById(id);

        model.addAttribute("usuario", session.getAttribute("usuario"));
        // Redirige a la lista de productos
        return "redirect:/administrador/productos";
    }

    // Maneja la solicitud GET para listar productos por categoría
    @GetMapping("/productos/categoria/{id}")
    public String listarProductosPorCategoria(HttpSession session, @PathVariable String id, Model model) {
        // Verifica si el usuario está en sesión, si no, redirige a la página de error de acceso
        if (session.getAttribute("usuario") == null) {
            return "administrador/errorAcceso";
        }
        // Busca la categoría por ID
        Categoria categoria = categoriaService.findById(id).orElse(null);
        if (categoria != null) {
            // Obtiene los productos de la categoría
            List<Producto> productos = productoService.findByCategoriaId(id);
            model.addAttribute("productos", productos);
            model.addAttribute("categoria", categoria);
        } else {
            model.addAttribute("productos", List.of());
            model.addAttribute("categoria", null);
        }

        model.addAttribute("usuario", session.getAttribute("usuario"));
        // Devuelve la vista de productos por categoría
        return "administrador/productosPorCategoria";
    }

    // Maneja la solicitud GET para listar categorías
    @GetMapping("/categorias")
    public String listarCategorias(HttpSession session, Model model) {
        // Verifica si el usuario está en sesión, si no, redirige a la página de error de acceso
        if (session.getAttribute("usuario") == null) {
            return "administrador/errorAcceso";
        }
        // Añade las categorías al modelo
        model.addAttribute("categorias", categoriaService.findAll());

        model.addAttribute("usuario", session.getAttribute("usuario"));
        // Devuelve la vista de listar categorías
        return "administrador/listarCategorias";
    }

    // Maneja la solicitud GET para mostrar el formulario de agregar categoría
    @GetMapping("/categorias/add")
    public String getAgregarCategoria(HttpSession session, Model model) {
        // Verifica si el usuario está en sesión, si no, redirige a la página de error de acceso
        if (session.getAttribute("usuario") == null) {
            return "administrador/errorAcceso";
        }
        // Añade una nueva categoría al modelo
        model.addAttribute("nuevaCategoria", new Categoria());

        model.addAttribute("usuario", session.getAttribute("usuario"));
        // Devuelve la vista para agregar categoría
        return "administrador/addCategoria";
    }

    // Maneja la solicitud POST para agregar una nueva categoría
    @PostMapping("/categorias")
    public String agregarCategoria(HttpSession session, @ModelAttribute Categoria categoria, Model model) {
        // Verifica si el usuario está en sesión, si no, redirige a la página de error de acceso
        if (session.getAttribute("usuario") == null) {
            return "administrador/errorAcceso";
        }
        // Guarda la nueva categoría
        categoriaService.save(categoria);

        model.addAttribute("usuario", session.getAttribute("usuario"));
        // Redirige a la lista de categorías
        return "redirect:/administrador/categorias";
    }

    // Maneja la solicitud GET para eliminar una categoría por ID
    @GetMapping("/categorias/eliminar")
    public String eliminarCategoria(HttpSession session, @RequestParam String id, Model model) {
        // Verifica si el usuario está en sesión, si no, redirige a la página de error de acceso
        if (session.getAttribute("usuario") == null) {
            return "administrador/errorAcceso";
        }
        // Elimina la categoría usando el servicio
        categoriaService.deleteById(id);

        model.addAttribute("usuario", session.getAttribute("usuario"));
        // Redirige a la lista de categorías
        return "redirect:/administrador/categorias";
    }

    // Maneja la solicitud GET para mostrar el formulario de agregar un administrador
    @GetMapping("/addAdmin")
    public String getAgregarAdministrador(HttpSession session, Model model) {
        // Verifica si el usuario está en sesión, si no, redirige a la página de error de acceso
        if (session.getAttribute("usuario") == null) {
            return "administrador/errorAcceso";
        }
        // Añade un nuevo administrador al modelo
        model.addAttribute("administrador", new Usuario());
        model.addAttribute("usuario", session.getAttribute("usuario"));
        // Devuelve la vista para registrar un administrador
        return "administrador/registroAdministrador";
    }

    // Maneja la solicitud POST para agregar un nuevo administrador
    @PostMapping("/addAdmin")
    public String postAgregarAdministrador(@Valid @ModelAttribute("administrador") Usuario administrador, BindingResult result, HttpSession session, Model model) {
        // Verifica si el usuario está en sesión, si no, redirige a la página de error de acceso
        if (session.getAttribute("usuario") == null) {
            return "administrador/errorAcceso";
        }
        // Si hay errores de validación, muestra nuevamente el formulario
        if (result.hasErrors()) {
            return "administrador/registroAdministrador";
        }
        // Verifica si el correo del nuevo administrador ya está registrado
        if (usuarioService.findByEmail(administrador.getEmail()).isPresent()) {
            model.addAttribute("error", "El correo ya está registrado.");
            return "administrador/registroAdministrador";
        }

        // Guarda el nuevo administrador
        usuarioService.save(administrador);
        model.addAttribute("usuario", session.getAttribute("usuario"));
        // Redirige a la página de inicio del administrador
        return "redirect:/administrador/inicio";
    }

    // Maneja la solicitud GET para cerrar sesión
    @GetMapping("/cerrar_sesion")
    public String cerrarSesion(HttpSession sesion) {
        // Invalida la sesión actual
        sesion.invalidate();
        // Redirige a la página de login del administrador
        return "redirect:/loginAdmin";
    }
}
