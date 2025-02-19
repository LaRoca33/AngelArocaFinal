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
    List<Cliente> findByUsuarioEmailEmailContainingIgnoreCaseAndPaisNombreInAndFechaNacimientoBetweenAndSalarioBetween(String email,List<String> pais, LocalDate fechaInicio, LocalDate fechaFin,Integer salario1,Integer salario2);
    List <Cliente> findByUsuarioEmailEmailContainingIgnoreCaseAndFechaNacimientoBetweenAndSalarioBetween(String email,  LocalDate fechaInicio, LocalDate fechaFin,Integer salario1,Integer salario2);
    Cliente findByUsuarioEmail(Usuario usuarioEmail);

}
