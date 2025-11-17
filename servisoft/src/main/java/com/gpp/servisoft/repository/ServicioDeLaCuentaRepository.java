package com.gpp.servisoft.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gpp.servisoft.model.entities.ServicioDeLaCuenta;

@Repository
public interface ServicioDeLaCuentaRepository extends JpaRepository<ServicioDeLaCuenta, Integer> {
    
    /**
     * Busca si ya existe una relación entre una cuenta y un servicio específico
     */
    @Query("SELECT s FROM ServicioDeLaCuenta s WHERE s.cuenta.idCuenta = :idCuenta AND s.servicio.idServicio = :idServicio")
    Optional<ServicioDeLaCuenta> findByCuentaAndServicio(@Param("idCuenta") int idCuenta, @Param("idServicio") int idServicio);
    
    /**
     * Obtiene todos los servicios asociados a una cuenta específica
     */
    @Query("SELECT s FROM ServicioDeLaCuenta s WHERE s.cuenta.idCuenta = :idCuenta")
    List<ServicioDeLaCuenta> findByCuenta(@Param("idCuenta") int idCuenta);
    
    /**
     * Obtiene todos los servicios pendientes de una cuenta específica
     */
    @Query("SELECT s FROM ServicioDeLaCuenta s WHERE s.cuenta.idCuenta = :idCuenta AND s.estadoServicio = :estadoServicio")
    List<ServicioDeLaCuenta> findByCuentaAndEstado(@Param("idCuenta") int idCuenta, @Param("estadoServicio") com.gpp.servisoft.model.enums.EstadoServicio estadoServicio);
}