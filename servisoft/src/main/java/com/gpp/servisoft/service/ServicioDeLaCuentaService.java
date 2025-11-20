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
    public ServicioDeLaCuenta agregarServicioACuenta(int idCuenta, int idServicio) {
        // Verificar que no exista ya la relación
        servicioDeLaCuentaRepository.findByCuentaAndServicio(idCuenta, idServicio)
            .ifPresent(s -> {
                throw new RuntimeException("El servicio ya está asociado a esta cuenta");
            });

        Cuenta cuenta = cuentaRepository.findById(idCuenta)
            .orElseThrow(() -> new RuntimeException("No existe la cuenta con ID: " + idCuenta));

        Servicio servicio = servicioRepository.findById(idServicio)
            .orElseThrow(() -> new RuntimeException("No existe el servicio con ID: " + idServicio));

        // Crear la relación y congelar el precio actual
        ServicioDeLaCuenta servicioCuenta = new ServicioDeLaCuenta();
        servicioCuenta.setCuenta(cuenta);
        servicioCuenta.setServicio(servicio);
        // servicioCuenta.setCantidadDePreferencia(idServicio);
        servicioCuenta.setEstadoServicio(EstadoServicio.PENDIENTE);

        return servicioDeLaCuentaRepository.save(servicioCuenta);
    }
    /**
     * Agrega múltiples servicios a una cuenta
     * @param idCuenta ID de la cuenta
     * @param idsServicios Lista de IDs de servicios
     */
    @Transactional
    public void agregarMultiplesServicios(int idCuenta, List<Integer> idsServicios) {
        for (Integer idServicio : idsServicios) {
            // Verificar si ya existe antes de agregar
            if (servicioDeLaCuentaRepository.findByCuentaAndServicio(idCuenta, idServicio).isEmpty()) {
                agregarServicioACuenta(idCuenta, idServicio);
            }
        }
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
    public List<ServicioDeLaCuenta> obtenerServiciosDeCuenta(int idCuenta) {
        return servicioDeLaCuentaRepository.findByCuenta(idCuenta);
    }
    /**
     * Obtiene todos los servicios pendientes de una cuenta específica
     * @param idCuenta ID de la cuenta
     * @return Lista de DTOs de servicios pendientes
     */
    public List<ServicioDeLaCuentaDto> obtenerServiciosPorCuentaPendiente(Integer idCuenta) {
        return servicioDeLaCuentaRepository
                .findByCuentaAndEstado(idCuenta, EstadoServicio.PENDIENTE)
                .stream().map(Mapper::toDto).toList();
    }
}