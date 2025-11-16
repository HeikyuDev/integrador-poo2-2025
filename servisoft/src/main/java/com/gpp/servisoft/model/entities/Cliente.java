package com.gpp.servisoft.model.entities;

import java.util.List;

import com.gpp.servisoft.model.enums.Estado;
import com.gpp.servisoft.model.enums.TipoCliente;

import jakarta.persistence.*;
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
 * Cliente
 * Clase que representa la entidad Cliente en la base de datos.
 */
@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
/**
 * Atributo Id: Identificador único del cliente.
 * Los métodos setter del constructor generado por Lombok no 
 * pueden ser accedidos. 
 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private int idCliente;
/**
 * Atributo tipoCliente: Constantes que representan los tipos de clientes.
 * Debe ser uno de los valores del enum "TipoCliente".
 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCliente tipoCliente;
/**
 * Atributo nombre: Nombre del cliente.
 * Debe ser una cadena no vacía con un máximo de 50 caracteres.
 */
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 50, message = "El nombre no debe superar los 50 caracteres")
    @Column(nullable = false, length = 50)
    private String nombre;
/**
 * Atributo direccion: Dirección del cliente.
 * Debe ser una cadena no vacía con un máximo de 50 caracteres.
 */
    @NotBlank(message = "La dirección no puede estar vacía")
    @Size(max = 50, message = "La dirección no debe superar los 50 caracteres")
    @Column(nullable = false, length = 50)
    private String direccion;
/**
 * Atributo telefono: Número de teléfono del cliente.
 * Debe ser una cadena no vacía que solo contenga números, espacios 
 * y guiones, con un máximo de 20 caracteres.
 */
    @NotBlank(message = "El teléfono no puede estar vacío")
    @Pattern(regexp = "^[0-9\\-\\s]+$", message = "El campo solo puede contener números, espacios y guiones")
    @Size(max = 20, message = "El teléfono no debe superar los 20 caracteres")
    @Column(nullable = false, length = 20)
    private String telefono;
/**
 * Atributo correoElectronico: Correo electrónico del cliente.
 * Debe ser una cadena no vacía con un formato válido y un máximo de
 * 50 caracteres.
 */
    @Email(message = "El correo electrónico debe ser válido")
    @NotBlank(message = "El correo electrónico no puede estar vacío")
    @Size(max = 50, message = "El correo electrónico no debe superar los 50 caracteres")
    @Column(nullable = false, length = 50)
    private String correoElectronico;
/**
 * Atributo estado: Estado del cliente (ACTIVO o INACTIVO).
 * Debe ser uno de los valores del enum "Estado".
 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.ACTIVO;

/**
 * Asociación con la clase Cuenta.
 * Un cliente puede estar asociado con una o muchas cuentas.
 */
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @Valid
    private List<Cuenta> cuentas;
}