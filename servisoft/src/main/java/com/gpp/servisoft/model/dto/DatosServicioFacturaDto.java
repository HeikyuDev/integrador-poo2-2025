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

    private Double precioActual;

}
