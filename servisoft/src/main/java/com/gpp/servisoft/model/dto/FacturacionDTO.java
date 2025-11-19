package com.gpp.servisoft.model.dto;

import java.time.LocalDate;
import java.util.List;

import com.gpp.servisoft.model.enums.EstadoFactura;
import com.gpp.servisoft.model.enums.Periodicidad;
import com.gpp.servisoft.model.enums.TipoComprobante;

import lombok.Builder;
import lombok.Data;

/**
 * DTO para representar la información completa de una factura.
 * Contiene solo DTOs, sin referencias a entidades.
 */
@Data
@Builder
public class FacturacionDTO {

    // Datos principales
    private int idFactura;

    private String nroComprobante;

    private String razonSocial;

    private LocalDate fechaEmision;

    private LocalDate fechaVencimiento;

    // Montos
    private double montoTotal;

    private double subtotal;

    private double totalIva;

    private double saldo;

    // Estados y tipos
    private TipoComprobante tipo;

    private EstadoFactura estado;

    private Periodicidad periodicidad;

    // DTOs en lugar de entidades
    private DatosClienteFacturaDto datosClienteFactura;

    private List<DatosServicioFacturaDto> serviciosInvolucrados;

    private List<DetalleFacturaDto> detalleFacturas;

    /**
     * Obtiene el monto total garantizando que nunca sea nulo.
     * @return montoTotal siempre es un valor numérico válido (mínimo 0.0)
     */
    public double getMontoTotalSafe() {
        return Double.isFinite(this.montoTotal) ? this.montoTotal : 0.0d;
    }

    /**
     * Obtiene el subtotal garantizando que nunca sea nulo.
     * @return subtotal siempre es un valor numérico válido (mínimo 0.0)
     */
    public double getSubtotalSafe() {
        return Double.isFinite(this.subtotal) ? this.subtotal : 0.0d;
    }

    /**
     * Obtiene el IVA total garantizando que nunca sea nulo.
     * @return totalIva siempre es un valor numérico válido (mínimo 0.0)
     */
    public double getTotalIvaSafe() {
        return Double.isFinite(this.totalIva) ? this.totalIva : 0.0d;
    }

}
