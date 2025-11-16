package com.gpp.servisoft.model.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FacturacionMasivaDto {

    private int idFacturacionMasiva;

    /**
     * Monto total Recaudado con esta facturacion Masiva
     */
    private Double montoTotal;

    /**
     * Fecha de emisión de la facturacion Masiva.
     */
    private LocalDate fechaEmision;

    /**
     * Cantidad de Facturas Emitidas
     */
    private int cantidadDeFacturas;

    /**
     * una Facturacion masiva involucra una lista de Facturas
     */
     private List<FacturacionDTO> facturas;
}