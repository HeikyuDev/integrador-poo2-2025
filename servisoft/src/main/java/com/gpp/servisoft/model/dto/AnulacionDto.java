package com.gpp.servisoft.model.dto;

import java.time.LocalDate;

import com.gpp.servisoft.model.enums.TipoComprobante;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnulacionDto {
    
    // Entrada
    @NotNull
    private Integer idFactura;
    @NotBlank
    private String motivo;
    
    // Salida
    private String nroComprobante;
    private TipoComprobante tipo;
    private Double monto;
    private LocalDate fechaEmision;
    private String cliente;  // Nombre de la cuenta
    private LocalDate fechaAnulacion;
}
