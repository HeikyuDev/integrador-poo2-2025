package com.gpp.servisoft.mapper;

import com.gpp.servisoft.model.dto.ServicioDeLaCuentaDto;
import com.gpp.servisoft.model.entities.ServicioDeLaCuenta;

public class Mapper {

    public static ServicioDeLaCuentaDto toDto(ServicioDeLaCuenta sdc)
    {
        if (sdc == null) return null;

        return ServicioDeLaCuentaDto.builder().idServicioDeLaCuenta(sdc.getIdServicioDeLaCuenta())
        .cuenta(sdc.getCuenta())
        .servicio(sdc.getServicio())
        .estadoServicio(sdc.getEstadoServicio())
        .build();
    }
}
