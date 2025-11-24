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

import com.gpp.servisoft.domain.facturacion.factory.ReglaFacturaFactory;
import com.gpp.servisoft.domain.facturacion.strategy.ReglaFacturaStrategy;
import com.gpp.servisoft.exceptions.ExcepcionNegocio;
import com.gpp.servisoft.mapper.Mapper;
import com.gpp.servisoft.model.dto.AnulacionDto;
import com.gpp.servisoft.model.dto.FacturacionDTO;
import com.gpp.servisoft.model.dto.FacturacionMasivaDto;
import com.gpp.servisoft.model.dto.ServicioSeleccionadoDto;
import com.gpp.servisoft.model.entities.Cuenta;
import com.gpp.servisoft.model.entities.DatosClienteFactura;
import com.gpp.servisoft.model.entities.DatosServicioFactura;
import com.gpp.servisoft.model.entities.DetalleFactura;
import com.gpp.servisoft.model.entities.Factura;
import com.gpp.servisoft.model.entities.FacturacionMasiva;
import com.gpp.servisoft.model.entities.NotaDeCredito;
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
import com.gpp.servisoft.repository.NotaDeCreditoRepository;
import com.gpp.servisoft.repository.ServicioDeLaCuentaRepository;

@Service
@SuppressWarnings("null")
public class FacturacionService {

    private static final CondicionFrenteIVA CONDICION_EMPRESA = CondicionFrenteIVA.RESPONSABLE_INSCRIPTO;

    @Autowired
    public FacturacionRepository facturacionRepository;

    @Autowired
    public ServicioDeLaCuentaRepository servicioDeLaCuentaRepository;

    @Autowired
    public CuentaRepository cuentaRepository;

    @Autowired
    public FacturacionMasivaRepository facturacionMasivaRepository;

    @Autowired
    public NotaDeCreditoRepository notaDeCreditoRepository;

    /**
     * Obtiene una lista paginada de facturas, permitiendo filtrar por el ID de la
     * cuenta
     * y/o por una búsqueda parcial del número de comprobante.
     *
     * @param idCuenta    El mismo tiene que ser Integer, ya que se acepta que el
     *                    valor
     *                    puede ser nulo (Todas las Cuentas)
     * @param comprobante Representa el Comprobante "0001-000000004" de una factura,
     *                    se utiliza el LIKE para filtrar
     * @param pageable    Objeto de pregunta generado por Spring
     * @return Un objeto Page de FacturacionDTO con los datos de paginación
     *         preservados.
     */
    public Page<FacturacionDTO> obtenerFacturas(Integer idCuenta, String comprobante, Pageable pageable) {

        // 1. Llama al repositorio y obtienes la página de ENTIDADES (Factura)
        Page<Factura> paginaDeEntidades = facturacionRepository.findFacturasByFilters(idCuenta, comprobante, pageable);

        // 2. Mapea la página de Entidades a una página de DTOs
        // Esto aplica "Mapper::toDto" a cada ítem Y MANTIENE la paginación.
        return paginaDeEntidades.map(Mapper::toDto);
    }

    /**
     * obtiene una lista Paginada de Facturas, en el estado de Pendiente Y ParcialmentePagado
     * Utilizada para mostrar en la lista de gestion de Pagos
     */
    public Page<FacturacionDTO> obtenerFacturasPendientesyParciales(Integer idCuenta, String comprobante, Pageable pageable) {
        Page<Factura> paginaDeFacturasPendientes = facturacionRepository.findFacturasPendientesByFilters(idCuenta,
                comprobante, pageable);
        return paginaDeFacturasPendientes.map(Mapper::toDto);
    }

