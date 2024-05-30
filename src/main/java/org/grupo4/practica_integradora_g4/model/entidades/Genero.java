package org.grupo4.practica_integradora_g4.model.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Entity
@Data
public class Genero {
    @Id
    @NotBlank
    @NotNull
    private String gen;
    @NotBlank
    @NotNull
    private String siglas;
}
