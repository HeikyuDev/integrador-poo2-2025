package com.gpp.servisoft.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.gpp.servisoft.model.dto.PagoConsultaDto;
import com.gpp.servisoft.model.dto.PagoDto;
import com.gpp.servisoft.model.entities.DatosClienteFactura;
import com.gpp.servisoft.model.entities.Factura;
import com.gpp.servisoft.model.entities.Pago;
import com.gpp.servisoft.repository.FacturacionRepository;
import com.gpp.servisoft.repository.PagoRepository;

/**
 * Servicio de gestión de pagos.
 * Proporciona funcionalidades para procesar y registrar pagos de facturas.
 */
@Service
public class PagoService {

    @Autowired
    private FacturacionRepository facturacionRepository;

    @Autowired
    private PagoRepository pagoRepository;

    /**
     * Consulta paginada de pagos con filtros opcionales.
     */
    public Page<PagoConsultaDto> consultarPagos(Integer cuentaId, String comprobante,
            LocalDate fechaDesde, LocalDate fechaHasta, Pageable pageable) {

        if (fechaDesde != null && fechaHasta != null && fechaDesde.isAfter(fechaHasta)) {
            throw new IllegalArgumentException("La fecha desde no puede ser posterior a la fecha hasta");
        }

        Page<Pago> paginaDePagos = pagoRepository.findByFilters(cuentaId,
                comprobante != null ? comprobante.trim() : null,
                fechaDesde,
                fechaHasta,
                pageable);

        return paginaDePagos.map(this::mapearPagoConsulta);
    }

    /**
     * Procesa un pago total de una factura.
     * 
     * Este método realiza las siguientes operaciones:
     * 1. Valida que el DTO de pago no sea nulo y contenga los datos requeridos
     * 2. Obtiene la factura asociada del repositorio
     * 3. Crea un nuevo registro de pago con el monto total de la factura
     * 4. Guarda el pago en la base de datos
     * 
     * @param pagoDto el DTO que contiene la información del pago (método de pago, ID de factura, etc.)
     * @return el objeto Pago creado y guardado en la base de datos
     * @throws IllegalArgumentException si el pagoDto es nulo, falta información requerida o la factura no existe
     */
    public Pago procesarPagoTotal(PagoDto pagoDto) {
        // PASO 0: Validación inicial del DTO
        // Verifica que el objeto pagoDto no sea nulo antes de procesarlo
        if (pagoDto == null) {
            throw new IllegalArgumentException("Pago dto no puede ser nulo");
        }
        // PASO 1: Validación de atributos requeridos
        // Verifica que el pagoDto contenga todos los datos necesarios (método de pago, ID de factura válido)
        validarAtributos(pagoDto);

        // PASO 2: Obtener la Factura Asociada al Pago
        Factura factura = obtenerFacturaAsociada(pagoDto.getIdFacturacion());
        
        // PASO 3: Crear nueva instancia de Pago
        Pago pago = new Pago();

        // Configurar los datos del pago
        pago.setFactura(factura);                      // Asociar la factura obtenida
        pago.setMonto(pagoDto.getMonto());        // Usar el monto total de la factura
        pago.setFechaPago(LocalDate.now());            // Registrar la fecha actual como fecha de pago
        pago.setMetodoPago(pagoDto.getMetodoPago());   // Establecer el método de pago seleccionado

        // PASO 4: Persistir el pago en la base de datos
        // La relación con Factura se mantiene automáticamente gracias a JPA
        // No es necesario guardar la factura nuevamente
        pagoRepository.save(pago);
        return pago;
    }

    private PagoConsultaDto mapearPagoConsulta(Pago pago) {
        if (pago == null) {
            throw new IllegalArgumentException("El pago no puede ser nulo");
        }

        Factura factura = pago.getFactura();
        DatosClienteFactura datosCliente = factura != null ? factura.getDatosClienteFactura() : null;

        return PagoConsultaDto.builder()
                .idPago(pago.getIdPago())
                .monto(pago.getMonto())
                .fechaPago(pago.getFechaPago())
                .metodoPago(pago.getMetodoPago())
                .idFactura(factura != null ? factura.getIdFactura() : null)
                .nroComprobante(factura != null ? factura.getNroComprobante() : null)
                .idCuenta(datosCliente != null ? datosCliente.getIdCuenta() : null)
                .razonSocialCliente(datosCliente != null ? datosCliente.getRazonSocial() : null)
                .build();
    }

    public Pago procesarPagoParcial(PagoDto pagoDto) {
        // Verifica que el objeto pagoDto no sea nulo antes de procesarlo
        if (pagoDto == null) {
            throw new IllegalArgumentException("Pago dto no puede ser nulo");
        }

        // PASO 1: Validación de atributos requeridos
        validarAtributos(pagoDto);

        // Validamos que el pago monto a pagar sea valido
        if(pagoDto.getMonto() <= 0){
            throw new IllegalArgumentException("El monto no puede ser nulo ni negativo !!!");
        }

        // PASO 2: Obtener la Factura Asociada al Pago
        Factura factura = obtenerFacturaAsociada(pagoDto.getIdFacturacion());
        
        // Validar que el monto a pagar no exceda el saldo disponible
        double saldoPendiente = factura.getSaldo();
        if (pagoDto.getMonto() > saldoPendiente) {
            throw new IllegalArgumentException("El monto a pagar (" + pagoDto.getMonto() + 
                    ") no puede exceder el saldo pendiente (" + saldoPendiente + ")");
        }
        
        // PASO 3: Crear nueva instancia de Pago
        // Se crea un nuevo objeto Pago sin argumentos (utiliza el constructor por defecto)

        Pago pago = new Pago();

        // Configurar los datos del pago
        pago.setFactura(factura);                      // Asociar la factura obtenida
        pago.setMonto(pagoDto.getMonto());             // Usar el monto PARCIAL ingresado por el usuario
        pago.setFechaPago(LocalDate.now());            // Registrar la fecha actual como fecha de pago
        pago.setMetodoPago(pagoDto.getMetodoPago());   // Establecer el método de pago seleccionado

        // PASO 4: Persistir el pago en la base de datos
        // La relación con Factura se mantiene automáticamente gracias a JPA
        // No es necesario guardar la factura nuevamente
        pagoRepository.save(pago);

        return pago;
    }

    /**
     * Valida que el DTO de pago contenga todos los atributos requeridos.
     * 
     * Verifica:
     * - Que se haya seleccionado un método de pago (no nulo)
     * - Que el ID de la factura sea válido (no nulo y mayor a 0)
     * 
     * @param pagoDto el DTO a validar
     * @throws IllegalArgumentException si alguna validación falla
     */
    private void validarAtributos(PagoDto pagoDto) {
        // Validar que se haya seleccionado un método de pago
        // El método de pago es obligatorio para procesar cualquier tipo de pago
        if (pagoDto.getMetodoPago() == null) {
            throw new IllegalArgumentException("Se debe seleccionar un metodo de pago para procesar el pago !!!");
        }

        // Validar que se haya proporcionado un ID de factura válido
        // El ID debe ser no nulo y mayor a 0 para ser un identificador válido en la BD
        if (pagoDto.getIdFacturacion() == null || pagoDto.getIdFacturacion() <= 0) {
            throw new IllegalArgumentException("ID invalido !!!");
        }
    }


    private Factura obtenerFacturaAsociada(int idFactura)
    {
        return facturacionRepository.findById(idFactura)
                .orElseThrow(() -> new IllegalArgumentException("Factura no existe para el ID proporcionado"));
    }
}
