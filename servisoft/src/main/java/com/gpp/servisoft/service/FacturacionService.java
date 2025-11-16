package com.gpp.servisoft.service;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gpp.servisoft.mapper.Mapper;
import com.gpp.servisoft.model.dto.FacturacionDTO;
import com.gpp.servisoft.model.dto.FacturacionMasivaDto;
import com.gpp.servisoft.model.dto.ServicioSeleccionadoDto;
import com.gpp.servisoft.model.entities.Cuenta;
import com.gpp.servisoft.model.entities.DatosClienteFactura;
import com.gpp.servisoft.model.entities.DatosServicioFactura;
import com.gpp.servisoft.model.entities.DetalleFactura;
import com.gpp.servisoft.model.entities.Factura;
import com.gpp.servisoft.model.entities.FacturacionMasiva;
import com.gpp.servisoft.model.entities.Servicio;
import com.gpp.servisoft.model.entities.ServicioDeLaCuenta;
import com.gpp.servisoft.model.enums.CondicionFrenteIVA;
import com.gpp.servisoft.model.enums.Estado;
import com.gpp.servisoft.model.enums.EstadoServicio;
import com.gpp.servisoft.model.enums.Periodicidad;
import com.gpp.servisoft.model.enums.TipoComprobante;
import com.gpp.servisoft.repository.CuentaRepository;
import com.gpp.servisoft.repository.FacturacionMasivaRepository;
import com.gpp.servisoft.repository.FacturacionRepository;
import com.gpp.servisoft.repository.ServicioDeLaCuentaRepository;

@Service
@SuppressWarnings("null")
public class FacturacionService {

    // Utilizamos Autowired para poder inyectar la dependencia del repositorio
    // A la clase, service

    @Autowired
    public FacturacionRepository facturacionRepository;

    @Autowired
    public ServicioDeLaCuentaRepository servicioDeLaCuentaRepository;

    @Autowired
    public CuentaRepository cuentaRepository;

    @Autowired
    public FacturacionMasivaRepository facturacionMasivaRepository;

    /**
     * Obtiene una lista paginada de facturas, permitiendo filtrar por el ID de la cuenta
     * y/o por una búsqueda parcial del número de comprobante.
     *
     * @param idCuenta    El mismo tiene que ser Integer, ya que se acepta que el valor
     * puede ser nulo (Todas las Cuentas)
     * @param comprobante Representa el Comprobante "0001-000000004" de una factura,
     * se utiliza el LIKE para filtrar
     * @param pageable    Objeto de pregunta generado por Spring
     * @return Un objeto Page de FacturacionDTO con los datos de paginación preservados.
     */
    public Page<FacturacionDTO> obtenerFacturas(Integer idCuenta, String comprobante, Pageable pageable) {
        
        // 1. Llama al repositorio y obtienes la página de ENTIDADES (Factura)
        Page<Factura> paginaDeEntidades = facturacionRepository.findFacturasByFilters(idCuenta, comprobante, pageable);

        // 2. Mapea la página de Entidades a una página de DTOs
        //    Esto aplica "Mapper::toDto" a cada ítem Y MANTIENE la paginación.
        return paginaDeEntidades.map(Mapper::toDto);
    }

    public Page<FacturacionDTO> obtenerFacturasPendientes(Integer idCuenta, String comprobante, Pageable pageable) {
        // Obtener la página de facturas pendientes usando el repositorio
        // Una factura se considera pendiente si no tiene pagos o el total pagado es menor al monto total
        Page<Factura> paginaDeFacturasPendientes = facturacionRepository.findFacturasPendientesByFilters(idCuenta, comprobante, pageable);
        
        // Mapear cada factura de la página a su correspondiente DTO
        return paginaDeFacturasPendientes.map(Mapper::toDto);
    }

    /**
     * 
     * @return
     */
    public Page<FacturacionMasivaDto> obtenerFacturacionesMasivas(Pageable pageable)
    {
        // Llamo al repositorio y obtengo la pagina de Entidades (Facturaciones Masivas)
        Page<FacturacionMasiva> paginaDeEntidades = facturacionMasivaRepository.findAll(pageable);
        // 2. Mapea la página de Entidades a una página de DTOs
        return paginaDeEntidades.map(Mapper::toDto);
    }

