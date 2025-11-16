package com.gpp.servisoft.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar el detalle de una factura.
 * Contiene información sobre los items individuales facturados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleFacturaDto {

    private int idDetalleFactura;

    private String nombreServicio;

    private int cantidad;

    private double precioUnitario;

    private double subtotal;

    private double ivaCalculado;

    /**
     * Obtiene el precio unitario garantizando que nunca sea nulo.
     * @return precioUnitario siempre es un valor numérico válido (mínimo 0.0)
     */
    public double getPrecioUnitarioSafe() {
        return Double.isFinite(this.precioUnitario) ? this.precioUnitario : 0.0d;
    }

    /**
     * Obtiene el subtotal garantizando que nunca sea nulo.
     * @return subtotal siempre es un valor numérico válido (mínimo 0.0)
     */
    public double getSubtotalSafe() {
        return Double.isFinite(this.subtotal) ? this.subtotal : 0.0d;
    }

    /**
     * Obtiene el IVA calculado garantizando que nunca sea nulo.
     * @return ivaCalculado siempre es un valor numérico válido (mínimo 0.0)
     */
    public double getIvaCalculadoSafe() {
        return Double.isFinite(this.ivaCalculado) ? this.ivaCalculado : 0.0d;
    }

}
