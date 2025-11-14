package com.gpp.servisoft.model.entities;

import java.util.List;

import com.gpp.servisoft.model.enums.CondicionFrenteIVA;
import com.gpp.servisoft.model.enums.Estado;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Cuenta
 * Clase que representa la entidad Cuenta en la base de datos.
 */

@Entity
@Table(name = "cuentas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cuenta {
/**
 * Atributo idCuenta: Identificador único de la cuenta.
 * Los métodos setter del constructor generado por Lombok no pueden ser 
 * accedidos.
 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private int idCuenta;
/**
 * Atributo domicilioFiscal: Domicilio fiscal de la cuenta.
 * Debe ser una cadena no vacía con un máximo de 50 caracteres.
 */
    @NotBlank(message = "El domicilio fiscal no puede estar vacío")
    @Size(max = 50, message = "El domicilio fiscal no debe superar los 50 caracteres")
    @Column(nullable = false, length = 50)
    private String domicilioFiscal;
/**
 * Atributo condicionFrenteIVA: Condición frente al IVA de la cuenta.
 * Debe ser uno de los valores del enum "CondicionFrenteIVA".
 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CondicionFrenteIVA condicionFrenteIVA;
/**
 * Atributo cuit: CUIT de la cuenta.
 * Debe ser una cadena no vacía de 11 dígitos numéricos.
 */
    @NotBlank(message = "El CUIT no puede estar vacío")
    @Pattern(regexp = "^[0-9]{11}$", message = "El CUIT debe tener 11 dígitos numéricos")
    @Column(nullable = false, unique = true, length = 11)
    private String cuit;
/**
 * Atributo razonSocial: Razón social de la cuenta.
 * Debe ser una cadena no vacía con un máximo de 50 caracteres.
 */
    @NotBlank(message = "La razón social no puede estar vacía")
    @Size(max = 50, message = "La razón social no debe superar los 50 caracteres")
    @Column(nullable = false, length = 50)
    private String razonSocial;
/**
 * Atributo estado: Estado de la cuenta (ACTIVO o INACTIVO).
 * Debe ser uno de los valores del enum "Estado".
 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.ACTIVO;

/**
 * Atributo cliente: Cliente que se asocia a la cuenta.
 * Posee cardinalidad uno a muchos, donde una o muchas cuentas 
 * pueden estar asociadas a un solo cliente.
 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

/**
 * Atributo serviciosDeLaCuenta: Lista de servicios asociados a la cuenta.
 * Posee cardinalidad uno a muchos, donde una cuenta puede tener 
 * uno o varios servicios asociados.
 */
    @OneToMany(mappedBy="cuenta", cascade=CascadeType.ALL, orphanRemoval=true) // si elimino un servicio de la lista de serviciosdelacuenta, automaticamente jpa la elimina de la bd
    @Valid
    private List<ServicioDeLaCuenta> serviciosDeLaCuenta;
}