    /**
     * Obtiene una factura por su ID y la convierte a DTO.
     * 
     * @param idFactura ID de la factura a buscar
     * @return FacturacionDTO con los datos de la factura
     * @throws IllegalArgumentException si no se encuentra una factura con el ID proporcionado
     */
    public FacturacionDTO obtenerFacturaPorId(Integer idFactura) {
        Factura factura = facturacionRepository.findById(idFactura)
                .orElseThrow(() -> new IllegalArgumentException("Factura con ID " + idFactura + " no encontrada"));
        
        return Mapper.toDto(factura);
    }

    /**
     * Obtener un Servicio de la Cuenta específico
     * 
     * @param servicioSeleccionado DTO con id y cantidad del servicio
     * @return ServicioDeLaCuenta encontrado
     * @throws IllegalArgumentException si el servicio es null o no existe
     */
    public ServicioDeLaCuenta obtenerServicioDeLaCuenta(ServicioSeleccionadoDto servicioSeleccionado) {
        // Validamos que no sea null el servicio seleccionado
        if (servicioSeleccionado == null) {
            throw new IllegalArgumentException("El servicio seleccionado no puede ser nulo");
        }

        // Obtenemos el servicioDeLaCuenta usando el ID correcto
        return servicioDeLaCuentaRepository.findById(servicioSeleccionado.getIdServicio())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Servicio con ID " + servicioSeleccionado.getIdServicio() + " no encontrado"));
    }

    /**
     * Metodo utilizado para realizar la facturacion Individual, se le provee una
     * lista de los servicios seleccionados por el controller, y Crea y persiste las
     * respectivas entidades.
     * La transacción asegura atomicidad: si algo falla, todo se revierte.
     */
    @Transactional
    public void facturacionIndividual(List<ServicioSeleccionadoDto> listaServicios, Periodicidad periodicidad) {
        // Validamos que lista de servicios no sea invalida (nula / Vacia)
        validarListaServicios(listaServicios);
        List<DetalleFactura> detallesFacturas = crearDetallesFacturaDesdeSeleccion(listaServicios);
        procesarFacturacion(detallesFacturas, periodicidad);
    }

    /**
     * Metodo para realizar facturación masiva. Selecciona automáticamente todos los
     * servicios
     * pendientes de todas las cuentas activas y los factura en un único proceso.
     */
    @Transactional
    public void facturacionMasiva(Periodicidad periodicidad) {

        // Creamos una instancia Vacia de la Facturacion Masiva
        FacturacionMasiva facturacionMasiva = new FacturacionMasiva();

        // Creamos una lista donde la cual vamos a obtener todas las facturas generadas en el proceso

        List<Factura> facturas = new ArrayList<>();


        List<Cuenta> cuentasActivas = cuentaRepository.findByEstado(Estado.ACTIVO);

        if (cuentasActivas.isEmpty()) {
            throw new IllegalArgumentException("No hay cuentas activas para facturar");
        }

        for (Cuenta cuenta : cuentasActivas) {
            if (cuenta.getServiciosDeLaCuenta() != null && !cuenta.getServiciosDeLaCuenta().isEmpty()) {
                List<DetalleFactura> detalles = crearDetallesFacturaDesdeServicios(cuenta.getServiciosDeLaCuenta());
                if (!detalles.isEmpty()) {
                    facturas.add(procesarFacturacion(detalles, periodicidad));
                }
            }
        }

        // Seteamos los atributos de la facturacion
        facturacionMasiva.setFacturas(facturas);
        facturacionMasiva.setCantidadDeFacturas(facturas.size());
        facturacionMasiva.setFechaEmision(LocalDate.now());
        facturacionMasiva.setMontoTotal((double) facturas.stream()
                .mapToDouble(factura -> factura.getMontoTotal() != null ? factura.getMontoTotal() : 0.0)
                .sum());
        facturacionMasivaRepository.save(facturacionMasiva);
    }

