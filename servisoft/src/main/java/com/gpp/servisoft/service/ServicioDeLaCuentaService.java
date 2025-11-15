package com.gpp.servisoft.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gpp.servisoft.mapper.Mapper;
import com.gpp.servisoft.model.dto.ServicioDeLaCuentaDto;
import com.gpp.servisoft.model.enums.EstadoServicio;
import com.gpp.servisoft.repository.CuentaRepository;
import com.gpp.servisoft.repository.ServicioDeLaCuentaRepository;

@Service
public class ServicioDeLaCuentaService {

    @Autowired
    ServicioDeLaCuentaRepository servicioDeLaCuentaRepository;
    @Autowired
    CuentaRepository cuentaRepository;

    public ServicioDeLaCuentaService(ServicioDeLaCuentaRepository servicioDeLaCuentaRepository,
            CuentaRepository cuentaRepository) {
        this.servicioDeLaCuentaRepository = servicioDeLaCuentaRepository;
        this.cuentaRepository = cuentaRepository;
    }

    /**
     * Obtener servicio por cuenta
     */
    public List<ServicioDeLaCuentaDto> obtenerServiciosPorCuentaPendiente(Integer idCuenta) {
        return servicioDeLaCuentaRepository
                .findByCuentaAndEstadoServicio(cuentaRepository.findById(idCuenta).orElse(null),
                        EstadoServicio.PENDIENTE)
                .stream().map(Mapper::toDto).toList();
    }
}
