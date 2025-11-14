package com.gpp.servisoft.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que almacena los datos históricos de un servicio en una factura.
 * Se usa para mantener un registro de los servicios tal como estaban
 * al momento de la facturación, permitiendo refacturación y gestión histórica.
 */

@Entity
@Table(name = "datos_servicio_factura")
@Data
@NoArgsConstructor
public class DatosServicioFactura {


    /**
     * Identificador unico de los servicios que intervinieron
     * en el proceso de facturacion
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private int idDatosServicioFactura;


    /**
     * Nombre del servicio. Obligatorio y hasta 50 caracteres.
     */
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 50, message = "El nombre no debe superar los 50 caracteres")
    @Column(nullable = false, length = 50)
    private String nombreServicio;

    /**
     * Descripción breve del servicio. Obligatoria y hasta 50 caracteres.
     */
    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(max = 50, message = "La descripción no debe superar los 50 caracteres")
    @Column(nullable = false, length = 50)
    private String descripcionServicio;

    /**
     * Precio actual del servicio. Debe ser un valor numérico no negativo.
     * Se usa {@link Double} para permitir distinguir entre "no proporcionado"
     * y 0.0 cuando sea necesario.
     */
    @NotNull(message = "El monto no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = true, message = "El monto no puede ser negativo")
    @Column(nullable = false)
    private Double precioActual;
}
