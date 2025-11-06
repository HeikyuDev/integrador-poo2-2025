package com.gpp.servisoft.model.entities;

import java.util.List;

import com.gpp.servisoft.model.enums.Estado;
import com.gpp.servisoft.model.enums.TipoCliente;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa un cliente del sistema.
 *
 * Contiene el tipo de cliente, datos de contacto y una relación con
 * las cuentas asociadas. Utiliza validaciones de Jakarta Bean Validation
 * para asegurar integridad básica de los datos en la capa de aplicación.
 */
@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private int idCliente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCliente tipoCliente;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 50, message = "El nombre no debe superar los 50 caracteres")
    @Column(nullable = false, length = 50)
    private String nombre;

    @NotBlank(message = "La dirección no puede estar vacía")
    @Size(max = 50, message = "La dirección no debe superar los 50 caracteres")
    @Column(nullable = false, length = 50)
    private String direccion;

    @NotBlank(message = "El teléfono no puede estar vacío")
    @Pattern(regexp = "^[0-9\\-\\s]+$", message = "El campo solo puede contener números, espacios y guiones")
    @Size(max = 20, message = "El teléfono no debe superar los 20 caracteres")
    @Column(nullable = false, length = 20)
    private String telefono;

    @Email(message = "El correo electrónico debe ser válido")
    @NotBlank(message = "El correo electrónico no puede estar vacío")
    @Size(max = 50, message = "El correo electrónico no debe superar los 50 caracteres")
    @Column(nullable = false, length = 50)
    private String correoElectronico;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.ACTIVO;

    // un cliente tiene muchas cuentas asociadas
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @Valid
    private List<Cuenta> cuentas;
}