package com.gpp.servisoft.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Datos embebidos que describen un servicio ligado a una factura.
 *
 * Incluye nombre, descripción y el precio actual del servicio. Se
 * usa como {@code @Embeddable} en la entidad principal de factura.
 */
@Embeddable
@Data
@NoArgsConstructor
public class DatosServicioFactura {

    /**
     * Atributo nombreServicio: Indica el nombre del servicio.
     * Debe ser una cadena no vacía con un máximo de 50 caracteres.
     */
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 50, message = "El nombre no debe superar los 50 caracteres")
    @Column(nullable = false, length = 50)
    private String nombreServicio;

    /**
     * Atributo descripcionServicio: Descripción breve del servicio.
     * Debe ser una cadena no vacía con un máximo de 50 caracteres.
     */
    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(max = 50, message = "La descripción no debe superar los 50 caracteres")
    @Column(nullable = false, length = 50)
    private String descripcionServicio;

    /**
     * Atributo precioActual: Precio actual del servicio.
     * Debe ser un valor numérico no negativo.
     * Se usa {@link Double} para permitir distinguir entre "no proporcionado"
     * y 0.0 cuando sea necesario.
     */
    @NotNull(message = "El monto no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = true, message = "El monto no puede ser negativo")
    @Column(nullable = false)
    private Double precioActual;
}
