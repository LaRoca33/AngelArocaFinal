
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
            @Validated({DatosUsuario.class}) // Aplicar validaciones del grupo DatosUsuario
            @ModelAttribute("clientePlantilla") Cliente cliente,
            BindingResult posiblesErrores,
            @RequestParam("numero") Integer numero,
            @RequestParam("tipoTarjeta") String tipoTarjeta,
            @RequestParam("cvv") String cvv,
            @RequestParam("fechaCad") LocalDate fechaCad,
            HttpSession sesion,
            Model model
    ) {
        if (sesion.getAttribute("usuarioAutenticado") == null) {
            return "administrador/errorAcceso";
        }

        boolean hayErrores = false;

        // Validar los parámetros enviados
        if (numero == null || numero.toString().isEmpty()) {
            model.addAttribute("errorNumero", "El número de la tarjeta es obligatorio");
            hayErrores = true;
        }
        if (tipoTarjeta == null || tipoTarjeta.isEmpty()) {
            model.addAttribute("errorTipoTarjeta", "El tipo de tarjeta es obligatorio");
            hayErrores = true;
        }
        if (cvv == null || cvv.isEmpty()) {
            model.addAttribute("errorCvv", "El CVV es obligatorio");
            hayErrores = true;
        }
        if (fechaCad == null) {
            model.addAttribute("errorFechaCad", "La fecha de caducidad es obligatoria");
            hayErrores = true;
        }

        // Verificar si hay errores de validación en los parámetros o en el objeto Cliente
        if (posiblesErrores.hasErrors() || hayErrores) {
            model.addAttribute("usuarioAutenticado", sesion.getAttribute("usuarioAutenticado"));
            return "registro/paso3";
        }

        // Si no hay errores, continuar con el proceso de registro
        cliente.anadirTarjeta(new TarjetaCredito(numero, tipoTarjeta, cvv, fechaCad, cliente));
        sesion.setAttribute("datos_usuario", cliente);
        model.addAttribute("usuarioAutenticado", sesion.getAttribute("usuarioAutenticado"));
        return "redirect:/registro/resumen";
    }




    @GetMapping("resumen")
    private String resumenGet(Cliente cliente, Model model, HttpSession sesion) {
        if (sesion.getAttribute("usuarioAutenticado") == null) {
            return "administrador/errorAcceso";
        }

        Usuario usuAut = (Usuario) sesion.getAttribute("usuarioAutenticado");

        // Buscar el cliente por el usuario actual
        Cliente clienteExistente = clienteService.findByUsuario(usuAut);

        if (clienteExistente != null) {
            cliente = clienteExistente;
        } else {
            cliente = new Cliente();
        }

        // Actualizar los datos personales si existen en la sesión
        if (sesion.getAttribute("datos_personales") != null) {
            Cliente datosPersonales = (Cliente) sesion.getAttribute("datos_personales");
            cliente.setGenero(datosPersonales.getGenero());
            cliente.setPais(datosPersonales.getPais());
            cliente.setFechaNacimiento(datosPersonales.getFechaNacimiento());
            cliente.setTipoDocumentoCliente(datosPersonales.getTipoDocumentoCliente());
            cliente.setDocumento(datosPersonales.getDocumento());
            cliente.setNombre(datosPersonales.getNombre());
            cliente.setApellidos(datosPersonales.getApellidos());
            cliente.setSalario(datosPersonales.getSalario());
        }

        // Actualizar los datos de contacto si existen en la sesión
        if (sesion.getAttribute("datos_contacto") != null) {
            Cliente datosContacto = (Cliente) sesion.getAttribute("datos_contacto");
            cliente.setTelefonoMovil(datosContacto.getTelefonoMovil());
        }

        // Actualizar los datos de usuario si existen en la sesión
        if (sesion.getAttribute("datos_usuario") != null) {
            Cliente datosUsuario = (Cliente) sesion.getAttribute("datos_usuario");
            cliente.setComentarios(datosUsuario.getComentarios());
        }

        cliente.setUsuarioEmail(usuAut);


        // Guardar la dirección personal si existe en la sesión
        if (sesion.getAttribute("datos_contacto") != null) {
            Cliente datosContacto = (Cliente) sesion.getAttribute("datos_contacto");
            clienteService.save(cliente);
            Direccion direccion = datosContacto.getDirecciones();
            direccion.setCliente(cliente);
            direccionService.save(direccion);
            cliente.setDirecciones(direccion);
        }

        // Guardar las tarjetas de crédito si existen en la sesión
        if (sesion.getAttribute("datos_usuario") != null) {

            Cliente datosUsuario = (Cliente) sesion.getAttribute("datos_usuario");
            clienteService.save(cliente);
            for (TarjetaCredito tarjeta : datosUsuario.getTarjetasCredito()) {
                tarjeta.setCliente(cliente);
            }
            tarjetaCreditoService.save(datosUsuario.getTarjetasCredito());
        }

        sesion.setAttribute("clienteFinal", cliente);
        registroCompleto=true;
        model.addAttribute("clientePlantilla", cliente);
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
        if (sesion.getAttribute("datos_personales") == null ||
                sesion.getAttribute("datos_contacto") == null ||
                sesion.getAttribute("datos_usuario") == null) {
            // Si falta alguno de los datos esenciales, mostrar un mensaje de error
            model.addAttribute("error", "Faltan datos esenciales para completar el registro");
            // Redirigir de vuelta a la página de resumen
            return "registro/resumen";
        }
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


