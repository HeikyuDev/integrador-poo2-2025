package com.gpp.servisoft.model.dto;



import java.util.List;

import com.gpp.servisoft.model.enums.Periodicidad;

import lombok.Data;

@Data
public class FacturacionFormDto { // <-- El contenedor

    // 1. Para el <select> de Cuenta
    private Long cuentaId;

    // 2. Para el <select> de Periodo
    private Periodicidad periodo;

    // 3. Para la <table>
    private List<ServicioSeleccionadoDto> items; // <-- La lista de "filas"
}