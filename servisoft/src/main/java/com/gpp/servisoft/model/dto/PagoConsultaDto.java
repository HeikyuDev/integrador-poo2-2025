package com.gpp.servisoft.model.dto;

import java.time.LocalDate;

import com.gpp.servisoft.model.enums.MetodoPago;

import lombok.Builder;
import lombok.Data;

/**
 * DTO de solo lectura para la vista de historial de pagos.
 */
@Data
@Builder
public class PagoConsultaDto {

    private Integer idPago;

    private Double monto;

    private LocalDate fechaPago;

    private MetodoPago metodoPago;

    private Integer idFactura;

    private String nroComprobante;

    private Integer idCuenta;

    private String razonSocialCliente;
}
