package org.grupo4.practica_integradora_g4.model.entidades;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import org.grupo4.practica_integradora_g4.model.extra.DatosContacto;
import org.grupo4.practica_integradora_g4.model.extra.DatosPersonales;
import org.grupo4.practica_integradora_g4.model.extra.DatosUsuario;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"tarjetasCredito","direcciones"})
@Entity
@Data
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    //DATOS PERSONALES


    @ManyToOne
    @JoinColumn(
            name = "genero_gen",
            foreignKey = @ForeignKey(name = "FK_cli_genero_generoGen")

    )
    private Genero genero;
    private LocalDate fechaNacimiento;
    @ManyToOne
    @JoinColumn(
            name = "nombre_Pais",
            foreignKey = @ForeignKey(name = "FK_cli_pais_nombrePais")

    )
    private Pais pais;
    private String tipoDocumentoCliente;
    private String documento;
    @NotBlank( groups = DatosPersonales.class)
    private String nombre;
    private String apellidos;

    //DATOS DE CONTACTO
    @OneToOne
    @JoinColumn(
            name = "direccion_personal",
            foreignKey = @ForeignKey(name = "FK_cli_direccion_direccionPersonal")
    )
    private Direccion direcciones;
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Direccion> direccionesEntrega = new HashSet<>();
    @NotBlank ( groups = DatosContacto.class)
    private String telefonoMovil;

    //DATOS DE CLIENTE
    @OneToOne
    @JoinColumn(
            name = "email_usuario",
            foreignKey = @ForeignKey(name = "FK_cli_usuario_usuarioEmail")
    )
    private Usuario usuarioEmail;
  /*  @NotBlank(groups = DatosUsuario.class)
    @NotNull(groups = DatosUsuario.class)

   */
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TarjetaCredito> tarjetasCredito = new HashSet<>() ;


    private BigDecimal gastoAcumuladoCliente;

    @ManyToOne
    @JoinColumn(
            name = "tipo_Cliente",
            foreignKey = @ForeignKey(name = "FK_cli_tipoCli_tipoCli")

    )
    private TipoCliente tipoCliente;


    private boolean licencia;
    @OneToOne
    @JoinColumn(
            name = "id_auditoria",
            foreignKey = @ForeignKey(name = "FK_cli_auditoria_idAuditoria")
    )
    private Auditoria auditoria;

    @NotBlank ( groups = DatosUsuario.class)
    @NotNull (groups = DatosUsuario.class)
    private String comentarios;

    public void addItem(TarjetaCredito tarjetaCredito) {
        this.tarjetasCredito.add(tarjetaCredito);
        tarjetaCredito.setCliente(this);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id); // Solo usa 'id' para evitar recursión
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cliente cliente = (Cliente) obj;
        return Objects.equals(id, cliente.id);
    }

}

