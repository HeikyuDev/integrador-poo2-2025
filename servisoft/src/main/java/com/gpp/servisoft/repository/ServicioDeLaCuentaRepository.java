package com.gpp.servisoft.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gpp.servisoft.model.entities.ServicioDeLaCuenta;
import com.gpp.servisoft.model.entities.Cuenta;
import com.gpp.servisoft.model.enums.EstadoServicio;
import java.util.List;

@Repository
public interface  ServicioDeLaCuentaRepository extends JpaRepository<ServicioDeLaCuenta, Integer> {
    
    /**
     * Obtiene todos los servicios de una cuenta en estado específico.
     * Ej: findByCuentaAndEstadoServicio(cuenta, EstadoServicio.PENDIENTE)
     */
    List<ServicioDeLaCuenta> findByCuentaAndEstadoServicio(Cuenta cuenta, EstadoServicio estado);
    
    /**
     * Obtiene todos los servicios de una cuenta sin filtrar por estado.
     */
    List<ServicioDeLaCuenta> findByCuenta(Cuenta cuenta);
}
