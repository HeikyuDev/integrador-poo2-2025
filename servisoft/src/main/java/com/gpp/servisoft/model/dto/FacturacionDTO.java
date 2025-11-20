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

    /**
     * Entrada: Necesario para saber, la factura que se va a facturar
     */
    private int idFactura;

    /**
     * Salida: Necesario Para mostrar al usuario el Nro De Comprobante de la Factura
     *  Ej: 0001-00000001
     */
    private String nroComprobante;
    
    /**
     * Salida: Necesario para mostrar la razon social de la Facturacion.
     */
    private String razonSocial;

    /**
     * Salida: Necesario para mostrar la fecha en la que se emitio la factura
     */
    private LocalDate fechaEmision;

    /**
     * Salida: Necesario para mostrar la fecha en la que se emitio la factura
     */
    private LocalDate fechaVencimiento;

    /**
     * Salida: Necesario para mostrar el Monto total de la factura
     */
    private double montoTotal;

    /**
     * Salida: Necesario para mostrar el Subtotal de la factura 
     * Suma de todos los montos * Cantidad de los detalles
     */
    private double subtotal;

    /**
     * Salida: Necesario para mostrar, el iva total de los detalles
     * de todas las facturas
     */
    private double totalIva;

    /**
     * Salida: Necesario para mostrar el saldo a pagar, de la factura
     * (Monto Total - Monto Pagado)
     */
    private double saldo;

    /**
     * Salida: Necesario para determinar el tipo de Comprobante que tiene la factura
     */
    private TipoComprobante tipo;

    /**
     * Salida: Necesario para mostrar, los estados de forma grafica utilizando th:if
     */
    private EstadoFactura estado;

    /**
     * Salida
     */
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
