
        package org.grupo4.practica_integradora_g4.controller;

import jakarta.servlet.http.HttpSession;
import org.grupo4.practica_integradora_g4.extras.Colecciones;
import org.grupo4.practica_integradora_g4.model.entidades.*;
import org.grupo4.practica_integradora_g4.model.extra.DatosContacto;
import org.grupo4.practica_integradora_g4.model.extra.DatosPersonales;
import org.grupo4.practica_integradora_g4.model.extra.DatosUsuario;
import org.grupo4.practica_integradora_g4.repositories.GeneroRepository;
import org.grupo4.practica_integradora_g4.repositories.PaisRepository;
import org.grupo4.practica_integradora_g4.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


        @Controller
@RequestMapping(value = "registro")
public class ControladorRegistroCliente {
    @Autowired
    private PaisService paisService;
    @Autowired
    private GeneroService generoService;
    @Autowired
    private PaisRepository paisRepository;
    @Autowired
    private GeneroRepository generoRepository;
    @Autowired
    private TipoClienteService tipoClienteService;
    @Autowired
    private DireccionService direccionService;
    @Autowired
    private ClienteService clienteService;
    @Autowired
    private TarjetaCreditoService tarjetaCreditoService;


    @ModelAttribute("listaGeneros")
    private Map<String, String> getGeneros(){return Colecciones.getGeneros();}

    @ModelAttribute("listaPrefijos")
    private Map<String, String> getPrefijos(){return Colecciones.getGPrefijos();}

    @ModelAttribute("listaPaises")
    private Map<String, String> getNacionalidades(){return Colecciones.getNacionalidades();}

    @ModelAttribute("listaDepartamentos")
    private Map<String, String> getDepartamentos(){return Colecciones.getDepartamentos();}

    @ModelAttribute("listaTiposDocumento")
    private Map<String, String> getTipoDocumento(){return Colecciones.getTipoDocumento();}

    @ModelAttribute("listaTipoCliente")
    private List< TipoCliente> getTipoCliente(){return Colecciones.getTIPOCLIENTES();}

    private boolean registroCompleto;


    //PASO 1
    @GetMapping("paso1")
    private String paso1Get(Cliente cliente,
                            Model model,
                            HttpSession sesion
    ){
        if (sesion.getAttribute("usuarioAutenticado") == null) {
            return "administrador/errorAcceso";
        }
        // Cargar los países y guardarlos en el servicio de países
        Colecciones.getNacionalidades().forEach((siglas, nombre) -> {
            Pais pais = new Pais(nombre, siglas);
            paisService.save(pais);
        });
        Colecciones.getGeneros().forEach((siglas, gen) -> {
            Genero genero = new Genero(gen, siglas);
            generoService.save(genero);
        });
        Colecciones.getTIPOCLIENTES().forEach((tipo) -> {
            TipoCliente tipoCliente = new TipoCliente(tipo.getTipo(),tipo.getSiglas(),tipo.getGastoUmbral(),tipo.getPorcentajeDescuento());
            tipoClienteService.save(tipoCliente);
        });

        // Añadir la lista de países al modelo
        model.addAttribute("listaP",paisRepository.findAll());
        model.addAttribute("listaG",generoRepository.findAll());
        if (sesion.getAttribute("datos_personales")!=null){
            cliente=(Cliente) sesion.getAttribute("datos_personales");
            model.addAttribute("clientePlantilla", cliente);
        }else model.addAttribute("clientePlantilla",cliente);

        model.addAttribute("usuarioAutenticado", sesion.getAttribute("usuarioAutenticado"));
        return "registro/paso1";
    }