    /**
     * Obtnego las facturaciones Masivas realizadas
     * @return Lista de Facturaciones Masivas
     */
    public Page<FacturacionMasivaDto> obtenerFacturacionesMasivas(Pageable pageable) {
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
     * @throws IllegalArgumentException si no se encuentra una factura con el ID
     *                                  proporcionado
     */
    public FacturacionDTO obtenerFacturaPorId(Integer idFactura) {
        Factura factura = obtenerFactura(idFactura);
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
            throw new ExcepcionNegocio("El servicio seleccionado no puede ser nulo");
        }

        // Obtenemos el servicioDeLaCuenta usando el ID correcto
        return servicioDeLaCuentaRepository.findById(servicioSeleccionado.getIdServicio())
                .orElseThrow(() -> new ExcepcionNegocio(
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
     * servicios pendientes de todas las cuentas activas y los factura en un único proceso.
     * 
     * Valida que existan servicios PENDIENTES en las cuentas activas.
     * Si hay cuentas activas pero ninguna tiene servicios pendientes, lanza ExcepcionNegocio.
     * 
     * @param periodicidad Período de vigencia de las facturas
     * @throws ExcepcionNegocio si la periodicidad es nula, si no hay cuentas activas o no hay servicios pendientes
     */
    @Transactional
    public void facturacionMasiva(Periodicidad periodicidad) {
        
        // Validar que la periodicidad no sea nula
        if (periodicidad == null) {
            throw new ExcepcionNegocio("La periodicidad es requerida para realizar la facturación masiva");
        }

        // Creamos una instancia Vacia de la Facturacion Masiva
        FacturacionMasiva facturacionMasiva = new FacturacionMasiva();

        // Creamos una lista en la cual vamos a agregar todas las facturas generadas en
        // Este proceso
        List<Factura> facturas = new ArrayList<>();

        // Obtengo todas las cuentas que esten en el estado de activo.
        List<Cuenta> cuentasActivas = cuentaRepository.findByEstado(Estado.ACTIVO);

        if (cuentasActivas.isEmpty()) {
            throw new ExcepcionNegocio("No hay cuentas activas para facturar");
        }

        // Procesar cada cuenta y recolectar detalles de facturas
        for (Cuenta cuenta : cuentasActivas) {
            // Si hay cuentas activas y tienen servicios
            if (cuenta.getServiciosDeLaCuenta() != null && !cuenta.getServiciosDeLaCuenta().isEmpty()) {
                // Creamos la lista de detalles de cada cuenta (filtra solo PENDIENTES)
                List<DetalleFactura> detalles = crearDetallesFacturaDesdeServicios(cuenta.getServiciosDeLaCuenta());
                if (!detalles.isEmpty()) {
                    // Si se generan detalles para una determinada cuenta, Procedo 
                    // A realizar la Facturación, utilizando la lista de detalles y el período
                    facturas.add(procesarFacturacion(detalles, periodicidad));
                }
            }
        }

        // Validar que se hayan generado facturas (es decir, que haya servicios PENDIENTES)
        if (facturas.isEmpty()) {
            throw new ExcepcionNegocio(
                    "No se pudo realizar la facturación masiva: no hay servicios pendientes de facturación en las cuentas activas"
            );
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
        // Si los detalles son vacios o null -> Excepcion
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
        // Creo  una nueva instancia de Factura
        Factura factura = new Factura();

        // Establecer bidireccionalidad: cada detalle debe conocer su factura
        detallesFacturas.forEach(detalle -> detalle.setFactura(factura));

        factura.setDetallesFacturas(detallesFacturas);
        factura.setMontoTotal(factura.calcularMontoTotalFactura());
        factura.setFechaEmision(LocalDate.now());
        factura.setPeriodicidad(periodicidad);
        factura.setFechaVencimiento(factura.getFechaEmision().plusDays(periodicidad.getDias()));

        // Datos históricos del cliente y servicios
        factura.setDatosClienteFactura(extraerDatosCliente(detallesFacturas.get(0)));
        factura.setDatosServicioFactura(extraerDatosServicios(detallesFacturas));

        // Determinamos el Tipo del Comprobante
        factura.setTipoComprobante(determinarTipoComprobantePorCondicion(factura.getDatosClienteFactura().getCondicionFrenteIVA()));
        
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
            throw new ExcepcionNegocio("ServicioDeLaCuenta no encontrado en el detalle");
        }

        Cuenta cuenta = servicioDeLaCuenta.getCuenta();
        if (cuenta == null) {
            throw new ExcepcionNegocio("Cuenta no encontrada en ServicioDeLaCuenta");
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
            throw new ExcepcionNegocio("detallesFactura no puede ser nulo o estar vacío");
        }

        return detallesFactura.stream()
                .peek(detalle -> {
                    if (detalle.getServicioDeLaCuenta() == null) {
                        throw new ExcepcionNegocio("ServicioDeLaCuenta no encontrado en el detalle");
                    }
                })
                .map(detalle -> {

                    Servicio servicio = detalle.getServicioDeLaCuenta().getServicio();
                    if (servicio == null) {
                        throw new ExcepcionNegocio("Servicio no encontrado en ServicioDeLaCuenta");
                    }

                    DatosServicioFactura datosServicio = new DatosServicioFactura();
                    datosServicio.setNombreServicio(servicio.getNombreServicio());
                    datosServicio.setDescripcionServicio(servicio.getDescripcionServicio());
                    datosServicio.setPrecioActual(servicio.getMontoServicio());
                    datosServicio.setAlicuotaActual(servicio.getAlicuota());

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
     * Utiliza la cantidad de preferencia definida en cada ServicioDeLaCuenta.
     * Filtra solo los servicios en estado PENDIENTE.
     */
    private List<DetalleFactura> crearDetallesFacturaDesdeServicios(List<ServicioDeLaCuenta> servicios) {
        return servicios.stream()
                .filter(servicio -> servicio.getEstadoServicio() == EstadoServicio.PENDIENTE)
                .map(servicio -> construirDetalleFactura(servicio, servicio.getCantidadDePreferencia()))
                .collect(Collectors.toList());
    }

    

    /**
     * Valida que la lista de servicios seleccionados no sea nula o vacía.
     */
    private void validarListaServicios(List<ServicioSeleccionadoDto> listaServicios) {
        if (listaServicios == null || listaServicios.isEmpty()) {
            throw new ExcepcionNegocio("La lista de servicios seleccionados no puede ser nula o estar vacía");
        }
    }

    /**
     * Valida que la lista de detalles no sea nula o vacía.
     */
    private void validarDetallesNoVacios(List<DetalleFactura> detalles) {
        if (detalles == null || detalles.isEmpty()) {
            throw new ExcepcionNegocio("La lista de detalles no puede ser nula o estar vacía");
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
            throw new ExcepcionNegocio("servicioSeleccionado no puede ser nulo");
        }

        Integer cantidad = servicioSeleccionado.getCantidad();
        if (cantidad == null || cantidad < 1) {
            throw new ExcepcionNegocio("cantidad inválida: debe ser >= 1");
        }

        ServicioDeLaCuenta servicioDeLaCuenta = obtenerServicioDeLaCuenta(servicioSeleccionado);
        if (servicioDeLaCuenta == null) {
            throw new ExcepcionNegocio("Servicio de la cuenta no encontrado para el DTO");
        }

        return construirDetalleFactura(servicioDeLaCuenta, cantidad);
    }


    /**
     * Método auxiliar que construye un DetalleFactura con los cálculos necesarios.
     * Encapsula la lógica de validación y cálculo de IVA.
     */
    private DetalleFactura construirDetalleFactura(ServicioDeLaCuenta servicioDeLaCuenta, int cantidad) {
        if (servicioDeLaCuenta == null) {
            throw new ExcepcionNegocio("servicioDeLaCuenta no puede ser nulo");
        }

        // Obtengo el Precio Total del Servicio
        Double precioUnitario = servicioDeLaCuenta.getServicio().getMontoServicio();
        if (precioUnitario == null) {
            throw new ExcepcionNegocio("No hay precio definido para el servicio");
        }

        // Obtengo la alicuota asignada al servicio.
        Double alicuota = servicioDeLaCuenta.getServicio().getAlicuota();
        if (alicuota == null) {
            throw new ExcepcionNegocio("No hay alícuota definida para el servicio");
        }

        // Realizamos calculos para determinar el Subtotal y el Iva calculado
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

    /**
     * Anula una factura creando una nota de crédito asociada.
     * Nota: La factura se considera anulada cuando tiene asociada una
     * NotaDeCredito.
     * 
     * @param idFactura    ID de la factura a anular
     * @param anulacionDto DTO con el motivo de anulación
     * @throws ExcepcionNegocio si el ID es inválido, falta motivo, factura no
     *                          existe o ya está anulada
     */
    /**
     * Anula una factura creando una nota de crédito asociada y revirtiendo 
     * los servicios a estado PENDIENTE para permitir refacturación.
     * 
     * Operaciones realizadas:
     * 1. Valida DTO y motivo
     * 2. Obtiene y valida la factura
     * 3. Crea nota de crédito
     * 4. Revierte estado de servicios a PENDIENTE
     * 5. Persiste cambios de forma transaccional
     * 
     * @param anulacionDto DTO con idFactura y motivo de anulación
     * @throws ExcepcionNegocio si datos son inválidos, factura no existe o ya está anulada
     */
    
    @Transactional 
    public void anularFactura(AnulacionDto anulacionDto) {

        // Validar que el DTO y motivo sean válidos
        validarAnulacionDto(anulacionDto);

        // Obtener la factura
        Factura factura = obtenerFactura(anulacionDto.getIdFactura());

        // Verificar que la factura no esté ya anulada
        if (factura.getNotaDeCredito() != null) {
            throw new ExcepcionNegocio("No se puede anular una factura que ya está anulada");
        }

        // Crear nota de crédito
        crearNotaDeCredito(factura, anulacionDto.getMotivo());

        // Revertir servicios a PENDIENTE para permitir refacturación
        revertirServiciosAPendiente(factura.getDetallesFacturas());
    }

    /**
     * Valida que el DTO de anulación contenga datos válidos.
     */
    private void validarAnulacionDto(AnulacionDto anulacionDto) {
        if (anulacionDto == null || anulacionDto.getIdFactura() == null) {
            throw new ExcepcionNegocio("El ID de factura es obligatorio para anular");
        }

        if (anulacionDto.getMotivo() == null || anulacionDto.getMotivo().isBlank()) {
            throw new ExcepcionNegocio("El motivo de anulación es obligatorio");
        }
    }

    /**
     * Crea y persiste una nota de crédito asociada a una factura.
     */
    private void crearNotaDeCredito(Factura factura, String motivo) {
        NotaDeCredito notaDeCredito = new NotaDeCredito();
        notaDeCredito.setFactura(factura); // Bidireccionalidad
        notaDeCredito.setFechaEmision(LocalDate.now());
        notaDeCredito.setMonto(factura.getMontoTotal());
        notaDeCredito.setMotivo(motivo);
        notaDeCredito.setTipoComprobante(determinarTipoComprobantePorCondicion(factura.getDatosClienteFactura().getCondicionFrenteIVA()));

        notaDeCreditoRepository.save(notaDeCredito);
    }

    /**
     * Revierte el estado de todos los servicios de una factura anulada a PENDIENTE.
     * Permite que se puedan volver a facturar en el futuro.
     */
    private void revertirServiciosAPendiente(List<DetalleFactura> detallesFacturas) {
        if (detallesFacturas == null || detallesFacturas.isEmpty()) {
            return;
        }

        List<ServicioDeLaCuenta> serviciosARevertir = detallesFacturas.stream()
                .map(DetalleFactura::getServicioDeLaCuenta)
                .peek(sdc -> sdc.setEstadoServicio(EstadoServicio.PENDIENTE))
                .collect(Collectors.toList());

        servicioDeLaCuentaRepository.saveAll((Iterable<ServicioDeLaCuenta>) serviciosARevertir);
    }

    /**
     * Obtiene una factura por su ID.
     * 
     * @param idFactura ID de la factura a obtener
     * @return Factura encontrada
     * @throws ExcepcionNegocio si la factura no existe
     */
    private Factura obtenerFactura(Integer idFactura) {
        return facturacionRepository.findById(idFactura)
                .orElseThrow(() -> new ExcepcionNegocio(
                        "Factura con ID " + idFactura + " no encontrada"));
    }

    /**
     * Obtiene la nota de crédito asociada a una factura anulada.
     * 
     * @param idFactura ID de la factura
     * @return NotaDeCredito encontrada, o null si no existe
     */
    public Object obtenerNotaDeCreditoPorFactura(Integer idFactura) {
        Factura factura = obtenerFactura(idFactura);
        return factura.getNotaDeCredito();
    }


    /**
     * Obtiene el Tipo de Comprobante de la Factura/NotaDeCredito, Dependiendo
     * de la condicion frente al iva del Emisor (Empresa: Por ahora Hardckodeado con R.I)
     * y la condicion del Receptor (CLIENTE)
     * @param condicionReceptor Condicion Frente al iva del Receptor
     * @return El Tipo de COmprobante de la factura/nota (A,B,C,D,E)
     */
    private TipoComprobante determinarTipoComprobantePorCondicion(CondicionFrenteIVA condicionReceptor) {

        ReglaFacturaStrategy regla = ReglaFacturaFactory.getStrategy(CONDICION_EMPRESA);

        // Delego la decisión a la regla seleccionada
        return regla.determinar(CONDICION_EMPRESA, condicionReceptor);
    }
}
