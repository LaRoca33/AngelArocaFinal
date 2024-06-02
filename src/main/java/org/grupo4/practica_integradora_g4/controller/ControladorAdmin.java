package org.grupo4.practica_integradora_g4.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.grupo4.practica_integradora_g4.model.entidades.Cliente;
import org.grupo4.practica_integradora_g4.model.entidades.Genero;
import org.grupo4.practica_integradora_g4.model.entidades.Pais;
import org.grupo4.practica_integradora_g4.model.entidades.Usuario;
import org.grupo4.practica_integradora_g4.model.mongo.Categoria;
import org.grupo4.practica_integradora_g4.model.mongo.Producto;
import org.grupo4.practica_integradora_g4.repositories.ClienteRepository;
import org.grupo4.practica_integradora_g4.repositories.UsuarioRepository;
import org.grupo4.practica_integradora_g4.repositories.mongo.ProductoRepository;
import org.grupo4.practica_integradora_g4.repositories.mongo.CategoriaRepository;
import org.grupo4.practica_integradora_g4.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@Controller
@RequestMapping(value = "administrador")
public class ControladorAdmin {

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


    @GetMapping("/inicio")
    public String getAdministracion(HttpSession session, Model model) {
        if (session.getAttribute("usuario") == null) {
            return "administrador/errorAcceso";
        }
       List<Cliente> clientes = clienteService.findAll();

        model.addAttribute("clientes", clientes);
        model.addAttribute("usuario", session.getAttribute("usuario"));
        return "loginAdmin/administracion";
    }
    @PostMapping("/inicio/eliminar/{id}")
    public String eliminarCliente(@PathVariable("id") String id) {
        UUID uuid = UUID.fromString(id);
        clienteService.deleteById(uuid);
        return "redirect:/administrador/inicio";
    }

    @PostMapping("/inicio/buscar-clientes")
    public String buscarCliente(
            @RequestParam(value = "usuarioEmail.email", required = false) String email,
            @RequestParam(value = "pais.nombre", required = false) String pais,
            Model modelo) {
        List<Cliente> clientes;
        modelo.addAttribute("usuarioEmail.email", email);
        modelo.addAttribute("pais.nombre", pais);

        clientes = clienteService.buscarParam( email,pais);

        modelo.addAttribute("clientes", clientes);

        return "loginAdmin/administracion";
    }
    @GetMapping("/usuarios")
    public String getUsuarios(HttpSession session, Model model) {
        if (session.getAttribute("usuario") == null) {
            return "administrador/errorAcceso";
        }
        List<Usuario> usuarios = usuarioService.findAll();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("usuario", session.getAttribute("usuario"));
        return "administrador/usuario";
    }