    @PostMapping("paso1")
    private String paso1Post(
            @Validated({DatosPersonales.class})
            @ModelAttribute("clientePlantilla") Cliente cliente,
            BindingResult posiblesErrores,
            @RequestParam("pais") String siglaPais,
            @RequestParam("genero") String siglaGenero,
            HttpSession sesion,
            Model model
    ){
        if (sesion.getAttribute("usuarioAutenticado") == null) {
            return "administrador/errorAcceso";
        }
        // Añadir la lista de países y generos al modelo
        model.addAttribute("listaP", paisRepository.findAll());
        model.addAttribute("listaG", generoRepository.findAll());

        // Obtener la sigla del país  y genero seleccionado en el formulario

        System.out.println(siglaPais);
        String cadenaRotaPais = siglaPais.split(",")[0];
        System.out.println(siglaGenero);


        // Buscar el país y genero en la base de datos por su sigla
        Pais paisSeleccionado = paisRepository.findBySiglas(cadenaRotaPais);
        Genero generoSeleccionado = generoRepository.findBySiglas(siglaGenero);
        // Si se encuentra el país, asignarlo al cliente
        if (paisSeleccionado != null) {
            cliente.setPais(paisSeleccionado);
            sesion.setAttribute("datos_personales", cliente);
        } else {
            model.addAttribute("error", "Pais no existente");
        }
        if (generoSeleccionado != null) {
            cliente.setGenero(generoSeleccionado);
            sesion.setAttribute("datos_personales", cliente);
        } else {
            model.addAttribute("error", "Pais no existente");
        }

        if (posiblesErrores.hasErrors()) {
            System.out.println(posiblesErrores.getAllErrors());

            model.addAttribute("usuarioAutenticado", sesion.getAttribute("usuarioAutenticado"));
            return "registro/paso1";
        }
        else {
            System.out.println(cliente.toString());
            sesion.setAttribute("datos_personales", cliente);

            model.addAttribute("usuarioAutenticado", sesion.getAttribute("usuarioAutenticado"));
            return "redirect:/registro/paso2";
        }
    }


    //PASO 2
    @GetMapping("paso2")
    private String paso2Get(Cliente cliente,
                            Model model,
                            HttpSession sesion
    ){
        if (sesion.getAttribute("usuarioAutenticado") == null) {
            return "administrador/errorAcceso";
        }
        System.out.println(cliente
                .toString());
        if (sesion.getAttribute("datos_contacto")!=null){
            cliente=(Cliente) sesion.getAttribute("datos_contacto");
            model.addAttribute("clientePlantilla", cliente);
        }else model.addAttribute("clientePlantilla",cliente);

        model.addAttribute("usuarioAutenticado", sesion.getAttribute("usuarioAutenticado"));
        return "registro/paso2";
    }
    @PostMapping("paso2")
    private String paso2Post(
            @Validated({DatosContacto.class})
            @ModelAttribute("clientePlantilla")Cliente cliente,
            BindingResult posiblesErrores,
            HttpSession sesion,
            Model model
    ){
        if (sesion.getAttribute("usuarioAutenticado") == null) {
            return "administrador/errorAcceso";
        }
        if (posiblesErrores.hasErrors()) {
            System.out.println(posiblesErrores.getAllErrors());
            return "registro/paso2";
        } else {

            System.out.println(cliente.toString());
            sesion.setAttribute("datos_contacto", cliente);

            model.addAttribute("usuarioAutenticado", sesion.getAttribute("usuarioAutenticado"));
            return "redirect:/registro/paso3";
        }
    }

    //PASO 3
    @GetMapping("paso3")
    private String paso3Get(Cliente cliente,
                            Model model,
                            HttpSession sesion


    ){
        if (sesion.getAttribute("usuarioAutenticado") == null) {
            return "administrador/errorAcceso";
        }


        if (sesion.getAttribute("datos_usuario")!=null){
            cliente=(Cliente) sesion.getAttribute("datos_usuario");

            model.addAttribute("clientePlantilla", cliente);
        }else model.addAttribute("clientePlantilla",cliente);
        model.addAttribute("usuarioAutenticado", sesion.getAttribute("usuarioAutenticado"));
        return "registro/paso3";
    }

    @PostMapping("paso3")
    private String paso3Post(
            @Validated({DatosUsuario.class})
            @ModelAttribute("clientePlantilla") Cliente cliente,
            @RequestParam("numero") Integer numero,
            @RequestParam("tipoTarjeta") String tipoTarjeta,
            @RequestParam("cvv") String cvv,
            @RequestParam("fechaCad") LocalDate fechaCad,
            BindingResult posiblesErrores,
            HttpSession sesion,
            Model model
    ){
        if (sesion.getAttribute("usuarioAutenticado") == null) {
            return "administrador/errorAcceso";
        }
        if (posiblesErrores.hasErrors()) {
            System.out.println(posiblesErrores.getAllErrors());
            return "registro/paso3";
        } else {

            // Procesar las tarjetas de crédito y agregar al cliente

            cliente.addItem(new TarjetaCredito(numero,tipoTarjeta,cvv,fechaCad,cliente));


            // Guardar el cliente en la base de datos, si es necesario
            // clienteService.save(cliente);
            System.out.println(cliente.getTarjetasCredito());
            sesion.setAttribute("datos_usuario", cliente);

            model.addAttribute("usuarioAutenticado", sesion.getAttribute("usuarioAutenticado"));
            return "redirect:/registro/resumen";
        }
    }


