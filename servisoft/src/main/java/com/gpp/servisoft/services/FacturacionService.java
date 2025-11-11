package com.gpp.servisoft.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gpp.servisoft.model.dto.ServicioSeleccionadoDto;
import com.gpp.servisoft.model.entities.Cuenta;
import com.gpp.servisoft.model.entities.DatosClienteFactura;
import com.gpp.servisoft.model.entities.DatosServicioFactura;
import com.gpp.servisoft.model.entities.DetalleFactura;
import com.gpp.servisoft.model.entities.Factura;
import com.gpp.servisoft.model.entities.Servicio;
import com.gpp.servisoft.model.entities.ServicioDeLaCuenta;
import com.gpp.servisoft.model.enums.CondicionFrenteIVA;
import com.gpp.servisoft.model.enums.Estado;
import com.gpp.servisoft.model.enums.TipoComprobante;
import com.gpp.servisoft.repository.CuentaRepository;
import com.gpp.servisoft.repository.DetalleFacturaRepository;
import com.gpp.servisoft.repository.FacturacionRepository;
import com.gpp.servisoft.repository.ServicioDeLaCuentaRepository;

@Service
public class FacturacionService {


    // Utilizamos Autowired para poder inyectar la dependencia del repositorio
    // A la clase, service

    @Autowired
    public FacturacionRepository facturacionRepository;
    
    @Autowired
    public ServicioDeLaCuentaRepository servicioDeLaCuentaRepository;

    @Autowired
    public DetalleFacturaRepository detalleFacturaRepository;

    @Autowired
    public CuentaRepository cuentaRepository;

    /**
     * Implementacion Basica de la consulta de todos los registros en la BD
     * @return la lista completa de Facturas existentes de la BD
     */
    public List<Factura> obtenerFacturas()
    {
        return facturacionRepository.findAll();
    }


    /**
     * Obtener un Servicio de la Cuenta específico
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
                    "Servicio con ID " + servicioSeleccionado.getIdServicio() + " no encontrado"
                ));
    }


    /**
     * Metodo utilizado para realizar la facturacion Individual, se le provee una lista de los servicios
     * Seleccionados por el controller, y Crea y persiste las respectivas clases
     */
    public void facturacionIndividual(List<ServicioSeleccionadoDto> listaServicios, int diasPeriodo)
    {
        // 1. Creamos los detalles de los servicios, utilizando la lista de servicios
        List<DetalleFactura> detallesFacturas = altaDetallesFactura(listaServicios);

        facturar(detallesFacturas, diasPeriodo);
    }

    /**
     * Metodo Utilizado para realziar una Facturacion Masiva, la misma no recibe como parametro una Lista 
     * De los servicios, en su lugar elige todos los Servicios de todas las cuentas activas y las factura
     */
    public void facturacionMasiva(int diasPeriodo)
    {
        // Obtenemos todas las cuentas
        List<Cuenta> cuentas = cuentaRepository.findByEstado(Estado.ACTIVO);
        // Recorro las cuentas generando Facturas para cada servicio Asociada a dicha cuenta
        for(Cuenta cuenta : cuentas)
        {
            // Creacion Masiva de Detalles de Cuentas
            facturar(altaDetallesFacturaDesdeServicios(cuenta.getServiciosDeLaCuenta()), diasPeriodo);
        }
    }

    /**
     * Metodo Utilizado para realizar una Facturacion tanto individual, como masiva
     * @param listaServicios Representa los Servicios seleccionados por el cliente
     */
    @Transactional
    private void facturar(List<DetalleFactura> detallesFacturas, int diasPeriodo)
    {
        if (detallesFacturas == null || detallesFacturas.isEmpty()) {
            throw new IllegalArgumentException("La lista de detalles no puede ser nula o estar vacía");
        }

        // 2. Creamos las instancias de Datos del Cliente (historicos) - Extraemos del primer detalle
        DatosClienteFactura datosCliente = altaDatosClienteDesdeDetalle(detallesFacturas.get(0));

        // 3. Creamos las instancias de Datos de Servicios (historicos) - Extraemos de cada detalle
        List<DatosServicioFactura> datosServicios = altaDatosServiciosDesdeDetalles(detallesFacturas);

        // 4. Crear la Factura con todos estos datos
        Factura factura = new Factura();

        // 4.1 Setear Datos especificos de la factura
        factura.setDetallesFacturas(detallesFacturas);
        factura.setMontoTotal(calcularMontoTotalFactura(detallesFacturas));
        factura.setFechaEmision(LocalDate.now());
        factura.setFechaVencimiento(factura.getFechaEmision().plusDays(diasPeriodo));
        factura.setTipoComprobante(determinarComprobante(detallesFacturas.get(0)));
        // 4.2 Setear Datos especificos del CLiente y del Servicio
        factura.setDatosClienteFactura(datosCliente);
        factura.setDatosServicioFactura(datosServicios);        
    // 5. Persistimos la instancia de Factura
    facturacionRepository.save(factura);
    // 6. Asociamos cada Detalle con la factura recien creada (asignación y batch save)
    asociarFactura(factura, detallesFacturas);
        

    }

