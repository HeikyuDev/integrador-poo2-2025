package com.gpp.servisoft.model.dto;

import java.time.LocalDate;

import com.gpp.servisoft.model.enums.MetodoPago;
import com.gpp.servisoft.model.enums.TipoPago;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PagoDto {

    private Integer idPago;

    private Double monto;

    private LocalDate fechaPago;

    private MetodoPago metodoPago;

    private TipoPago tipoPago;

    private Integer idFacturacion;
    
}
