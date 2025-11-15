package com.gpp.servisoft.model.dto;

import java.time.LocalDate;
import java.util.List;

import com.gpp.servisoft.model.entities.DatosClienteFactura;
import com.gpp.servisoft.model.entities.DatosServicioFactura;
import com.gpp.servisoft.model.entities.DetalleFactura;
import com.gpp.servisoft.model.enums.EstadoFactura;
import com.gpp.servisoft.model.enums.Periodicidad;
import com.gpp.servisoft.model.enums.TipoComprobante;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FacturacionDTO {

    // Datos para mostrar en la tabla Principal
    private int idFactura;

    private String nroComprobante;

    private String razonSocial;

    private LocalDate fechaEmision;

    private LocalDate fechaVencimiento;

    private double montoTotal;


    // Detalles Adicionales

    // En caso de querer mostrar el iva de forma separada
    private double totalIva; 

    // Suma de todos los montos de cada servicio
    private double subtotal; 

    // Para mostrar el tipo de comprobante
    private TipoComprobante tipo;

    // Para mostrar los servicios que se facturaron
    List<DatosServicioFactura> serviciosInvolucrados;

    // Paar mostrar datos fiscales del Cliente
    DatosClienteFactura datosClienteFactura;

    // Detalles del Servicio
    List<DetalleFactura> detalleFacturas;
    
    // Estados que puede tener la misma
    private EstadoFactura estado;

    private Periodicidad periodicidad;

}