    /**
     * Procesa la facturación: crea la factura, persiste y actualiza estados de
     * servicios.
     * Este es el método central de la lógica de facturación.
     * 
     * @param detallesFacturas Lista de detalles a facturar
     * @param periodicidad     Período de vigencia de la factura
     */
    private Factura procesarFacturacion(List<DetalleFactura> detallesFacturas, Periodicidad periodicidad) {
        validarDetallesNoVacios(detallesFacturas);

        // Crear la factura con todos sus datos
        Factura factura = construirFactura(detallesFacturas, periodicidad);

        // Persistir factura y detalles (cascade automático)
        facturacionRepository.save((Factura) factura);

        // Actualizar estados de servicios a FACTURADO
        actualizarEstadoServiciosAFacturado(detallesFacturas);

        // Retornamos la Factura
        return factura;
    }

    /**
     * Construye una factura a partir de los detalles, generando datos históricos
     * de cliente y servicios.
     */
    private Factura construirFactura(List<DetalleFactura> detallesFacturas, Periodicidad periodicidad) {
        Factura factura = new Factura();

        // Establecer bidireccionalidad: cada detalle debe conocer su factura
        detallesFacturas.forEach(detalle -> detalle.setFactura(factura));

        factura.setDetallesFacturas(detallesFacturas);
        factura.setMontoTotal(calcularMontoTotalFactura(detallesFacturas));
        factura.setFechaEmision(LocalDate.now());
        factura.setPeriodicidad(periodicidad);
        factura.setFechaVencimiento(factura.getFechaEmision().plusDays(periodicidad.getDias()));
        factura.setTipoComprobante(determinarComprobante(detallesFacturas.get(0)));

        // Datos históricos del cliente y servicios
        factura.setDatosClienteFactura(extraerDatosCliente(detallesFacturas.get(0)));
        factura.setDatosServicioFactura(extraerDatosServicios(detallesFacturas));

        return factura;
    }

    /**
     * Extrae los datos históricos del cliente de un detalle de factura.
     */
    private DatosClienteFactura extraerDatosCliente(DetalleFactura detalleFactura) {
        if (detalleFactura == null) {
            throw new IllegalArgumentException("detalleFactura no puede ser nulo");
        }

        ServicioDeLaCuenta servicioDeLaCuenta = detalleFactura.getServicioDeLaCuenta();
        if (servicioDeLaCuenta == null) {
            throw new IllegalStateException("ServicioDeLaCuenta no encontrado en el detalle");
        }

        Cuenta cuenta = servicioDeLaCuenta.getCuenta();
        if (cuenta == null) {
            throw new IllegalStateException("Cuenta no encontrada en ServicioDeLaCuenta");
        }

        DatosClienteFactura datosCliente = new DatosClienteFactura();
        datosCliente.setIdCuenta(cuenta.getIdCuenta());
        datosCliente.setDomicilioFiscal(cuenta.getDomicilioFiscal());
        datosCliente.setCondicionFrenteIVA(cuenta.getCondicionFrenteIVA());
        datosCliente.setCuit(cuenta.getCuit());
        datosCliente.setRazonSocial(cuenta.getRazonSocial());

        return datosCliente;
    }

    /**
     * Extrae los datos históricos de los servicios de una lista de detalles.
     */
    private List<DatosServicioFactura> extraerDatosServicios(List<DetalleFactura> detallesFactura) {
        if (detallesFactura == null || detallesFactura.isEmpty()) {
            throw new IllegalArgumentException("detallesFactura no puede ser nulo o estar vacío");
        }

        return detallesFactura.stream()
                .peek(detalle -> {
                    if (detalle.getServicioDeLaCuenta() == null) {
                        throw new IllegalStateException("ServicioDeLaCuenta no encontrado en el detalle");
                    }
                })
                .map(detalle -> {

                    Servicio servicio = detalle.getServicioDeLaCuenta().getServicio();
                    if (servicio == null) {
                        throw new IllegalStateException("Servicio no encontrado en ServicioDeLaCuenta");
                    }

                    DatosServicioFactura datosServicio = new DatosServicioFactura();
                    datosServicio.setNombreServicio(servicio.getNombreServicio());
                    datosServicio.setDescripcionServicio(servicio.getDescripcionServicio());
                    datosServicio.setPrecioActual(servicio.getMontoServicio());

                    return datosServicio;
                })
                .collect(Collectors.toList());
    }

