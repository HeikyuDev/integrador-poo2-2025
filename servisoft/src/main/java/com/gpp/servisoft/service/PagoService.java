package com.gpp.servisoft.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gpp.servisoft.model.dto.PagoDto;
import com.gpp.servisoft.model.entities.Factura;
import com.gpp.servisoft.model.entities.Pago;
import com.gpp.servisoft.model.enums.TipoPago;
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
        // Busca la factura en la base de datos. Si no existe, lanza una excepción
        Factura factura = facturacionRepository.findById(pagoDto.getIdFacturacion())
                .orElseThrow(() -> new IllegalArgumentException("Factura no existe para el ID proporcionado"));
        
        // PASO 3: Crear nueva instancia de Pago
        // Se crea un nuevo objeto Pago sin argumentos (utiliza el constructor por defecto)
        Pago pago = new Pago();

        // Configurar los datos del pago
        pago.setFactura(factura);                      // Asociar la factura obtenida
        pago.setMonto(factura.getMontoTotal());        // Usar el monto total de la factura
        pago.setFechaPago(LocalDate.now());            // Registrar la fecha actual como fecha de pago
        pago.setMetodoPago(pagoDto.getMetodoPago());   // Establecer el método de pago seleccionado
        pago.setTipoPago(TipoPago.TOTAL);              // Especificar que es un pago total

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

}
