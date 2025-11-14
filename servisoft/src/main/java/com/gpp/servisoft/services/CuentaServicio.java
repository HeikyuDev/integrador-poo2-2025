package com.gpp.servisoft.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gpp.servisoft.model.entities.Cuenta;
import com.gpp.servisoft.model.enums.Estado;
import com.gpp.servisoft.repository.CuentaRepository;

@Service
public class CuentaServicio {

    @Autowired
    CuentaRepository cuentaRepository;

    public CuentaServicio(CuentaRepository cuentaRepository)
    {
        this.cuentaRepository = cuentaRepository;
    }

    /**
     * Implementacion Basica de la consulta de todos los registros en 
     * estado ACTIVO
     * 
     * @return la lista completa de Cuentas existentes de la BD, que tienen el estado activo
     */
    public List<Cuenta> obtenerCuentas() {
        return cuentaRepository.findByEstado(Estado.ACTIVO);
    }
}
