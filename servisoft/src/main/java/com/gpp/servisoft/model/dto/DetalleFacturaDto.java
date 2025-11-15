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

    private Double precioUnitario;

    private Double subtotal;

    private Double ivaCalculado;

}
