
        package org.grupo4.practica_integradora_g4.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotNull;
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
    @Autowired
    private UsuarioService usuarioService;
            private static boolean usuarioEmailAsignado = false;


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
    private String paso1Get(Cliente cliente, Model model, HttpSession sesion) {
        if (sesion.getAttribute("usuarioAutenticado") == null) {
            return "administrador/errorAcceso";
        }

        // Cargar los países y generos en el servicio
        Colecciones.getNacionalidades().forEach((siglas, nombre) -> {
            Pais pais = new Pais(nombre, siglas);
            paisService.save(pais);
        });
        Colecciones.getGeneros().forEach((siglas, gen) -> {
            Genero genero = new Genero(gen, siglas);
            generoService.save(genero);
        });
        Colecciones.getTIPOCLIENTES().forEach((tipo) -> {
            TipoCliente tipoCliente = new TipoCliente(tipo.getTipo(), tipo.getSiglas(), tipo.getGastoUmbral(), tipo.getPorcentajeDescuento());
            tipoClienteService.save(tipoCliente);
        });

        model.addAttribute("listaP", paisRepository.findAll());
        model.addAttribute("listaG", generoRepository.findAll());
        if (sesion.getAttribute("datos_personales") != null) {
            cliente = (Cliente) sesion.getAttribute("datos_personales");
        }
        model.addAttribute("clientePlantilla", cliente);
        model.addAttribute("usuarioAutenticado", sesion.getAttribute("usuarioAutenticado"));
        return "registro/paso1";
    }

            @PostMapping("paso1")
            private String paso1Post(
                    @Validated({DatosPersonales.class}) @ModelAttribute("clientePlantilla") Cliente cliente,
                    BindingResult posiblesErrores,
                    @RequestParam(value = "pais", required = false) String siglaPais,
                    @RequestParam(value = "genero", required = false) String siglaGenero,
                    HttpSession sesion,
                    Model model
            ) {
                if (sesion.getAttribute("usuarioAutenticado") == null) {
                    return "administrador/errorAcceso";
                }

                model.addAttribute("listaP", paisRepository.findAll());
                model.addAttribute("listaG", generoRepository.findAll());

                // Verificar si el género es nulo o vacío
                if (siglaGenero == null || siglaGenero.isEmpty()) {
                    // Manejar el caso cuando el género no se selecciona
                    model.addAttribute("error", "Debe seleccionar un género");
                    return "registro/paso1";
                }

                // Asignación de país y género seleccionados
                Pais paisSeleccionado = paisRepository.findBySiglas(siglaPais.split(",")[0]);
                Genero generoSeleccionado = generoRepository.findBySiglas(siglaGenero);

                if (paisSeleccionado != null) {
                    cliente.setPais(paisSeleccionado);
                } else {
                    model.addAttribute("error", "País no existente");
                    return "registro/paso1";
                }
                if (generoSeleccionado != null) {
                    cliente.setGenero(generoSeleccionado);
                } else {
                    model.addAttribute("error", "Género no existente");
                    return "registro/paso1";
                }

                // Manejo de errores de validación
                if (posiblesErrores.hasErrors()) {
                    model.addAttribute("usuarioAutenticado", sesion.getAttribute("usuarioAutenticado"));
                    return "registro/paso1";
                }
                System.out.println(cliente.getGenero());
                sesion.setAttribute("datos_personales", cliente);
                model.addAttribute("usuarioAutenticado", sesion.getAttribute("usuarioAutenticado"));
                return "redirect:/registro/paso2";
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
        if (!usuarioEmailAsignado){
            cliente.setUsuarioEmail(usuAut);
            usuarioEmailAsignado=true;
        }

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
        clienteService.save(cliente);

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
            clienteService.save(cliente);
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
