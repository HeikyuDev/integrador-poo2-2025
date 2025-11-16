package com.gpp.servisoft.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar los datos fiscales del cliente en una factura.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatosClienteFacturaDto {

    private int idCuenta;

    private String domicilioFiscal;

    private String condicionFrenteIVA;

    private String cuit;

    private String razonSocial;

}
