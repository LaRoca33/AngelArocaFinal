package org.grupo4.practica_integradora_g4.repositories;

import org.grupo4.practica_integradora_g4.model.entidades.Cliente;
import org.grupo4.practica_integradora_g4.model.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public interface ClienteRepository extends JpaRepository <Cliente, UUID> {
/*
    List<Cliente> findByNombreContainingIgnoreCaseAndApellidosContainingIgnoreCaseAndUsuarioEmail_EmailContainingIgnoreCaseAndPaisNombreContainingIgnoreCaseAndGeneroGenContainingIgnoreCaseAndTelefonoMovilContainingIgnoreCaseAndTipoDocumentoClienteContainingIgnoreCaseAndDocumentoContainingIgnoreCaseAndComentariosContainingIgnoreCase(
            String nombre, String apellidos, String email, String pais, String genero,String telefono, String tipoDoc, String doc, String comentarios);
*/
    List<Cliente> findByUsuarioEmailEmailContainingIgnoreCaseAndPaisNombreContainingIgnoreCaseAndFechaNacimientoBetween(String email, String pais, LocalDate fechaInicio, LocalDate fechaFin);
    Cliente findByUsuarioEmail(Usuario usuarioEmail);

}
