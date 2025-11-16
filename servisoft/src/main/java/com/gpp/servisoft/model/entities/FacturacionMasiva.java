package com.gpp.servisoft.model.entities;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Data
@Table(name = "facturacionMasiva")
@NoArgsConstructor
public class FacturacionMasiva {
    /**
     * Entidad que agrupa una operación de facturación masiva.
     *
     * NOTA: los tipos de los campos se mantuvieron tal como están en el
     * repositorio (no se cambió ningún tipo). Las validaciones aplicadas
     * son compatibles con esos tipos.
     */
    /**
     * Identificador único de la facturación masiva. Se genera automáticamente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private int idFacturacionMasiva;


    /**
     * Monto total acumulado de la facturación masiva.
     * Debe ser mayor o igual a 0.0.
     */
    @NotNull(message = "El monto total es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El monto total no puede ser negativo")
    private Double montoTotal;

    /**
     * Fecha de emisión de la facturacion Masiva. No puede ser nula y no puede estar en el futuro.
     */
    @NotNull(message = "La fecha de emisión es obligatoria")
    @PastOrPresent(message = "La fecha de emisión no puede ser en el futuro")
    private LocalDate fechaEmision;

    /**
     * Cantidad de facturas incluidas en esta operación masiva. Debe ser al menos 1.
     */
    @Min(value = 1, message = "La cantidad de facturas debe ser al menos 1")
    private int cantidadDeFacturas;

    /**
     * una Facturacion masiva involucra una lista de Facturas
     */

    @OneToMany()
    @Valid
    private List<Factura> facturas;
}