    /**
     * Crea detalles de factura a partir de servicios seleccionados por el usuario (facturación individual).
     * La cantidad viene especificada en el DTO ServicioSeleccionadoDto.
     * @param listaServicios Lista de DTOs con servicios y cantidades seleccionadas
     * @return Lista de DetalleFactura creados
     */
    private List<DetalleFactura> altaDetallesFactura(List<ServicioSeleccionadoDto> listaServicios) {
        // Por cada servicio seleccionado tengo que crear un detalle de facturas, y a 
        // cada detalle creado, guardarlo en una lista, para asi asociarlo a una factura

        // Atributos de la clase
        List<DetalleFactura> detallesFacturas = new ArrayList<>();

        // 1. Facturarlo, Por cada ServicioSeleccionado en la Lista de Servicios,
        // Facturarlo y Guardarlo en una lista

        for(ServicioSeleccionadoDto servicioSeleccionadoDto : listaServicios)
        {
            detallesFacturas.add(altaDetalleFactura(servicioSeleccionadoDto));
        }

        // 2. Retorno la lista de Detalles de Factura.homeController

        return detallesFacturas;
    }

    /**
     * Crear un detalle de factura a partir del servicio seleccionado
     * @param servicioSeleccionado DTO con id y cantidad del servicio
     * @return DetalleFactura creado
     */
    private DetalleFactura altaDetalleFactura(ServicioSeleccionadoDto servicioSeleccionado) {
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

        Double precioUnitario = servicioDeLaCuenta.getServicio().getMontoServicio();
        if (precioUnitario == null) {
            throw new IllegalStateException("No hay precio definido para el servicio");
        }

        Double alicuota = servicioDeLaCuenta.getServicio().getAlicuota();
        if (alicuota == null) {
            throw new IllegalStateException("No hay alícuota definida para el servicio");
        }

        // Cálculos simples con Double
        Double subtotal = precioUnitario * cantidad;
        Double ivaCalculado = subtotal * alicuota;

        DetalleFactura unDetalleFactura = new DetalleFactura();
        unDetalleFactura.setCantidad(cantidad);
        unDetalleFactura.setPrecioUnitario(precioUnitario);
        unDetalleFactura.setSubtotal(subtotal);
        unDetalleFactura.setIvaCalculado(ivaCalculado);
        unDetalleFactura.setServicioDeLaCuenta(servicioDeLaCuenta);

        // Creacion de la instancia en la BD

        detalleFacturaRepository.save(unDetalleFactura);

        // TODO: Logica para Asociar un Detalle de Factura, con un ServicioDelCliente

        // ServicioDelClienteServices() -> AgregarDetalle() 

        return unDetalleFactura;
    }

    //--------------------SOBRECARGA CON DETALLES DE FACTURA----------------------------

    /**
     * Crea detalles de factura a partir de servicios de cuenta (facturación masiva).
     * Se asume cantidad = 1 porque cada servicio de una cuenta se factura una sola vez
     * por período en el proceso de facturación masiva.
     * @param servicios Lista de ServicioDeLaCuenta de una o múltiples cuentas activas
     * @return Lista de DetalleFactura creados con cantidad=1 cada uno
     */
    private List<DetalleFactura> altaDetallesFacturaDesdeServicios(List<ServicioDeLaCuenta> servicios) {
        // Por cada servicio seleccionado tengo que crear un detalle de facturas, y a 
        // cada detalle creado, guardarlo en una lista, para asi asociarlo a una factura

        // Atributos de la clase
        List<DetalleFactura> detallesFacturas = new ArrayList<>();

        // 1. Facturarlo, Por cada ServicioSeleccionado en la Lista de Servicios,
        // Facturarlo y Guardarlo en una lista

        for(ServicioDeLaCuenta servicioDeLaCuenta : servicios)
        {
            detallesFacturas.add(altaDetalleFactura(servicioDeLaCuenta));
        }

        // 2. Retorno la lista de Detalles de Factura

        return detallesFacturas;
    }

    /**
     * Sobrecarga para crear un detalle a partir de un ServicioDeLaCuenta.
     * Se utiliza en facturación masiva donde no viene una cantidad específica del usuario,
     * por lo que se asume cantidad = 1 (el servicio se factura una sola vez por ciclo).
     * @param servicio ServicioDeLaCuenta a facturar con cantidad = 1
     * @return DetalleFactura creado
     */
    private DetalleFactura altaDetalleFactura(ServicioDeLaCuenta servicio) {
        if (servicio == null) {
            throw new IllegalArgumentException("servicio no puede ser nulo");
        }

        Double precioUnitario = servicio.getServicio().getMontoServicio();
        if (precioUnitario == null) {
            throw new IllegalStateException("No hay precio definido para el servicio");
        }

        Double alicuota = servicio.getServicio().getAlicuota();
        if (alicuota == null) {
            throw new IllegalStateException("No hay alícuota definida para el servicio");
        }

        // Cantidad fija = 1 porque en facturación masiva cada servicio se factura una sola vez por período
        int cantidadFija = 1;
        Double subtotal = precioUnitario * cantidadFija;
        Double ivaCalculado = subtotal * alicuota;

        DetalleFactura unDetalleFactura = new DetalleFactura();
        unDetalleFactura.setCantidad(cantidadFija);
        unDetalleFactura.setPrecioUnitario(precioUnitario);
        unDetalleFactura.setSubtotal(subtotal);
        unDetalleFactura.setIvaCalculado(ivaCalculado);
        unDetalleFactura.setServicioDeLaCuenta(servicio);

        // Creacion de la instancia en la BD
        detalleFacturaRepository.save(unDetalleFactura);

        return unDetalleFactura;
    }

