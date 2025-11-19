package com.gpp.servisoft.model.dto;

import java.time.LocalDate;

import com.gpp.servisoft.model.enums.MetodoPago;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PagoDto {

    
    private Integer idPago;

    private MetodoPago metodoPago; // Dato de Entrada

    private Double monto; // Dato de Entrada (Si es Parcial) // Salida trae el saldo de la factura

    private LocalDate fechaPago;


    private Integer idFacturacion; // Dato de Entrada
    
}
