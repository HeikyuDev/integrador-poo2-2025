package com.gpp.servisoft.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gpp.servisoft.model.entities.Cliente;
import com.gpp.servisoft.model.entities.Cuenta;
import com.gpp.servisoft.model.enums.Estado;
import com.gpp.servisoft.repository.CuentaRepository;
import com.gpp.servisoft.repository.ClienteRepository;

/**
 * *Cuenta Service*
 * Métodos del servicio para manejar la lógica de negocio relacionada
 * con la entidad Cuenta.
 */
@Service
public class CuentaService {
/**
    * {@link CuentaRepository}  Inyección de la dependencia del repositorio PersonaRepository, para manejar la información almacenada en la base de datos.
*/
// Acá van los métodos de la clase Cuenta
    @Autowired
    private CuentaRepository cuentaRepository;
    @Autowired
    private ClienteRepository clienteRepository;

 /**
    * Obtener listado de todas las cuentas NO dadas de baja.
    * @return Lista de cuentas.
*/
    public List<Cuenta> obtenerCuentasActivas() {
        return cuentaRepository.findByEstadoNot(Estado.INACTIVO);
    }

/**
     * Alta de una nueva cuenta asociada a un cliente.
     * @param cuenta Datos de la cuenta a crear.
     * @param idCliente ID del cliente al que se asociará.
     * @return Cuenta creada.
*/
    public Cuenta altaCuenta(Cuenta cuenta, int idCliente) {

        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("No existe el cliente con ID: " + idCliente));

        cuenta.setCliente(cliente);

        return cuentaRepository.save(cuenta);
    }

/**
    * Obtener una cuenta específica por ID (que no esté dada de baja).
    * Usa JOIN FETCH para cargar eagerly las relaciones lazy (cliente y servicios).
    * @param idCuenta ID de la cuenta.
    * @return Cuenta encontrada con sus relaciones cargadas.
*/
public Cuenta obtenerPorId(int idCuenta) {
    return cuentaRepository.findByIdConRelaciones(idCuenta)
            .orElseThrow(() -> new RuntimeException("No existe la cuenta activa con ID: " + idCuenta));
}

/**
    * Modifica los datos de una cuenta existente.
    * @param idCuenta ID de la cuenta a modificar.
    * @param datosActualizados Nuevos datos.
    * @return Cuenta modificada.
*/
    public Cuenta modificarCuenta(int idCuenta, Cuenta datosActualizados) {

        Cuenta existente = cuentaRepository.findById(idCuenta)
                .orElseThrow(() -> new RuntimeException("No existe la cuenta con ID: " + idCuenta));

        existente.setDomicilioFiscal(datosActualizados.getDomicilioFiscal());
        existente.setCondicionFrenteIVA(datosActualizados.getCondicionFrenteIVA());
        existente.setCuit(datosActualizados.getCuit());
        existente.setRazonSocial(datosActualizados.getRazonSocial());
        existente.setEstado(datosActualizados.getEstado());

        return cuentaRepository.save(existente);
    }

/**
    * Realiza una baja lógica de una cuenta (soft delete).
    * Su estado se cambia a INACTIVO.
    * @param idCuenta ID de la cuenta a dar de baja.
*/
    public void bajaCuenta(int idCuenta) {
        Cuenta cuenta = cuentaRepository.findByIdCuentaAndEstadoNot(idCuenta, Estado.INACTIVO)
                .orElseThrow(() -> new RuntimeException("No existe la cuenta activa con ID: " + idCuenta));

        cuenta.setEstado(Estado.INACTIVO);
        cuentaRepository.save(cuenta);
    }

    // METODOS QUE SE NECESITAN PARA EL MODULO DE FACTURACION

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
