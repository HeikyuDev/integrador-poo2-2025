package com.gpp.servisoft.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gpp.servisoft.mapper.Mapper;
import com.gpp.servisoft.model.dto.ServicioDeLaCuentaDto;
import com.gpp.servisoft.model.entities.Cuenta;
import com.gpp.servisoft.model.entities.Servicio;
import com.gpp.servisoft.model.entities.ServicioDeLaCuenta;
import com.gpp.servisoft.model.enums.EstadoServicio;
import com.gpp.servisoft.repository.CuentaRepository;
import com.gpp.servisoft.repository.ServicioDeLaCuentaRepository;
import com.gpp.servisoft.repository.ServicioRepository;

@Service
@SuppressWarnings("null")
public class ServicioDeLaCuentaService {

    @Autowired
    private ServicioDeLaCuentaRepository servicioDeLaCuentaRepository;
    
    @Autowired
    private CuentaRepository cuentaRepository;
    
    @Autowired
    private ServicioRepository servicioRepository;

    /**
     * Agrega un servicio a una cuenta específica
     * @param idCuenta ID de la cuenta
     * @param idServicio ID del servicio a agregar
     * @return ServicioDeLaCuenta creado
     */
    @Transactional
    public ServicioDeLaCuenta agregarServicioACuenta(int idCuenta, int idServicio, int cantidad) {
        Cuenta cuenta = cuentaRepository.findById(idCuenta)
            .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
        
        Servicio servicio = servicioRepository.findById(idServicio)
            .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        
        // Verificar si el servicio ya está asociado
        boolean yaExiste = servicioDeLaCuentaRepository
            .existsByCuentaAndServicio(cuenta, servicio);
        
        if (yaExiste) {
            throw new RuntimeException("El servicio ya está asociado a esta cuenta");
        }
        
        ServicioDeLaCuenta servicioDeLaCuenta = new ServicioDeLaCuenta();
        servicioDeLaCuenta.setCuenta(cuenta);
        servicioDeLaCuenta.setServicio(servicio);
        servicioDeLaCuenta.setCantidadDePreferencia(cantidad);
        servicioDeLaCuenta.setEstadoServicio(EstadoServicio.PENDIENTE);
        
        return servicioDeLaCuentaRepository.save(servicioDeLaCuenta);
    }

    /**
     * Agrega un servicio a una cuenta específica con cantidad personalizada
     * @param idCuenta ID de la cuenta
     * @param idServicio ID del servicio a agregar
     * @return ServicioDeLaCuenta creado
     */
    // Sobrecarga para mantener compatibilidad (cantidad por defecto = 1)
    public ServicioDeLaCuenta agregarServicioACuenta(int idCuenta, int idServicio) {
        return agregarServicioACuenta(idCuenta, idServicio, 1);
    }
    /**
     * Actualiza la cantidad de un servicio en una cuenta
     * @param idServicioDeLaCuenta ID de la relación servicio-cuenta
     * @param nuevaCantidad Nueva cantidad a establecer
     */
    @Transactional
    public void actualizarCantidad(int idServicioDeLaCuenta, int nuevaCantidad) {
    ServicioDeLaCuenta servicioDeLaCuenta = servicioDeLaCuentaRepository
        .findById(idServicioDeLaCuenta)
        .orElseThrow(() -> new RuntimeException("Servicio de cuenta no encontrado"));
    
    if (nuevaCantidad < 1) {
        throw new RuntimeException("La cantidad debe ser al menos 1");
    }
    
    servicioDeLaCuenta.setCantidadDePreferencia(nuevaCantidad);
    servicioDeLaCuentaRepository.save(servicioDeLaCuenta);
}

    /**
     * Agrega múltiples servicios a una cuenta
     * @param idCuenta ID de la cuenta
     * @param idsServicios Lista de IDs de servicios
     * @param cantidades Lista opcional de cantidades para cada servicio
     */
    @Transactional
    public void agregarMultiplesServicios(int idCuenta, List<Integer> idsServicios, List<Integer> cantidades) {
        for (int i = 0; i < idsServicios.size(); i++) {
            Integer idServicio = idsServicios.get(i);
            // Obtener cantidad, por defecto 1
            int cantidad = (cantidades != null && i < cantidades.size() && cantidades.get(i) != null) 
                ? cantidades.get(i) 
                : 1;
            
            // Verificar si ya existe antes de agregar
            if (servicioDeLaCuentaRepository.findByCuentaAndServicio(idCuenta, idServicio).isEmpty()) {
                agregarServicioACuenta(idCuenta, idServicio, cantidad);
            }
        }
    }
    
    /**
     * Agrega múltiples servicios a una cuenta (sobrecarga para compatibilidad)
     * @param idCuenta ID de la cuenta
     * @param idsServicios Lista de IDs de servicios
     */
    @Transactional
    public void agregarMultiplesServicios(int idCuenta, List<Integer> idsServicios) {
        agregarMultiplesServicios(idCuenta, idsServicios, null);
    }
    /**
     * Elimina un servicio de una cuenta
     * @param idServicioCuenta ID de la relación a eliminar
     */
    @Transactional
    public void eliminarServicioDeCuenta(int idServicioCuenta) {
        ServicioDeLaCuenta servicioCuenta = servicioDeLaCuentaRepository.findById(idServicioCuenta)
            .orElseThrow(() -> new RuntimeException("No existe la relación con ID: " + idServicioCuenta));
        
        servicioDeLaCuentaRepository.delete(servicioCuenta);
    }

    /**
     * Obtiene todos los servicios de una cuenta
     */
    @Transactional(readOnly = true)
    public List<ServicioDeLaCuenta> obtenerServiciosDeCuenta(int idCuenta) {
        return servicioDeLaCuentaRepository.findByCuenta(idCuenta);
    }
    /**
     * Obtiene todos los servicios pendientes de una cuenta específica
     * @param idCuenta ID de la cuenta
     * @return Lista de DTOs de servicios pendientes
     */
    @Transactional(readOnly = true)
    public List<ServicioDeLaCuentaDto> obtenerServiciosPorCuentaPendiente(Integer idCuenta) {
        return servicioDeLaCuentaRepository
                .findByCuentaAndEstado(idCuenta, EstadoServicio.PENDIENTE)
                .stream().map(Mapper::toDto).toList();
    }
}