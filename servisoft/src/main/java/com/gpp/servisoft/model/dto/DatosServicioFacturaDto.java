package com.gpp.servisoft.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar los servicios involucrados en una factura.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatosServicioFacturaDto {

    private int idDatosServicioFactura;

    private String nombreServicio;

    private String descripcionServicio;

    private double precioActual;

    /**
     * Obtiene el precio actual garantizando que nunca sea nulo.
     * @return precioActual siempre es un valor numérico válido (mínimo 0.0)
     */
    public double getPrecioActualSafe() {
        return Double.isFinite(this.precioActual) ? this.precioActual : 0.0d;
    }

}

