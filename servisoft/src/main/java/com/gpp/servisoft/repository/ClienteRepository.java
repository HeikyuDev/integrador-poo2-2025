package com.gpp.servisoft.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.gpp.servisoft.model.entities.Cliente;
import com.gpp.servisoft.model.enums.Estado;
import com.gpp.servisoft.model.enums.TipoCliente;

/**
 * *Cliente Repository*
 * Interfaz del repositorio para manejar las operaciones de base de datos relacionadas con la entidad Cliente.
 */
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    // Buscar clientes por estado
    List<Cliente> findByEstado(Estado estado);
    
    // Buscar clientes por tipo
    List<Cliente> findByTipoCliente(TipoCliente tipoCliente);
    
    // Buscar cliente por correo electrónico
    Optional<Cliente> findByCorreoElectronico(String correoElectronico);
    
    // Buscar clientes por nombre (búsqueda parcial)
    List<Cliente> findByNombreContainingIgnoreCase(String nombre);
    
    // Verificar si existe un cliente con un correo específico
    boolean existsByCorreoElectronico(String correoElectronico);

}