    @GetMapping("resumen")
    private String resumenGet(Cliente cliente, Model model, HttpSession sesion) {
        if (sesion.getAttribute("usuarioAutenticado") == null) {
            return "administrador/errorAcceso";
        }

        cliente = new Cliente();
        if (sesion.getAttribute("datos_personales") != null) {
            Cliente datos_personales = (Cliente) sesion.getAttribute("datos_personales");
            cliente.setGenero(datos_personales.getGenero());
            cliente.setPais(datos_personales.getPais());
            cliente.setFechaNacimiento(datos_personales.getFechaNacimiento());
            cliente.setTipoDocumentoCliente(datos_personales.getTipoDocumentoCliente());
            cliente.setDocumento(datos_personales.getDocumento());
            cliente.setNombre(datos_personales.getNombre());
            cliente.setApellidos(datos_personales.getApellidos());
        }
        if (sesion.getAttribute("datos_contacto") != null) {
            Cliente datos_contacto = (Cliente) sesion.getAttribute("datos_contacto");
            cliente.setTelefonoMovil(datos_contacto.getTelefonoMovil());
        }
        if (sesion.getAttribute("datos_usuario") != null) {
            Cliente datos_usuario = (Cliente) sesion.getAttribute("datos_usuario");
            cliente.setComentarios(datos_usuario.getComentarios());


        }

        Usuario usuAut = (Usuario) sesion.getAttribute("usuarioAutenticado");
        if (usuAut == null) {
            return "error";  // Maneja el caso donde el usuario no está en la sesión
        }

        cliente.setUsuarioEmail(usuAut);

        clienteService.save(cliente);


        if (sesion.getAttribute("datos_contacto") != null) {
            Cliente datos_contacto = (Cliente) sesion.getAttribute("datos_contacto");
            Direccion direccion = datos_contacto.getDirecciones();
            direccion.setCliente(cliente);
            direccionService.save(direccion);
            cliente.setDirecciones(direccion);
        }
        if (sesion.getAttribute("datos_usuario") != null) {
            Cliente datos_usuario = (Cliente) sesion.getAttribute("datos_usuario");
            for (TarjetaCredito tarjeta : datos_usuario.getTarjetasCredito()) {
                tarjeta.setCliente(cliente); // Establecer el cliente en cada tarjeta
            }
            tarjetaCreditoService.save(datos_usuario.getTarjetasCredito()); // Guardar las tarjetas de crédito
        }
        model.addAttribute("clientePlantilla", cliente);
        sesion.setAttribute("clienteFinal", cliente);
        registroCompleto = true;

        model.addAttribute("usuarioAutenticado", sesion.getAttribute("usuarioAutenticado"));
        return "registro/resumen";
    }

    @PostMapping("resumen")
    private String resumen(
            Model model,
            HttpSession sesion
    ){
        if (sesion.getAttribute("usuarioAutenticado") == null) {
            return "administrador/errorAcceso";
        }
        Cliente cliente = (Cliente) sesion.getAttribute("clienteFinal");
        model.addAttribute("clientePlantilla", cliente);

        if (registroCompleto) {
            registroCompleto=false;
            Colecciones.addCliente(cliente);
            sesion.invalidate();
            return "redirect:http://localhost:8081/tienda";
        }
        else {
            model.addAttribute("usuarioAutenticado", sesion.getAttribute("usuarioAutenticado"));
            return "registro/resumen";
        }
    }

    @PostMapping("cerrar-sesion")
    private String cerrarSesion(HttpSession sesion){
        if (sesion.getAttribute("usuarioAutenticado") == null) {
            return "administrador/errorAcceso";
        }
        registroCompleto=false;
        sesion.invalidate();
        return "redirect:/registro/paso1";
    }

}
