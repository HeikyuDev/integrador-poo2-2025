package com.gpp.servisoft.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gpp.servisoft.model.entities.Cuenta;
import com.gpp.servisoft.model.enums.Estado;

public interface CuentaRepository extends JpaRepository<Cuenta, Integer> {

    /**
     * Busca cuentas por su estado.
     * Ejemplo de uso: findByEstado(Estado.ACTIVO)
     */
    List<Cuenta> findByEstado(Estado estado);

}