    @PostMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable("id") String id) {
        usuarioService.deleteById(id);
        return "redirect:/administrador/usuarios";
    }
    @PostMapping("/bloquear-usuario/{id}")
    @ResponseBody
    public ResponseEntity<String> bloquearUsuario(@PathVariable("id") String usuario_id) {
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



    @PostMapping("/actualizar-cliente/{id}")
    @ResponseBody
    public ResponseEntity<String> actualizarCliente(@PathVariable("id") UUID clienteId, @RequestBody Map<String, String> updateData) {
        try {
            String field = updateData.get("field");
            String value = updateData.get("value");
            System.out.println("Field: " + field + ", Value: " + value);  // Log the field and value

            Optional<Cliente> clienteOpt = clienteService.findById(clienteId);
            if (clienteOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente no encontrado");
            }

            Cliente cliente = clienteOpt.get();

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

            clienteService.save(cliente);
            return ResponseEntity.ok("Cliente actualizado correctamente");
        } catch (Exception e) {
            e.printStackTrace();  // Log the exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el cliente: " + e.getMessage());
        }
    }







    //PRODUCTOS
    @GetMapping("/productos")
    public String listarProductos(HttpSession session, Model model,
                                  @RequestParam(required = false) String sortField,
                                  @RequestParam(required = false) String sortDir) {
        if (session.getAttribute("usuario") == null) {
            return "administrador/errorAcceso";
        }
        Sort sort = Sort.by("cantidadAlmacen"); // default sort
        if (sortField != null && sortDir != null) {
            sort = sortDir.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        }
        model.addAttribute("productos", productoService.findAll(sort));
        model.addAttribute("categorias", categoriaService.findAll());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);

        model.addAttribute("usuario", session.getAttribute("usuario"));
        return "administrador/productos";
    }
    @GetMapping("/productos/add")
    public String getAgregarProducto(HttpSession session,Model model) {
        if (session.getAttribute("usuario") == null) {
            return "administrador/errorAcceso";
        }
        model.addAttribute("nuevoProducto", new Producto());
        model.addAttribute("categorias", categoriaService.findAll());
        model.addAttribute("usuario", session.getAttribute("usuario"));
        return "administrador/addProducto";
    }
    @PostMapping("/productos")
    public String agregarProducto(HttpSession session, @Valid @ModelAttribute("nuevoProducto") Producto producto, BindingResult result, Model model) {
        if (session.getAttribute("usuario") == null) {
            return "administrador/errorAcceso";
        }
        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaService.findAll());
            model.addAttribute("usuario", session.getAttribute("usuario"));
            return "administrador/addProducto";
        }
        productoService.save(producto);

        model.addAttribute("usuario", session.getAttribute("usuario"));
        return "redirect:/administrador/productos";
    }
    @GetMapping("/productos/eliminar")
    public String eliminarProducto(HttpSession session,Model model, @RequestParam String id) {
        if (session.getAttribute("usuario") == null) {
            return "administrador/errorAcceso";
        }
        productoService.deleteById(id);

        model.addAttribute("usuario", session.getAttribute("usuario"));
        return "redirect:/administrador/productos";
    }

    @GetMapping("/productos/categoria/{id}")
    public String listarProductosPorCategoria(HttpSession session, @PathVariable String id, Model model) {
        if (session.getAttribute("usuario") == null) {
            return "administrador/errorAcceso";
        }
        Categoria categoria = categoriaService.findById(id).orElse(null);
        if (categoria != null) {
            List<Producto> productos = productoService.findByCategoriaId(id);
            model.addAttribute("productos", productos);
            model.addAttribute("categoria", categoria);
        } else {
            model.addAttribute("productos", List.of());
            model.addAttribute("categoria", null);
        }

        model.addAttribute("usuario", session.getAttribute("usuario"));
        return "administrador/productosPorCategoria";
    }

    //CATEGORIAS
    @GetMapping("/categorias")
    public String listarCategorias(HttpSession session, Model model) {
        if (session.getAttribute("usuario") == null) {
            return "administrador/errorAcceso";
        }
        model.addAttribute("categorias", categoriaService.findAll());

        model.addAttribute("usuario", session.getAttribute("usuario"));
        return "administrador/listarCategorias";
    }
    @GetMapping("/categorias/add")
    public String getAgregarCategoria(HttpSession session, Model model) {
        if (session.getAttribute("usuario") == null) {
            return "administrador/errorAcceso";
        }
        model.addAttribute("nuevaCategoria", new Categoria());

        model.addAttribute("usuario", session.getAttribute("usuario"));
        return "administrador/addCategoria";
    }
    @PostMapping("/categorias")
    public String agregarCategoria(HttpSession session, @ModelAttribute Categoria categoria, Model model) {
        if (session.getAttribute("usuario") == null) {
            return "administrador/errorAcceso";
        }
        categoriaService.save(categoria);

        model.addAttribute("usuario", session.getAttribute("usuario"));
        return "redirect:/administrador/categorias";
    }
    @GetMapping("/categorias/eliminar")
    public String eliminarCategoria(HttpSession session, @RequestParam String id, Model model) {
        if (session.getAttribute("usuario") == null) {
            return "administrador/errorAcceso";
        }
        categoriaService.deleteById(id);

        model.addAttribute("usuario", session.getAttribute("usuario"));
        return "redirect:/administrador/categorias";
    }

    //USUARIOS ADMINISTRADORES
    @GetMapping("/addAdmin")
    public String getAgregarAdministrador(HttpSession session, Model model){
        if (session.getAttribute("usuario") == null) {
            return "administrador/errorAcceso";
        }
        model.addAttribute("administrador", new Usuario());
        model.addAttribute("usuario", session.getAttribute("usuario"));
        return "administrador/registroAdministrador";
    }
    @PostMapping("/addAdmin")
    public String postAgregarAdministrador(@Valid @ModelAttribute("administrador") Usuario administrador, BindingResult result,HttpSession session, Model model){
        if (session.getAttribute("usuario") == null) {
            return "administrador/errorAcceso";
        }
        if (result.hasErrors()) {
            return "administrador/registroAdministrador";
        }
        if (usuarioService.findByEmail(administrador.getEmail()).isPresent()) {
            model.addAttribute("error", "El correo ya está registrado.");
            return "administrador/registroAdministrador";
        }

        usuarioService.save(administrador);
        model.addAttribute("usuario", session.getAttribute("usuario"));
        return "redirect:/administrador/inicio";

    }


    //CERRAR SESION
    @GetMapping("/cerrar_sesion")
    public String cerrarSesion(HttpSession sesion) {
        sesion.invalidate();
        return "redirect:/loginAdmin";
    }
}
