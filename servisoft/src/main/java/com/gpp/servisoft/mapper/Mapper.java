package com.gpp.servisoft.mapper;

import java.time.LocalDate;

import com.gpp.servisoft.model.dto.FacturacionDTO;
import com.gpp.servisoft.model.dto.ServicioDeLaCuentaDto;
import com.gpp.servisoft.model.entities.Factura;
import com.gpp.servisoft.model.entities.ServicioDeLaCuenta;
import com.gpp.servisoft.model.enums.EstadoFactura;

public class Mapper {

    public static ServicioDeLaCuentaDto toDto(ServicioDeLaCuenta sdc) {
        if (sdc == null)
            return null;

        return ServicioDeLaCuentaDto.builder().idServicioDeLaCuenta(sdc.getIdServicioDeLaCuenta())
                .cuenta(sdc.getCuenta())
                .servicio(sdc.getServicio())
                .estadoServicio(sdc.getEstadoServicio())
                .build();
    }

    public static FacturacionDTO toDto(Factura factura) {
        if (factura == null)
            return null;

        return FacturacionDTO.builder()
                .idFactura(factura.getIdFactura())
                .nroComprobante(factura.getNroComprobante())
                .razonSocial(factura.getDatosClienteFactura().getRazonSocial())
                .fechaEmision(factura.getFechaEmision())
                .fechaVencimiento(factura.getFechaVencimiento())
                .montoTotal(factura.getMontoTotal() == null ? 0.0d : factura.getMontoTotal())
                .totalIva(
                        factura.getDetallesFacturas()
                                .stream()
                                .mapToDouble(detalle -> detalle.getIvaCalculado() == null ? 0.0d : detalle.getIvaCalculado())
                                .sum())
                .subtotal(
                        factura.getDetallesFacturas()
                                .stream()
                                .mapToDouble(detalle -> detalle.getSubtotal() == null ? 0.0d : detalle.getSubtotal())
                                .sum())
                .tipo(factura.getTipoComprobante())
                .serviciosInvolucrados(factura.getDatosServicioFactura())
                .estado(calcularEstado(factura))
                .periodicidad(factura.getPeriodicidad())
                .detalleFacturas(factura.getDetallesFacturas())
                .datosClienteFactura(factura.getDatosClienteFactura())
                .build();
    }

    /**
     * Calcula el estado de la factura basándose en sus pagos, notas de crédito y
     * fecha de vencimiento.
     * 
     * @param factura La entidad Factura con sus datos completos
     * @return EstadoFactura correspondiente según la lógica de negocio
     */
    private static EstadoFactura calcularEstado(Factura factura) {
        // Validación de entrada
        if (factura == null) {
            throw new IllegalArgumentException("La factura no puede ser nula");
        }

        // --- Lógica 1: ANULADA ---
        // Si existe nota de crédito, la factura está anulada
        if (factura.getNotaDeCredito() != null) {
            return EstadoFactura.ANULADA;
        }

        // Validar que el monto total sea válido
        Double montoTotal = factura.getMontoTotal();
        if (montoTotal == null || montoTotal < 0.0) {
            throw new IllegalStateException("El monto total de la factura es inválido: " + montoTotal);
        }

        // --- Lógica 2: PAGADA / PARCIALMENTE PAGADA ---
        double totalPagado = 0.0;

        if (factura.getPagos() != null && !factura.getPagos().isEmpty()) {
            totalPagado = factura.getPagos()
                    .stream()
                    .mapToDouble(pago -> pago.getMonto() == null ? 0.0d : pago.getMonto())
                    .sum();
        }

        // Tolerancia para comparación de doubles (1 centavo)
        double epsilon = 0.01;

        if (totalPagado >= (montoTotal - epsilon)) {
            return EstadoFactura.PAGADA;
        }

        // --- Lógica 3: VENCIDA ---
        // Comparamos contra la fecha actual (HOY)
        LocalDate hoy = LocalDate.now();
        boolean estaVencida = factura.getFechaVencimiento() != null &&
                factura.getFechaVencimiento().isBefore(hoy);

        if (estaVencida) {
            return EstadoFactura.VENCIDA;
        }

        // --- Lógica 4: PARCIALMENTE PAGADA ---
        if (totalPagado > epsilon) {
            return EstadoFactura.PARCIALMENTE_PAGADA;
        }

        // --- Lógica 5: PENDIENTE (default) ---
        return EstadoFactura.PENDIENTE;
    }
}
