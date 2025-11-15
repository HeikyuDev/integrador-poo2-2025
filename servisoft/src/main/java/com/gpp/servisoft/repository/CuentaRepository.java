package com.gpp.servisoft.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gpp.servisoft.model.entities.Cuenta;
import com.gpp.servisoft.model.enums.Estado;

public interface CuentaRepository extends JpaRepository<Cuenta, Integer> {
    /**
     * Buscar todas las cuentas que no han sido eliminadas.
     * Es decir, que no estén en estado INACTIVO.
     */
    List<Cuenta> findByEstadoNot(Estado estado);

    /**
     * Busca cuentas por su estado.
     * Ejemplo de uso: findByEstado(Estado.ACTIVO)
     */
    List<Cuenta> findByEstado(Estado estado);
    
    /**
     * Buscar una cuenta por ID, sólo si no está inactiva.
     */
    Optional<Cuenta> findByIdCuentaAndEstadoNot(Integer idCuenta, Estado estado);
    
    /**
     * Buscar una cuenta por ID con JOIN FETCH para eager loading de relaciones lazy.
     * Esto evita problemas de LazyLoadingException al acceder a cliente y servicios
     * después de que se cierre la sesión de Hibernate.
     */
    @Query("SELECT DISTINCT c FROM Cuenta c " +
           "LEFT JOIN FETCH c.cliente " +
           "LEFT JOIN FETCH c.serviciosDeLaCuenta s " +
           "LEFT JOIN FETCH s.servicio " +
           "WHERE c.idCuenta = :idCuenta AND c.estado != 'INACTIVO'")
    Optional<Cuenta> findByIdConRelaciones(@Param("idCuenta") Integer idCuenta);
}
