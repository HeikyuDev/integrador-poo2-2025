package com.gpp.servisoft.model.entities;

import com.gpp.servisoft.model.enums.CondicionFrenteIVA;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Datos embebidos que representan la información fiscal del cliente
 * asociada a una factura. Se utiliza como `@Embeddable` dentro de
 * la entidad `Factura` para almacenar domicilio, condición frente
 * al IVA, CUIT y razón social.
 */
@Embeddable
@Data
@NoArgsConstructor
public class DatosClienteFactura {

    /**
     * Atributo domicilioFiscal: Domicilio fiscal del cliente.
     * Debe ser una cadena no vacía con un máximo de 50 caracteres.
     */
    @NotBlank(message = "El domicilio fiscal no puede estar vacío")
    @Size(max = 50, message = "El domicilio fiscal no debe superar los 50 caracteres")
    @Column(nullable = false, length = 50)
    private String domicilioFiscal;

    /**
     * Atributo condicionFrenteIVA: Condición frente al IVA del cliente.
     * Debe ser uno de los valores del enum "CondicionFrenteIVA".
     */
    @NotNull(message = "La condición frente al IVA es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CondicionFrenteIVA condicionFrenteIVA;

    /**
     * Atributo cuit: CUIT del cliente.
     * Debe ser una cadena no vacía de 11 dígitos numéricos.
     */
    @NotBlank(message = "El CUIT no puede estar vacío")
    @Pattern(regexp = "^[0-9]{11}$", message = "El CUIT debe tener 11 dígitos numéricos")
    @Column(nullable = false, length = 11)
    private String cuit;

    /**
    * Atributo razonSocial: Razón social del cliente.
    * Debe ser una cadena no vacía con un máximo de 50 caracteres.
    */
    @NotBlank(message = "La razón social no puede estar vacía")
    @Size(max = 50, message = "La razón social no debe superar los 50 caracteres")
    @Column(nullable = false, length = 50)
    private String razonSocial;
}