    /**
     * Actualiza el estado de todos los servicios facturados a FACTURADO
     * y persiste los cambios en BD.
     */
    private void actualizarEstadoServiciosAFacturado(List<DetalleFactura> detallesFacturas) {
        List<ServicioDeLaCuenta> serviciosAActualizar = detallesFacturas.stream()
                .map(DetalleFactura::getServicioDeLaCuenta)
                .peek(sdc -> sdc.setEstadoServicio(EstadoServicio.FACTURADO))
                .collect(Collectors.toList());

        servicioDeLaCuentaRepository.saveAll((Iterable<ServicioDeLaCuenta>) serviciosAActualizar);
    }

    /**
     * Crea detalles de factura a partir de servicios seleccionados (facturación
     * individual).
     */
    private List<DetalleFactura> crearDetallesFacturaDesdeSeleccion(List<ServicioSeleccionadoDto> listaServicios) {
        return listaServicios.stream()
                .map(this::crearDetalleFacturaDesdeSeleccion)
                .collect(Collectors.toList());
    }

    /**
     * Crea detalles de factura a partir de servicios de cuenta (facturación
     * masiva).
     * Se asume cantidad = 1.
     * Filtra solo los servicios en estado PENDIENTE.
     */
    private List<DetalleFactura> crearDetallesFacturaDesdeServicios(List<ServicioDeLaCuenta> servicios) {
        return servicios.stream()
                .filter(servicio -> servicio.getEstadoServicio() == EstadoServicio.PENDIENTE)
                .map(this::crearDetalleFacturaConCantidadUnitaria)
                .collect(Collectors.toList());
    }

    /**
     * Valida que la lista de servicios seleccionados no sea nula o vacía.
     */
    private void validarListaServicios(List<ServicioSeleccionadoDto> listaServicios) {
        if (listaServicios == null || listaServicios.isEmpty()) {
            throw new IllegalArgumentException("La lista de servicios seleccionados no puede ser nula o estar vacía");
        }
    }

