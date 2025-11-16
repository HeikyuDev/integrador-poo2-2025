package com.gpp.servisoft.service;

import com.gpp.servisoft.model.entities.Servicio;
import com.gpp.servisoft.model.enums.Estado;
import com.gpp.servisoft.repository.ServicioRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ServicioService {
    private final ServicioRepository servicioRepository;

	 /**
     * Lista todos los servicios
     */
    public List<Servicio> listarTodos() {
        return servicioRepository.findAll();
    }

    /**
     * Lista solo servicios activos
     */
    public List<Servicio> listarActivos() {
        return servicioRepository.findByEstado(Estado.ACTIVO);
    }

    /**
     * Lista servicios por estado
     */
    public List<Servicio> listarPorEstado(Estado estado) {
        return servicioRepository.findByEstado(estado);
    }

    /**
     * Busca un servicio por ID
     */
    public Servicio buscarPorId(int id) {
        return servicioRepository.findById(id).orElse(null);
    }

    /**
     * Busca servicios por nombre (búsqueda parcial)
     */
    public List<Servicio> buscarPorNombre(String nombre) {
        return servicioRepository.findByNombreServicioContainingIgnoreCase(nombre);
    }

    /**
     * Guarda un nuevo servicio
     */
    public Servicio guardar(Servicio servicio) {
        // Validaciones adicionales si son necesarias
        validarServicio(servicio);
        return servicioRepository.save(servicio);
    }

    /**
     * Actualiza un servicio existente
     */
    public Servicio actualizar(Servicio servicio) {
        // Verificar que el servicio existe
        if (servicio.getIdServicio() == 0 || !servicioRepository.existsById(servicio.getIdServicio())) {
            throw new IllegalArgumentException("El servicio no existe");
        }
        
        validarServicio(servicio);
        return servicioRepository.save(servicio);
    }

    /**
     * Elimina un servicio
     * Nota: Puede ser baja lógica (cambiar estado) o física según necesidad
     */
    public void eliminar(int id) {
        if (!servicioRepository.existsById(id)) {
            throw new IllegalArgumentException("El servicio no existe");
        }
        
        // Opción 1: Eliminación física
        servicioRepository.deleteById(id);
        
        // Opción 2: Baja lógica (comentar la línea anterior y descomentar estas)
        // Servicio servicio = buscarPorId(id);
        // servicio.setEstado(Estado.INACTIVO);
        // servicioRepository.save(servicio);
    }

    /**
     * Cambia el estado de un servicio
     */
    public void cambiarEstado(int id, Estado nuevoEstado) {
        Servicio servicio = buscarPorId(id);
        if (servicio == null) {
            throw new IllegalArgumentException("El servicio no existe");
        }
        servicio.setEstado(nuevoEstado);
        servicioRepository.save(servicio);
    }

    /**
     * Verifica si un servicio existe por nombre
     */
    public boolean existePorNombre(String nombre) {
        return servicioRepository.existsByNombreServicioIgnoreCase(nombre);
    }

    /**
     * Verifica si un servicio existe por nombre, excluyendo un ID específico
     * Útil para validar nombres únicos en actualizaciones
     */
    public boolean existePorNombreExcluyendoId(String nombre, int idExcluir) {
        return servicioRepository.existsByNombreServicioIgnoreCaseAndIdServicioNot(nombre, idExcluir);
    }

    /**
     * Validaciones de negocio para el servicio
     */
    private void validarServicio(Servicio servicio) {
        // Validar que el nombre no esté duplicado
        if (servicio.getIdServicio() == 0) {
            // Nuevo servicio
            if (existePorNombre(servicio.getNombreServicio())) {
                throw new IllegalArgumentException("Ya existe un servicio con ese nombre");
            }
        } else {
            // Actualización
            if (existePorNombreExcluyendoId(servicio.getNombreServicio(), servicio.getIdServicio())) {
                throw new IllegalArgumentException("Ya existe un servicio con ese nombre");
            }
        }

        // Validar monto positivo
        if (servicio.getMontoServicio() < 0) {
            throw new IllegalArgumentException("El monto no puede ser negativo");
        }

        // Validar alícuota en rango válido
        if (servicio.getAlicuota() < 0 || servicio.getAlicuota() > 1) {
            throw new IllegalArgumentException("La alícuota debe estar entre 0 y 1");
        }
    }

    /**
     * Calcula el monto total con IVA de un servicio
     */
    public double calcularMontoConIva(Servicio servicio) {
        double monto = servicio.getMontoServicio();
        double iva = monto * servicio.getAlicuota();
        return monto + iva;
    }

    /**
     * Calcula solo el IVA de un servicio
     */
    public double calcularIva(Servicio servicio) {
        return servicio.getMontoServicio() * servicio.getAlicuota();
    }
}
