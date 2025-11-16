package com.gpp.servisoft.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.gpp.servisoft.model.entities.Servicio;
import com.gpp.servisoft.model.enums.Estado;

public interface ServicioRepository extends JpaRepository<Servicio, Integer> {

    List<Servicio> findByEstado(Estado activo);

    List<Servicio> findByNombreServicioContainingIgnoreCase(String nombre);

    boolean existsByNombreServicioIgnoreCase(String nombre);

    boolean existsByNombreServicioIgnoreCaseAndIdServicioNot(String nombre, int idExcluir);

}
