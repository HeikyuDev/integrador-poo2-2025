package com.gpp.servisoft.model.entities;

import com.gpp.servisoft.model.enums.CondicionFrenteIVA;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Data
@NoArgsConstructor
public class DatosClienteFactura {

    /**
     * Domicilio fiscal del cliente (dirección). Campo obligatorio,
     * máximo 50 caracteres.
     */
    @NotBlank(message = "El domicilio fiscal no puede estar vacío")
    @Size(max = 50, message = "El domicilio fiscal no debe superar los 50 caracteres")
    @Column(nullable = false, length = 50)
    private String domicilioFiscal;

    /**
     * Condición del cliente frente al IVA (por ejemplo: RESPONSABLE_INSCRIPTO,
     * MONOTRIBUTO, etc.). Se persiste como STRING y es obligatorio.
     */
    @NotNull(message = "La condición frente al IVA es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CondicionFrenteIVA condicionFrenteIVA;

    /**
     * CUIT del cliente: 11 dígitos numéricos. Campo obligatorio.
     */
    @NotBlank(message = "El CUIT no puede estar vacío")
    @Pattern(regexp = "^[0-9]{11}$", message = "El CUIT debe tener 11 dígitos numéricos")
    @Column(nullable = false, length = 11)
    private String cuit;

    /**
     * Razón social o nombre fiscal del cliente. Obligatorio, máximo 50 caracteres.
     */
    @NotBlank(message = "La razón social no puede estar vacía")
    @Size(max = 50, message = "La razón social no debe superar los 50 caracteres")
    @Column(nullable = false, length = 50)
    private String razonSocial;

}
