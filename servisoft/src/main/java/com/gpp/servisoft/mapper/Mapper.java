package com.gpp.servisoft.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.gpp.servisoft.model.dto.DatosClienteFacturaDto;
import com.gpp.servisoft.model.dto.DatosServicioFacturaDto;
import com.gpp.servisoft.model.dto.DetalleFacturaDto;
import com.gpp.servisoft.model.dto.FacturacionDTO;
import com.gpp.servisoft.model.dto.FacturacionMasivaDto;
import com.gpp.servisoft.model.dto.ServicioDeLaCuentaDto;
import com.gpp.servisoft.model.entities.DatosClienteFactura;
import com.gpp.servisoft.model.entities.DetalleFactura;
import com.gpp.servisoft.model.entities.Factura;
import com.gpp.servisoft.model.entities.FacturacionMasiva;
import com.gpp.servisoft.model.entities.ServicioDeLaCuenta;

public class Mapper {

    public static ServicioDeLaCuentaDto toDto(ServicioDeLaCuenta sdc) {
        if (sdc == null)
            return null;

        return ServicioDeLaCuentaDto.builder().idServicioDeLaCuenta(sdc.getIdServicioDeLaCuenta())
                .cuenta(sdc.getCuenta())
                .servicio(sdc.getServicio())
                .cantidadDePreferencia(sdc.getCantidadDePreferencia())
                .estadoServicio(sdc.getEstadoServicio())
                .build();
    }

    /**
     * Convierte una Factura a FacturacionDTO con solo DTOs, sin referencias a
     * entidades.
     */
    public static FacturacionDTO toDto(Factura factura) {
        if (factura == null)
            return null;

        return FacturacionDTO.builder()
                .idFactura(factura.getIdFactura())
                .nroComprobante(factura.getNroComprobante())
                .razonSocial(factura.getDatosClienteFactura().getRazonSocial())
                .fechaEmision(factura.getFechaEmision())
                .fechaVencimiento(factura.getFechaVencimiento())
                .montoTotal(factura.getMontoTotal() != null ? (double)factura.getMontoTotal() : 0.0)
                .saldo(factura.getSaldo())
                .totalIva(
                        factura.getDetallesFacturas()
                                .stream()
                                .mapToDouble(detalle -> detalle.getIvaCalculado() == null ? 0.0d : (double) detalle.getIvaCalculado())
                                .sum())
                .subtotal(
                        factura.getDetallesFacturas()
                                .stream()
                                .mapToDouble(detalle -> detalle.getSubtotal() == null ? 0.0d : (double)detalle.getSubtotal())
                                .sum())
                .tipo(factura.getTipoComprobante())
                // Mapear DTOs
                .serviciosInvolucrados(mapearDatosServicioFactura(factura.getDatosServicioFactura()))
                .estado(factura.calcularEstado())
                .periodicidad(factura.getPeriodicidad())
                .detalleFacturas(mapearDetalleFacturas(factura.getDetallesFacturas()))
                .datosClienteFactura(mapearDatosClienteFactura(factura.getDatosClienteFactura()))
                .build();
    }

    /**
     * Convierte una FacturacionMasiva a FacturacionMasivaDto con lista de FacturacionDTO
     */
    public static FacturacionMasivaDto toDto(FacturacionMasiva facturacionMasiva) {
        if (facturacionMasiva == null)
            return null;

        return FacturacionMasivaDto.builder()
                .idFacturacionMasiva(facturacionMasiva.getIdFacturacionMasiva())
                .cantidadDeFacturas(facturacionMasiva.getCantidadDeFacturas())
                .montoTotal(facturacionMasiva.getMontoTotal() == null ? 0.0d : (double)facturacionMasiva.getMontoTotal())
                .fechaEmision(facturacionMasiva.getFechaEmision())
                .facturas(mapearFacturas(facturacionMasiva.getFacturas()))
                .build();
    }

    /**
     * Convierte una lista de Facturas a lista de FacturacionDTO
     */
    private static List<FacturacionDTO> mapearFacturas(List<Factura> facturas) {
        if (facturas == null || facturas.isEmpty())
            return List.of();

        return facturas.stream()
                .map(Mapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una lista de DatosServicioFactura a DTOs
     */
    private static List<DatosServicioFacturaDto> mapearDatosServicioFactura(
            List<com.gpp.servisoft.model.entities.DatosServicioFactura> servicios) {
        if (servicios == null || servicios.isEmpty())
            return List.of();

        return servicios.stream()
                .map(servicio -> DatosServicioFacturaDto.builder()
                        .idDatosServicioFactura(servicio.getIdDatosServicioFactura())
                        .nombreServicio(servicio.getNombreServicio())
                        .descripcionServicio(servicio.getDescripcionServicio())
                        .precioActual(servicio.getPrecioActual())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Convierte una lista de DetalleFactura a DTOs
     */
    private static List<DetalleFacturaDto> mapearDetalleFacturas(List<DetalleFactura> detalles) {
        if (detalles == null || detalles.isEmpty()) return List.of();
        
        return detalles.stream()
                .map(detalle -> DetalleFacturaDto.builder()
                        .idDetalleFactura(detalle.getIdDetalleFactura())
                        .nombreServicio(detalle.getServicioDeLaCuenta().getServicio().getNombreServicio())
                        .cantidad(detalle.getCantidad())
                        .precioUnitario(detalle.getPrecioUnitario())
                        .subtotal(detalle.getSubtotal())
                        .ivaCalculado(detalle.getIvaCalculado())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Convierte DatosClienteFactura a DTO
     */
    private static DatosClienteFacturaDto mapearDatosClienteFactura(DatosClienteFactura datosCliente) {
        if (datosCliente == null) return null;
        
        return DatosClienteFacturaDto.builder()
                .idCuenta(datosCliente.getIdCuenta())
                .domicilioFiscal(datosCliente.getDomicilioFiscal())
                .condicionFrenteIVA(datosCliente.getCondicionFrenteIVA().toString())
                .cuit(datosCliente.getCuit())
                .razonSocial(datosCliente.getRazonSocial())
                .build();
    }

}

