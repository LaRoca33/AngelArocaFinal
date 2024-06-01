package org.grupo4.practica_integradora_g4.model.entidades;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;

import org.grupo4.practica_integradora_g4.validaciones.ComprobarClaves;
import org.grupo4.practica_integradora_g4.validaciones.ComprobarEmail;
import org.grupo4.practica_integradora_g4.validaciones.ComprobarNombreEmail;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString(exclude ="auditoria")
@Validated
@ComprobarClaves
@Entity
public class Usuario {
    @Id
    @NotNull
    @NotBlank
    @ComprobarEmail
    @ComprobarNombreEmail
    private String email;
    @NotNull
    @NotBlank
    @Size(min=3, max=12)
    //@Pattern(regexp ="(?=.*[0-9])(?=.*[a-zñ])(?=.*[A-ZÑ])(?=.*[!#$%&]).*")
    private String clave;
    @NotNull
    private String confirmarClave;
    @NotNull
    @NotBlank
    private String pregRec;
    @NotNull
    @NotBlank
    private String respRec;
    private LocalDate fechaUltimaConexion=LocalDate.now();
    private Integer numeroAccesos=0;
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Auditoria> auditoria;
    private boolean bloqueado;
    private LocalDateTime fechaDesbloqueo;
    private int intentosFallidos;

}

