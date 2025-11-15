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

    // Estados y tipos
    private TipoComprobante tipo;

    private EstadoFactura estado;

    private Periodicidad periodicidad;

    // DTOs en lugar de entidades
    private DatosClienteFacturaDto datosClienteFactura;

    private List<DatosServicioFacturaDto> serviciosInvolucrados;

    private List<DetalleFacturaDto> detalleFacturas;

}