    /**
     * Valida que la lista de detalles no sea nula o vacía.
     */
    private void validarDetallesNoVacios(List<DetalleFactura> detalles) {
        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("La lista de detalles no puede ser nula o estar vacía");
        }
    }

    /**
     * Crear un detalle de factura a partir del servicio seleccionado (EN MEMORIA).
     * Este método construye el objeto pero NO lo persiste en BD aún.
     * Se utiliza en facturación individual donde viene una cantidad específica del
     * usuario.
     * 
     * @param servicioSeleccionado DTO con id y cantidad del servicio
     * @return DetalleFactura creado en memoria (sin persistir)
     */
    private DetalleFactura crearDetalleFacturaDesdeSeleccion(ServicioSeleccionadoDto servicioSeleccionado) {
        if (servicioSeleccionado == null) {
            throw new IllegalArgumentException("servicioSeleccionado no puede ser nulo");
        }

        Integer cantidad = servicioSeleccionado.getCantidad();
        if (cantidad == null || cantidad < 1) {
            throw new IllegalArgumentException("cantidad inválida: debe ser >= 1");
        }

        ServicioDeLaCuenta servicioDeLaCuenta = obtenerServicioDeLaCuenta(servicioSeleccionado);
        if (servicioDeLaCuenta == null) {
            throw new IllegalStateException("Servicio de la cuenta no encontrado para el DTO");
        }

        return construirDetalleFactura(servicioDeLaCuenta, cantidad);
    }

    /**
     * Crear un detalle de factura a partir de un ServicioDeLaCuenta (EN MEMORIA).
     * Se utiliza en facturación masiva donde se asume cantidad = 1.
     * El servicio se factura una sola vez por período.
     * 
     * NOTA: El llamador debe garantizar que el servicio está en estado PENDIENTE.
     * 
     * @param servicio ServicioDeLaCuenta a facturar con cantidad = 1
     * @return DetalleFactura creado en memoria (sin persistir)
     */
    private DetalleFactura crearDetalleFacturaConCantidadUnitaria(ServicioDeLaCuenta servicio) {
        if (servicio == null) {
            throw new IllegalArgumentException("servicio no puede ser nulo");
        }

        return construirDetalleFactura(servicio, 1);
    }

    /**
     * Método auxiliar que construye un DetalleFactura con los cálculos necesarios.
     * Encapsula la lógica de validación y cálculo de IVA.
     */
    private DetalleFactura construirDetalleFactura(ServicioDeLaCuenta servicioDeLaCuenta, int cantidad) {
        if (servicioDeLaCuenta == null) {
            throw new IllegalArgumentException("servicioDeLaCuenta no puede ser nulo");
        }

        Double precioUnitario = servicioDeLaCuenta.getServicio().getMontoServicio();
        if (precioUnitario == null) {
            throw new IllegalStateException("No hay precio definido para el servicio");
        }

        Double alicuota = servicioDeLaCuenta.getServicio().getAlicuota();
        if (alicuota == null) {
            throw new IllegalStateException("No hay alícuota definida para el servicio");
        }

        Double subtotal = precioUnitario * cantidad;
        Double ivaCalculado = subtotal * alicuota;

        DetalleFactura detalleFactura = new DetalleFactura();
        detalleFactura.setCantidad(cantidad);
        detalleFactura.setPrecioUnitario(precioUnitario);
        detalleFactura.setSubtotal(subtotal);
        detalleFactura.setIvaCalculado(ivaCalculado);
        detalleFactura.setServicioDeLaCuenta(servicioDeLaCuenta);

        return detalleFactura;
    }

    /**
     * Calcula el monto total de la factura (subtotal + IVA)
     * 
     * @param detalles Lista de detalles de la factura
     * @return Monto total incluyendo subtotales e IVA
     * @throws IllegalArgumentException si la lista es nula o está vacía
     */
    private Double calcularMontoTotalFactura(List<DetalleFactura> detalles) {
        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("La lista de detalles no puede ser nula o estar vacía");
        }

        return detalles.stream()
                .mapToDouble(detalle -> detalle.getSubtotal() + detalle.getIvaCalculado())
                .sum();
    }

    /**
     * Metodo el cual pasandole un detalleDeFactura, accede a los datos de la
     * cuenta, para
     * determinar el tipo de la factura (ABCME)
     */

    private TipoComprobante determinarComprobante(DetalleFactura detalleFactura) {
        // Validaciones básicas
        if (detalleFactura == null) {
            throw new IllegalArgumentException("detalleFactura no puede ser nulo");
        }

        ServicioDeLaCuenta sdc = detalleFactura.getServicioDeLaCuenta();
        if (sdc == null || sdc.getCuenta() == null) {
            throw new IllegalStateException("ServicioDeLaCuenta o Cuenta asociada no encontrada en el detalle");
        }

        CondicionFrenteIVA condicion = sdc.getCuenta().getCondicionFrenteIVA();

        // Mapeo por defecto (estas reglas son asumidas; revisar legislación /
        // requisitos del negocio
        // si hay que mapear de forma distinta)
        switch (condicion) {
            case RESPONSABLE_INSCRIPTO:
                // Si el cliente también es Responsable Inscripto → corresponde emitir Factura A
                // (con IVA discriminado)
                return TipoComprobante.A;

            case MONOTRIBUTISTA:
                // Si el cliente es Monotributista → el emisor (R.I.) debe emitir Factura B (IVA
                // incluido)
                return TipoComprobante.B;

            case EXENTO:
                // Si el cliente es Exento → también recibe Factura B (ya que el emisor es R.I.)
                return TipoComprobante.B;

            case CONSUMIDOR_FINAL:
            case NO_RESPONSABLE:
            default:
                // Consumidor Final o No Responsable → Factura B (IVA incluido)
                return TipoComprobante.B;
        }

    }

    /**
     * Obtiene una FacturacionMasiva por su ID convertida a DTO
     * 
     * @param idFacturacionMasiva ID de la facturación masiva
     * @return FacturacionMasivaDto con todas sus facturas
     */
    public FacturacionMasivaDto obtenerFacturacionMasivaPorId(Integer idFacturacionMasiva) {
        return facturacionMasivaRepository.findById(idFacturacionMasiva)
                .map(Mapper::toDto)
                .orElse(null);
    }

}