    /**
     * Metodo Utilizado para extraer y crear los datos historicos del cliente desde un detalle de factura
     * @param detalleFactura Detalle de factura que contiene la relacion con ServicioDeLaCuenta y Cuenta
     * @return DatosClienteFactura con los datos historicos del cliente
     */
    private DatosClienteFactura altaDatosClienteDesdeDetalle(DetalleFactura detalleFactura) {
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
        datosCliente.setDomicilioFiscal(cuenta.getDomicilioFiscal());
        datosCliente.setCondicionFrenteIVA(cuenta.getCondicionFrenteIVA());
        datosCliente.setCuit(cuenta.getCuit());
        datosCliente.setRazonSocial(cuenta.getRazonSocial());

        return datosCliente;
    }

    /**
     * Metodo Utilizado para extraer y crear los datos historicos de los servicios desde los detalles de factura
     * @param detallesFactura Lista de detalles de factura que contienen los servicios
     * @return Lista de DatosServicioFactura con los datos historicos de los servicios
     */
    private List<DatosServicioFactura> altaDatosServiciosDesdeDetalles(List<DetalleFactura> detallesFactura) {
        if (detallesFactura == null || detallesFactura.isEmpty()) {
            throw new IllegalArgumentException("detallesFactura no puede ser nulo o estar vacío");
        }

        List<DatosServicioFactura> datosServicios = new ArrayList<>();

        for (DetalleFactura detalle : detallesFactura) {
            if (detalle.getServicioDeLaCuenta() == null) {
                throw new IllegalStateException("ServicioDeLaCuenta no encontrado en el detalle");
            }

            Servicio servicio = detalle.getServicioDeLaCuenta().getServicio();
            if (servicio == null) {
                throw new IllegalStateException("Servicio no encontrado en ServicioDeLaCuenta");
            }

            DatosServicioFactura datosServicio = new DatosServicioFactura();
            datosServicio.setNombreServicio(servicio.getNombreServicio());
            datosServicio.setDescripcionServicio(servicio.getDescripcionServicio());
            datosServicio.setPrecioActual(servicio.getMontoServicio());

            datosServicios.add(datosServicio);
        }

        return datosServicios;
    }

    /**
     * Calcula el monto total de la factura (subtotal + IVA)
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
     * Metodo el cual pasandole un detalleDeFactura, accede a los datos de la cuenta, para
     * determinar el tipo de la factura (ABCME)
     */

    private TipoComprobante determinarComprobante(DetalleFactura detalleFactura){
        // Validaciones básicas
        if (detalleFactura == null) {
            throw new IllegalArgumentException("detalleFactura no puede ser nulo");
        }

        ServicioDeLaCuenta sdc = detalleFactura.getServicioDeLaCuenta();
        if (sdc == null || sdc.getCuenta() == null) {
            throw new IllegalStateException("ServicioDeLaCuenta o Cuenta asociada no encontrada en el detalle");
        }

        CondicionFrenteIVA condicion = sdc.getCuenta().getCondicionFrenteIVA();

        // Mapeo por defecto (estas reglas son asumidas; revisar legislación / requisitos del negocio
        // si hay que mapear de forma distinta)
        switch (condicion) {
            case RESPONSABLE_INSCRIPTO:
                // Cliente responsable inscripto -> Factura A (desglosa IVA)
                return TipoComprobante.A;
            case MONOTRIBUTISTA:
                // Monotributista suele recibir comprobantes tipo C en muchos esquemas o B en otros.
                // Elegimos C por defecto; si se requiere otro comportamiento, mover a configuración.
                return TipoComprobante.C;
            case EXENTO:
                // Exentos habitualmente no tienen IVA; usar comprobante E (existente en enum)
                return TipoComprobante.E;
            case CONSUMIDOR_FINAL:
            case NO_RESPONSABLE:
            default:
                // Consumidor final o casos no contemplados -> Factura B por defecto
                return TipoComprobante.B;
        }
    }

    /**
     * Metodo destinado  a asociar cada Detalle con la Factura recien creada
     */
    private void asociarFactura(Factura factura, List<DetalleFactura> detalles)
    {
        // Asignamos la factura a todos los detalles en memoria
        for (DetalleFactura detalle : detalles) {
            detalle.setFactura(factura);
        }

        // Guardamos en batch para reducir round-trips
        detalleFacturaRepository.saveAll(detalles);
    }
}
