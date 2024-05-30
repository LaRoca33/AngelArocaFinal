package org.grupo4.practica_integradora_g4.model.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter

@Entity
@Data
public class TarjetaCredito {
    @Id
    Integer numero;
    String tipoTarjetaCredito;
    String cvv;
    LocalDate fechaCad;
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    @Override
    public int hashCode() {
        return Objects.hash(numero); // Solo usa 'numero' para evitar recursión
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TarjetaCredito that = (TarjetaCredito) obj;
        return Objects.equals(numero, that.numero);
    }

}
