package com.gpp.servisoft.model.dto;

import com.gpp.servisoft.model.entities.Cuenta;
import com.gpp.servisoft.model.entities.Servicio;
import com.gpp.servisoft.model.enums.EstadoServicio;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServicioDeLaCuentaDto {

    private int idServicioDeLaCuenta;

    private Cuenta cuenta;

    private Servicio servicio;

    private int cantidadDePreferencia;

    private EstadoServicio estadoServicio;
}
