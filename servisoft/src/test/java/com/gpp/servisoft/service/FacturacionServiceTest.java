package com.gpp.servisoft.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.gpp.servisoft.exceptions.ExcepcionNegocio;
import com.gpp.servisoft.model.dto.AnulacionDto;
import com.gpp.servisoft.model.dto.FacturacionDTO;
import com.gpp.servisoft.model.dto.ServicioSeleccionadoDto;
import com.gpp.servisoft.model.entities.Cliente;
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
import com.gpp.servisoft.model.enums.EstadoServicio;
import com.gpp.servisoft.model.enums.Periodicidad;
import com.gpp.servisoft.model.enums.TipoCliente;
import com.gpp.servisoft.repository.CuentaRepository;
import com.gpp.servisoft.repository.FacturacionMasivaRepository;
import com.gpp.servisoft.repository.FacturacionRepository;
import com.gpp.servisoft.repository.NotaDeCreditoRepository;
import com.gpp.servisoft.repository.ServicioDeLaCuentaRepository;

@ExtendWith(MockitoExtension.class)
public class FacturacionServiceTest {

        // Dependencias que necesita "FacturacionService" para poder ser testeado
        // correctamente
        @Mock
        public FacturacionRepository facturacionRepository;

        @Mock
        public ServicioDeLaCuentaRepository servicioDeLaCuentaRepository;

        @Mock
        public CuentaRepository cuentaRepository;

        @Mock
        public FacturacionMasivaRepository facturacionMasivaRepository;

        @Mock
        public NotaDeCreditoRepository notaDeCreditoRepository;

        // inyectamos los Mocks anteriormente definidos al Servicio de Facturacion
        @InjectMocks
        private FacturacionService facturacionService;

        // OBJETOS SIMULADOS ATRIBUTOS

        // * ========== VARIABLES DE PRUEBA =============
        private Cliente CLIENTE;

        // Servicios (Catálogo)
        private Servicio SERVICIO1, SERVICIO2, SERVICIO3, SERVICIO4, SERVICIO5, SERVICIO6;

        // Cuentas
        private Cuenta CUENTA1, CUENTA2, CUENTA3;

        // Relaciones (ServicioDeLaCuenta)
        private ServicioDeLaCuenta SDC1_1, SDC1_2; // Para Cuenta 1
        private ServicioDeLaCuenta SDC2_1, SDC2_2; // Para Cuenta 2
        private ServicioDeLaCuenta SDC3_1, SDC3_2; // Para Cuenta 3

        // Detalles De Facturacion
        private DetalleFactura Detalle1, Detalle2;

        // Facturas
        private Factura factura1;

        @BeforeEach
        void setUp() {
                // 1. Crear Cliente
                CLIENTE = Cliente.builder()
                                .idCliente(1)
                                .nombre("Juan Carlos")
                                .correoElectronico("test@mail.com")
                                .tipoCliente(TipoCliente.FISICO)
                                .build();

                // 2. Crear Servicios (Catálogo)
                SERVICIO1 = crearServicio(1, "Agua", 8000.0, 0.21);
                SERVICIO2 = crearServicio(2, "Luz", 15000.0, 0.27);
                SERVICIO3 = crearServicio(3, "Internet", 22500.0, 0.21);
                SERVICIO4 = crearServicio(4, "Gas", 6800.0, 0.21);
                SERVICIO5 = crearServicio(5, "Mantenimiento", 12000.0, 0.21);
                SERVICIO6 = crearServicio(6, "Alarma", 18900.0, 0.21);

                // 3. Crear Cuentas (Inicialmente vacías de servicios)
                CUENTA1 = crearCuenta(1, "Cuenta Uno", CLIENTE);
                CUENTA2 = crearCuenta(2, "Cuenta Dos", CLIENTE);
                CUENTA3 = crearCuenta(3, "Cuenta Tres", CLIENTE);

                // 4. Crear Relaciones y Asociarlas
                // --- Cuenta 1 (Agua y Luz) ---
                SDC1_1 = crearRelacion(1, CUENTA1, SERVICIO1);
                SDC1_2 = crearRelacion(2, CUENTA1, SERVICIO2);
                CUENTA1.getServiciosDeLaCuenta().add(SDC1_1);
                CUENTA1.getServiciosDeLaCuenta().add(SDC1_2);

                // --- Cuenta 2 (Internet y Gas) ---
                SDC2_1 = crearRelacion(3, CUENTA2, SERVICIO3);
                SDC2_2 = crearRelacion(4, CUENTA2, SERVICIO4);
                CUENTA2.getServiciosDeLaCuenta().add(SDC2_1);
                CUENTA2.getServiciosDeLaCuenta().add(SDC2_2);

                // --- Cuenta 3 (Mantenimiento y Alarma) ---
                SDC3_1 = crearRelacion(5, CUENTA3, SERVICIO5);
                SDC3_2 = crearRelacion(6, CUENTA3, SERVICIO6);
                CUENTA3.getServiciosDeLaCuenta().add(SDC3_1);
                CUENTA3.getServiciosDeLaCuenta().add(SDC3_2);

                // 5. Crear Detalles de Factura
                Detalle1 = crearDetalleFactura(1, SDC1_1);
                Detalle2 = crearDetalleFactura(2, SDC1_2);

                // 6. Crear Factura Completa
                // Monto Total: Agua (8000 + 1680 IVA) + Luz (15000 + 4050 IVA) = 28730
                factura1 = crearFacturaCompleta(1, CUENTA1);
        }

        // --- Métodos Helper para limpiar el código ---

        private Servicio crearServicio(Integer id, String nombre, Double monto, Double alicuota) {
                return Servicio.builder()
                                .idServicio(id)
                                .nombreServicio(nombre)
                                .descripcionServicio("Descripción de " + nombre)
                                .montoServicio(monto)
                                .alicuota(alicuota)
                                .tieneCantidad(true)
                                .build();
        }

        private Cuenta crearCuenta(Integer id, String razonSocial, Cliente cliente) {
                return Cuenta.builder()
                                .idCuenta(id)
                                .razonSocial(razonSocial)
                                .cuit("2033333333" + id)
                                .condicionFrenteIVA(CondicionFrenteIVA.CONSUMIDOR_FINAL)
                                .domicilioFiscal("Calle Falsa " + id)
                                .cliente(cliente)
                                .serviciosDeLaCuenta(new ArrayList<>()) // Lista mutable vacía
                                .build();
        }

        private ServicioDeLaCuenta crearRelacion(Integer id, Cuenta cuenta, Servicio servicio) {
                return ServicioDeLaCuenta.builder()
                                .idServicioDeLaCuenta(id)
                                .cantidadDePreferencia(1)
                                .cuenta(cuenta)
                                .servicio(servicio)
                                .estadoServicio(EstadoServicio.PENDIENTE)
                                .build();
        }

        private DetalleFactura crearDetalleFactura(Integer id, ServicioDeLaCuenta servicioDeLaCuenta) {
                double subtotal = servicioDeLaCuenta.getServicio().getMontoServicio();
                double iva = subtotal * servicioDeLaCuenta.getServicio().getAlicuota();

                return DetalleFactura.builder()
                                .idDetalleFactura(id)
                                .cantidad(1)
                                .precioUnitario(servicioDeLaCuenta.getServicio().getMontoServicio())
                                .subtotal(subtotal)
                                .ivaCalculado(iva)
                                .servicioDeLaCuenta(servicioDeLaCuenta)
                                .build();
        }

        private Factura crearFacturaCompleta(Integer idFactura, Cuenta cuenta) {
                // Crear DatosClienteFactura
                DatosClienteFactura datosCliente = crearDatosCliente(cuenta);

                // Crear DatosServicioFactura usando los servicios de la cuenta
                List<DatosServicioFactura> datosServicios = crearDatosServicio(cuenta.getServiciosDeLaCuenta());

                // Crear detalles de factura a partir de los servicios de la cuenta
                List<DetalleFactura> detalles = new ArrayList<>();
                int detalleId = 1;
                for (ServicioDeLaCuenta sdc : cuenta.getServiciosDeLaCuenta()) {
                        DetalleFactura detalle = crearDetalleFactura(detalleId, sdc);
                        detalles.add(detalle);
                        detalleId++;
                }

                // Crear factura completa
                Factura factura = Factura.builder()
                                .idFactura(idFactura)
                                .montoTotal(detalles.stream().mapToDouble(d -> d.getSubtotal() + d.getIvaCalculado()).sum())
                                .fechaEmision(LocalDate.now())
                                .fechaVencimiento(LocalDate.now().plusDays(30))
                                .nroComprobante(String.format("0001-%08d", idFactura))
                                .notaDeCredito(null)
                                .pagos(null)
                                .datosClienteFactura(datosCliente)
                                .datosServicioFactura(datosServicios)
                                .detallesFacturas(detalles)
                                .build();

                // Asociar factura a los detalles
                detalles.forEach(d -> d.setFactura(factura));

                return factura;
        }

        private DatosClienteFactura crearDatosCliente(Cuenta cuenta) {
                DatosClienteFactura datos = new DatosClienteFactura();
                datos.setCondicionFrenteIVA(cuenta.getCondicionFrenteIVA());
                datos.setDomicilioFiscal(cuenta.getDomicilioFiscal());
                datos.setRazonSocial(cuenta.getRazonSocial());
                datos.setCuit(cuenta.getCuit());

                return datos;
        }

        private List<DatosServicioFactura> crearDatosServicio(List<ServicioDeLaCuenta> servicios){
                return servicios.stream().map(servicio -> DatosServicioFactura.builder()
                        .nombreServicio(servicio.getServicio().getNombreServicio())
                        .alicuotaActual(servicio.getServicio().getAlicuota())
                        .precioActual(servicio.getServicio().getMontoServicio())
                        .descripcionServicio(servicio.getServicio().getDescripcionServicio())
                        .build())
                .collect(Collectors.toList());
        }

        // * =============TEST FACTURACION INDIVIDUAL==================

        @Test
        void facturacionIndividual_FlujoNormal_1Servicio() {
                // ARRANGE - Preparar datos de entrada
                // SERVICIO1: $8000.0 * 21% IVA, cantidad: 2
                // Subtotal: 8000 * 2 = 16000
                // IVA: 16000 * 0.21 = 3360
                // Monto Total: 19360

                ServicioSeleccionadoDto servicio1 = new ServicioSeleccionadoDto();
                servicio1.setIdServicio(1);
                servicio1.setCantidad(2);
                servicio1.setSeleccionado(true);

                List<ServicioSeleccionadoDto> serviciosSeleccionados = new ArrayList<>();
                serviciosSeleccionados.add(servicio1);

                Periodicidad periodicidad = Periodicidad.MENSUAL;

                // Configurar Mocks para que devuelvan los servicios de la cuenta
                when(servicioDeLaCuentaRepository.findById(1))
                                .thenReturn(Optional.of(SDC1_1));

                // ACT - Ejecutar el método a testear
                facturacionService.facturacionIndividual(serviciosSeleccionados, periodicidad);

                // ASSERT - Verificar que se llamaron los métodos esperados
                // 1. Capturar la factura guardada
                ArgumentCaptor<Factura> facturaCaptor = ArgumentCaptor.forClass(Factura.class);
                verify(facturacionRepository, times(1)).save(facturaCaptor.capture());

                Factura facturaGuardada = facturaCaptor.getValue();

                // Validamos que la fecha de emision sea correcta
                assertEquals(LocalDate.now(), facturaGuardada.getFechaEmision());

                // Validamos que la fecha de vencimiento sea correcta
                assertEquals(LocalDate.now().plusDays(periodicidad.getDias()), facturaGuardada.getFechaVencimiento());

                // Validamos que cuando se crea la factura, no tiene una nota de credito
                // asociada
                assertEquals(null, facturaGuardada.getNotaDeCredito());

                // Validamos que cuando se crea la factura, no tenga un pago asociado
                assertEquals(null, facturaGuardada.getPagos());

                // 2. Validar monto total
                double montoEsperado = 8000 * 2 + (8000 * 2 * 0.21); // 16000 + 3360 = 19360
                assert facturaGuardada.getMontoTotal() != null : "El monto total no debería ser null";
                assert Math.abs(facturaGuardada.getMontoTotal() - montoEsperado) < 0.01 : String.format(
                                "Monto esperado: %f, pero obtuvo: %f", montoEsperado, facturaGuardada.getMontoTotal());

                // 3. Validar DatosClienteFactura (datos del cliente almacenados en la factura)
                assertNotNull(facturaGuardada.getDatosClienteFactura(),
                                "DatosClienteFactura no debe ser nulo");
                assertEquals(SDC1_1.getCuenta().getIdCuenta(), facturaGuardada.getDatosClienteFactura().getIdCuenta(),
                                "ID de cuenta debe coincidir");
                assertEquals(SDC1_1.getCuenta().getRazonSocial(),
                                facturaGuardada.getDatosClienteFactura().getRazonSocial(),
                                "Razón social debe coincidir");
                assertEquals(SDC1_1.getCuenta().getCuit(), facturaGuardada.getDatosClienteFactura().getCuit(),
                                "CUIT debe coincidir");
                assertEquals(SDC1_1.getCuenta().getDomicilioFiscal(),
                                facturaGuardada.getDatosClienteFactura().getDomicilioFiscal(),
                                "Domicilio fiscal debe coincidir");
                assertEquals(SDC1_1.getCuenta().getCondicionFrenteIVA(),
                                facturaGuardada.getDatosClienteFactura().getCondicionFrenteIVA(),
                                "Condición frente al IVA debe coincidir");

                // 4. Validar DatosServicioFactura (datos de los servicios almacenados en la
                // factura)
                assertNotNull(facturaGuardada.getDatosServicioFactura(),
                                "DatosServicioFactura no debe ser nulo");
                assertEquals(1, facturaGuardada.getDatosServicioFactura().size(),
                                "Debe haber 1 servicio en la factura");

                // Validar el primer (y único) servicio
                DatosServicioFactura servicoFacturado = facturaGuardada.getDatosServicioFactura().get(0);
                assertEquals(SDC1_1.getServicio().getNombreServicio(), servicoFacturado.getNombreServicio(),
                                "Nombre del servicio debe coincidir");
                assertEquals(SDC1_1.getServicio().getDescripcionServicio(), servicoFacturado.getDescripcionServicio(),
                                "Descripción del servicio debe coincidir");
                assertEquals(SDC1_1.getServicio().getMontoServicio(), servicoFacturado.getPrecioActual(),
                                "Precio del servicio debe coincidir");
                assertEquals(SDC1_1.getServicio().getAlicuota(), servicoFacturado.getAlicuotaActual(),
                                "Alícuota del servicio debe coincidir");

                // 5. Verificar que se actualizaron los estados de los servicios a FACTURADO
                verify(servicioDeLaCuentaRepository, times(1))
                                .saveAll(any());

                // 6. Verificar que el servicio ahora tiene estado FACTURADO
                assert SDC1_1.getEstadoServicio() == EstadoServicio.FACTURADO;
        }

        @Test
        void facturacionIndividual_FlujoNormal_2Servicios() {
                // ARRANGE - Preparar datos de entrada
                // SERVICIO1: $8000.0 * 21% IVA, cantidad: 2
                // Subtotal: 8000 * 2 = 16000, IVA: 3360, Total: 19360
                // SERVICIO2: $15000.0 * 27% IVA, cantidad: 3
                // Subtotal: 15000 * 3 = 45000, IVA: 12150, Total: 57150
                // Monto Total: 19360 + 57150 = 76510

                ServicioSeleccionadoDto servicio1 = new ServicioSeleccionadoDto();
                servicio1.setIdServicio(1);
                servicio1.setCantidad(2);
                servicio1.setSeleccionado(true);

                ServicioSeleccionadoDto servicio2 = new ServicioSeleccionadoDto();
                servicio2.setIdServicio(2);
                servicio2.setCantidad(3);
                servicio2.setSeleccionado(true);

                List<ServicioSeleccionadoDto> serviciosSeleccionados = new ArrayList<>();
                serviciosSeleccionados.add(servicio1);
                serviciosSeleccionados.add(servicio2);

                Periodicidad periodicidad = Periodicidad.MENSUAL;

                // Configurar Mocks para que devuelvan los servicios de la cuenta
                when(servicioDeLaCuentaRepository.findById(1))
                                .thenReturn(Optional.of(SDC1_1));
                when(servicioDeLaCuentaRepository.findById(2))
                                .thenReturn(Optional.of(SDC1_2));

                // ACT - Ejecutar el método a testear
                facturacionService.facturacionIndividual(serviciosSeleccionados, periodicidad);

                // ASSERT - Verificar que se llamaron los métodos esperados
                // 1. Capturar la factura guardada
                ArgumentCaptor<com.gpp.servisoft.model.entities.Factura> facturaCaptor = ArgumentCaptor
                                .forClass(com.gpp.servisoft.model.entities.Factura.class);
                verify(facturacionRepository, times(1)).save(facturaCaptor.capture());

                Factura facturaGuardada = facturaCaptor.getValue();

                // 2. Validar el monto total
                double servicio1Total = (8000 * 2) + (8000 * 2 * 0.21); // 19360
                double servicio2Total = (15000 * 3) + (15000 * 3 * 0.27); // 57150
                double montoEsperado = servicio1Total + servicio2Total; // 76510

                assert facturaGuardada.getMontoTotal() != null : "El monto total no debería ser null";
                assert Math.abs(facturaGuardada.getMontoTotal() - montoEsperado) < 0.01 : String.format(
                                "Monto esperado: %f, pero obtuvo: %f", montoEsperado, facturaGuardada.getMontoTotal());

                // 3. Verificar que se actualizaron los estados de los servicios a FACTURADO
                verify(servicioDeLaCuentaRepository, times(1))
                                .saveAll(any());

                // 4. Verificar que los servicios ahora tienen estado FACTURADO
                assert SDC1_1.getEstadoServicio() == EstadoServicio.FACTURADO;
                assert SDC1_2.getEstadoServicio() == EstadoServicio.FACTURADO;
        }

        @Test
        void facturacionIndividual_ListaVacia_LanzaExcepcion() {
                // ARRANGE
                List<ServicioSeleccionadoDto> serviciosVacios = new ArrayList<>();
                Periodicidad periodicidad = Periodicidad.MENSUAL;

                // ACT & ASSERT
                assertThrows(ExcepcionNegocio.class,
                                () -> facturacionService.facturacionIndividual(serviciosVacios, periodicidad),
                                "Debe lanzar ExcepcionNegocio cuando la lista está vacía");
        }

        @Test
        void facturacionIndividual_ListaNula_LanzaExcepcion() {
                // ARRANGE
                Periodicidad periodicidad = Periodicidad.MENSUAL;

                // ACT & ASSERT
                assertThrows(ExcepcionNegocio.class,
                                () -> facturacionService.facturacionIndividual(null, periodicidad),
                                "Debe lanzar ExcepcionNegocio cuando la lista es null");
        }

        @Test
        void facturacionIndividual_ServicioNoEncontrado_LanzaExcepcion() {
                // ARRANGE
                ServicioSeleccionadoDto servicioInexistente = new ServicioSeleccionadoDto();
                servicioInexistente.setIdServicio(999);
                servicioInexistente.setCantidad(1);
                servicioInexistente.setSeleccionado(true);

                List<ServicioSeleccionadoDto> servicios = new ArrayList<>();
                servicios.add(servicioInexistente);

                Periodicidad periodicidad = Periodicidad.MENSUAL;

                // Mock que retorna Optional.empty() simulando que no existe
                when(servicioDeLaCuentaRepository.findById(999))
                                .thenReturn(Optional.empty());

                // ACT & ASSERT
                assertThrows(ExcepcionNegocio.class,
                                () -> facturacionService.facturacionIndividual(servicios, periodicidad),
                                "Debe lanzar ExcepcionNegocio cuando el servicio no existe");
        }

        @Test
        void facturacionIndividual_CantidadInvalida_LanzaExcepcion() {
                // ARRANGE
                ServicioSeleccionadoDto servicioConCantidadCero = new ServicioSeleccionadoDto();
                servicioConCantidadCero.setIdServicio(1);
                servicioConCantidadCero.setCantidad(-44); // Cantidad inválida
                servicioConCantidadCero.setSeleccionado(true);

                List<ServicioSeleccionadoDto> servicios = new ArrayList<>();
                servicios.add(servicioConCantidadCero);

                Periodicidad periodicidad = Periodicidad.MENSUAL;

                // ACT & ASSERT
                assertThrows(ExcepcionNegocio.class,
                                () -> facturacionService.facturacionIndividual(servicios, periodicidad),
                                "Debe lanzar ExcepcionNegocio cuando la cantidad es <= 0");
        }

        @Test
        void facturacionIndividual_CantidadNula_LanzaExcepcion() {
                // ARRANGE
                ServicioSeleccionadoDto servicioConCantidadNula = new ServicioSeleccionadoDto();
                servicioConCantidadNula.setIdServicio(1);
                servicioConCantidadNula.setCantidad(null); // Cantidad nula
                servicioConCantidadNula.setSeleccionado(true);

                List<ServicioSeleccionadoDto> servicios = new ArrayList<>();
                servicios.add(servicioConCantidadNula);

                Periodicidad periodicidad = Periodicidad.MENSUAL;

                // ACT & ASSERT
                assertThrows(ExcepcionNegocio.class,
                                () -> facturacionService.facturacionIndividual(servicios, periodicidad),
                                "Debe lanzar ExcepcionNegocio cuando la cantidad es nula");
        }

        // * === TESTS FACTURACION MASIVA ===

        @Test
        void facturacionMasiva_FlujoNormal_3Cuentas() {
                // ARRANGE - Preparar datos y mocks
                List<Cuenta> cuentasActivas = Arrays.asList(CUENTA1, CUENTA2, CUENTA3);
                Periodicidad periodicidad = Periodicidad.MENSUAL;

                // Configurar mock para devolver cuentas activas
                when(cuentaRepository.findByEstado(any())).thenReturn(cuentasActivas);

                // ACT - Ejecutar el método a testear
                facturacionService.facturacionMasiva(periodicidad);

                // ASSERT - Capturar y verificar resultados
                // 1. Verificar que se guardó la facturación masiva exactamente una vez
                ArgumentCaptor<FacturacionMasiva> facturacionMasivaCaptor = ArgumentCaptor
                                .forClass(FacturacionMasiva.class);
                verify(facturacionMasivaRepository, times(1)).save(facturacionMasivaCaptor.capture());

                FacturacionMasiva facturacionMasiva = facturacionMasivaCaptor.getValue();

                // 2. Verificar que se guardaron 3 facturas (una por cada cuenta)
                ArgumentCaptor<Factura> facturaCaptor = ArgumentCaptor.forClass(Factura.class);
                verify(facturacionRepository, times(3)).save(facturaCaptor.capture());

                List<Factura> facturasGuardadas = facturaCaptor.getAllValues();

                // 3. Validar que la facturación masiva tiene el monto correcto
                // (suma de todos los montos de las facturas individuales)
                double montoTotalFacturas = facturasGuardadas.stream()
                                .mapToDouble(Factura::getMontoTotal)
                                .sum();

                assert facturacionMasiva.getMontoTotal() != null
                                : "El monto total de facturación masiva no debería ser null";
                assert Math.abs(facturacionMasiva.getMontoTotal() - montoTotalFacturas) < 0.01
                                : String.format("Monto esperado: %f, pero obtuvo: %f", montoTotalFacturas,
                                                facturacionMasiva.getMontoTotal());

                // 4. Validar que la fecha de emisión es la actual
                assertEquals(facturacionMasiva.getFechaEmision(), LocalDate.now(),
                                "La fecha de emisión debe ser la fecha actual");

                // 5. Validar que se generó una factura por cada cuenta
                assertEquals(3, facturasGuardadas.size(),
                                "Debe generarse una factura por cada cuenta activa (3 cuentas)");
        }

        @Test
        void facturacionMasiva_FlujoNormal_1Cuenta() {
                // ARRANGE - Solo una cuenta activa
                List<Cuenta> cuentasActivas = Arrays.asList(CUENTA1);
                Periodicidad periodicidad = Periodicidad.BIMESTRAL;

                when(cuentaRepository.findByEstado(any())).thenReturn(cuentasActivas);

                // ACT
                facturacionService.facturacionMasiva(periodicidad);

                // ASSERT
                // 1. Verificar que se guardó la facturación masiva
                ArgumentCaptor<FacturacionMasiva> facturacionMasivaCaptor = ArgumentCaptor
                                .forClass(FacturacionMasiva.class);
                verify(facturacionMasivaRepository, times(1)).save(facturacionMasivaCaptor.capture());

                // 2. Verificar que se guardó 1 factura (una por la única cuenta)
                ArgumentCaptor<Factura> facturaCaptor = ArgumentCaptor.forClass(Factura.class);
                verify(facturacionRepository, times(1)).save(facturaCaptor.capture());

                List<Factura> facturasGuardadas = facturaCaptor.getAllValues();
                assertEquals(1, facturasGuardadas.size(),
                                "Debe generarse una factura para la única cuenta activa");
        }

        @Test
        void facturacionMasiva_PeriodicidadNula_LanzaExcepcion() {
                // ARRANGE - Sin necesidad de configurar mocks, la excepción se lanza antes

                // ACT & ASSERT
                assertThrows(ExcepcionNegocio.class,
                                () -> facturacionService.facturacionMasiva(null),
                                "Debe lanzar ExcepcionNegocio cuando la periodicidad es nula");
        }

        @Test
        void facturacionMasiva_NoCuentasActivas_LanzaExcepcion() {
                // ARRANGE - Lista vacía de cuentas activas
                List<Cuenta> cuentasActivas = new ArrayList<>();
                Periodicidad periodicidad = Periodicidad.MENSUAL;

                when(cuentaRepository.findByEstado(any())).thenReturn(cuentasActivas);

                // ACT & ASSERT
                assertThrows(ExcepcionNegocio.class,
                                () -> facturacionService.facturacionMasiva(periodicidad),
                                "Debe lanzar ExcepcionNegocio cuando no hay cuentas activas");
        }

        @Test
        void facturacionMasiva_CuentasSinServiciosPendientes_LanzaExcepcion() {
                // ARRANGE - Cuentas activas pero sin servicios en estado PENDIENTE
                // Primero, dejamos las cuentas sin servicios
                CUENTA1.getServiciosDeLaCuenta().clear();
                CUENTA2.getServiciosDeLaCuenta().clear();
                CUENTA3.getServiciosDeLaCuenta().clear();

                List<Cuenta> cuentasActivas = Arrays.asList(CUENTA1, CUENTA2, CUENTA3);
                Periodicidad periodicidad = Periodicidad.MENSUAL;

                when(cuentaRepository.findByEstado(any())).thenReturn(cuentasActivas);

                // ACT & ASSERT
                assertThrows(ExcepcionNegocio.class,
                                () -> facturacionService.facturacionMasiva(periodicidad),
                                "Debe lanzar ExcepcionNegocio cuando no hay servicios pendientes en las cuentas");
        }

        @Test
        void facturacionMasiva_CuentasConServiciosYaFacturados_LanzaExcepcion() {
                // ARRANGE - Cuentas con servicios pero todos en estado FACTURADO (no hay
                // PENDIENTES)
                // Cambiar todos los servicios a FACTURADO
                SDC1_1.setEstadoServicio(EstadoServicio.FACTURADO);
                SDC1_2.setEstadoServicio(EstadoServicio.FACTURADO);
                SDC2_1.setEstadoServicio(EstadoServicio.FACTURADO);
                SDC2_2.setEstadoServicio(EstadoServicio.FACTURADO);
                SDC3_1.setEstadoServicio(EstadoServicio.FACTURADO);
                SDC3_2.setEstadoServicio(EstadoServicio.FACTURADO);

                List<Cuenta> cuentasActivas = Arrays.asList(CUENTA1, CUENTA2, CUENTA3);
                Periodicidad periodicidad = Periodicidad.MENSUAL;

                when(cuentaRepository.findByEstado(any())).thenReturn(cuentasActivas);

                // ACT & ASSERT
                assertThrows(ExcepcionNegocio.class,
                                () -> facturacionService.facturacionMasiva(periodicidad),
                                "Debe lanzar ExcepcionNegocio cuando todos los servicios ya están facturados");
        }

        @Test
        void facturacionMasiva_CuentasMixtas_AlgunasConServiciosPendientes() {
                // ARRANGE - Solo CUENTA1 y CUENTA2 tienen servicios pendientes
                CUENTA3.getServiciosDeLaCuenta().clear(); // CUENTA3 sin servicios

                List<Cuenta> cuentasActivas = Arrays.asList(CUENTA1, CUENTA2, CUENTA3);
                Periodicidad periodicidad = Periodicidad.TRIMESTRAL;

                when(cuentaRepository.findByEstado(any())).thenReturn(cuentasActivas);

                // ACT
                facturacionService.facturacionMasiva(periodicidad);

                // ASSERT
                // Se deben generar 2 facturas (CUENTA1 y CUENTA2, pero no CUENTA3)
                ArgumentCaptor<Factura> facturaCaptor = ArgumentCaptor.forClass(Factura.class);
                verify(facturacionRepository, times(2)).save(facturaCaptor.capture());

                List<Factura> facturasGuardadas = facturaCaptor.getAllValues();
                assertEquals(2, facturasGuardadas.size(),
                                "Debe generarse una factura solo para las cuentas con servicios pendientes");
        }

        // * ==== TESTS OBTENER FACTURAS ===

        @Test
        void obtenerFacturas_FlujoNormal_ConFiltros() {
                // ARRANGE
                Integer idCuenta = 1;
                String comprobante = "0001-00000001";
                Pageable pageable = PageRequest.of(0, 10);

                // Crear facturas de prueba - solo verificar que el flujo funciona sin mapear
                List<Factura> facturas = new ArrayList<>();
                // Objeto Page Valido {Representa lo que me devuelve Spring}
                Page<Factura> pageFacturas = new PageImpl<>(facturas, pageable, 0);

                // Configurar mock
                when(facturacionRepository.findFacturasByFilters(idCuenta, comprobante, pageable))
                                .thenReturn(pageFacturas);

                // ACT
                Page<FacturacionDTO> resultado = facturacionService.obtenerFacturas(idCuenta, comprobante, pageable);

                // ASSERT
                // 1. Verificar que se llamó al repositorio con los parámetros correctos
                verify(facturacionRepository, times(1)).findFacturasByFilters(idCuenta, comprobante, pageable);

                // 2. Verificar que retorna una página (aunque vacía en este caso)
                assertEquals(0, resultado.getContent().size(),
                                "La página debe estar vacía en este test unitario");

                // 3. Verificar que la información de paginación se mantiene
                assertEquals(pageable.getPageNumber(), resultado.getNumber(),
                                "El número de página debe ser igual");
                assertEquals(pageable.getPageSize(), resultado.getSize(),
                                "El tamaño de página debe ser igual");
        }

        @Test
        void obtenerFacturas_SinResultados() {
                // ARRANGE
                Integer idCuenta = 999;
                String comprobante = "9999-9999999";
                Pageable pageable = PageRequest.of(0, 10);

                // Página vacía
                Page<Factura> pageVacia = new PageImpl<>(new ArrayList<>(), pageable, 0);

                when(facturacionRepository.findFacturasByFilters(idCuenta, comprobante, pageable))
                                .thenReturn(pageVacia);

                // ACT
                Page<FacturacionDTO> resultado = facturacionService.obtenerFacturas(idCuenta, comprobante, pageable);

                // ASSERT
                // Verificar que retorna una página vacía
                assertTrue(resultado.getContent().isEmpty(),
                                "La página debe estar vacía cuando no hay resultados");
                assertEquals(0, resultado.getTotalElements(),
                                "El total de elementos debe ser 0");
        }

        @Test
        void obtenerFacturas_FlujoNormalConPaginacion() {
                // ARRANGE - Validar que funciona con diferentes páginas
                Pageable pageable = PageRequest.of(1, 5); // Página 2, 5 elementos por página

                // Página vacía para evitar problemas de mapping
                List<Factura> facturas = new ArrayList<>();
                Page<Factura> pagina = new PageImpl<>(facturas, pageable, 25); // 25 elementos totales

                when(facturacionRepository.findFacturasByFilters(null, null, pageable))
                                .thenReturn(pagina);

                // ACT
                Page<FacturacionDTO> resultado = facturacionService.obtenerFacturas(null, null, pageable);

                // ASSERT
                assertEquals(1, resultado.getNumber(),
                                "Debe estar en la página 1 (segunda página)");
                assertEquals(5, resultado.getSize(),
                                "Debe tener 5 elementos por página");
                assertEquals(25, resultado.getTotalElements(),
                                "Debe haber 25 elementos totales");
                assertTrue(resultado.hasNext(),
                                "Debe haber próxima página");
        }

        // * === TESTS OBTENER FACTURAS PENDIENTES Y PARCIALES ===

        @Test
        void obtenerFacturasPendientesyParciales_FlujoNormal() {
                // ARRANGE
                Integer idCuenta = 1;
                String comprobante = "0001-00000001";
                Pageable pageable = PageRequest.of(0, 10);

                // Usar lista vacía para evitar problemas de mapping
                List<Factura> facturas = new ArrayList<>();
                // Creo la respuesta que me va a devolver Spring JPA
                Page<Factura> pageFacturas = new PageImpl<>(facturas, pageable, 0);

                // Configurar mock
                when(facturacionRepository.findFacturasPendientesByFilters(idCuenta, comprobante, pageable))
                                .thenReturn(pageFacturas);

                // ACT
                Page<FacturacionDTO> resultado = facturacionService.obtenerFacturasPendientesyParciales(
                                idCuenta, comprobante, pageable);

                // ASSERT
                // 1. Verificar que se llamó al repositorio correcto
                verify(facturacionRepository, times(1)).findFacturasPendientesByFilters(idCuenta, comprobante,
                                pageable);

                // 2. Verificar que retorna una página vacía
                assertEquals(0, resultado.getContent().size(),
                                "Debe retornar una página vacía en este test unitario");
        }

        @Test
        void obtenerFacturasPendientesyParciales_SinResultados() {
                // ARRANGE - Cuando no hay facturas pendientes ni parcialmente pagadas
                Pageable pageable = PageRequest.of(0, 10);
                Page<Factura> pageVacia = new PageImpl<>(new ArrayList<>(), pageable, 0);

                when(facturacionRepository.findFacturasPendientesByFilters(any(), any(), any()))
                                .thenReturn(pageVacia);

                // ACT
                Page<FacturacionDTO> resultado = facturacionService.obtenerFacturasPendientesyParciales(
                                null, null, pageable);

                // ASSERT
                assertTrue(resultado.getContent().isEmpty(),
                                "No debe haber facturas pendientes ni parcialmente pagadas");
                assertEquals(0, resultado.getTotalElements(),
                                "El total debe ser 0");
        }

        @Test
        void obtenerFacturasPendientesyParciales_ConMultiplesPaginas() {
                // ARRANGE - Validar paginación con múltiples páginas
                Pageable pageable = PageRequest.of(0, 10);

                // Usar lista vacía para evitar problemas de mapping
                List<Factura> facturas = new ArrayList<>();
                Page<Factura> pagina = new PageImpl<>(facturas, pageable, 35); // 35 elementos totales

                when(facturacionRepository.findFacturasPendientesByFilters(1, "B", pageable))
                                .thenReturn(pagina);

                // ACT
                Page<FacturacionDTO> resultado = facturacionService.obtenerFacturasPendientesyParciales(1, "B",
                                pageable);

                // ASSERT
                assertEquals(0, resultado.getContent().size(),
                                "Primera página debe estar vacía en este test unitario");
                assertEquals(35, resultado.getTotalElements(),
                                "Total debe ser 35 elementos (de acuerdo con el mock)");
                assertTrue(resultado.hasNext(),
                                "Debe haber siguiente página");
                assertEquals(0, resultado.getNumber(),
                                "Debe ser la página 0 (primera)");
        }

        // * === ANULACION DE FACTURAS (Nota de Credito) ===

        @Test
        void anularFactura_FlujoNormal() {
                // ARRANGE - Usar factura1 ya creada en setUp() con detalles completos
                // Establecer estado de servicios a FACTURADO
                SDC1_1.setEstadoServicio(EstadoServicio.FACTURADO);
                SDC1_2.setEstadoServicio(EstadoServicio.FACTURADO);

                AnulacionDto anulacionDto = AnulacionDto.builder()
                                .idFactura(1)
                                .motivo("Error en cálculo")
                                .build();

                when(facturacionRepository.findById(1)).thenReturn(Optional.of(factura1));

                // ACT
                facturacionService.anularFactura(anulacionDto);

                // ASSERT
                // 1. Verificar que se guardó la nota de crédito
                ArgumentCaptor<NotaDeCredito> notaCreditoCaptor = ArgumentCaptor.forClass(NotaDeCredito.class);
                verify(notaDeCreditoRepository, times(1)).save(notaCreditoCaptor.capture());

                NotaDeCredito notaGuardada = notaCreditoCaptor.getValue();
                assertEquals(factura1, notaGuardada.getFactura(),
                                "La nota de crédito debe estar asociada a factura1");
                assertEquals(LocalDate.now(), notaGuardada.getFechaEmision(),
                                "La fecha de emisión debe ser hoy");
                assertEquals(28730.0, notaGuardada.getMonto(),
                                "El monto debe coincidir con factura1 (28730)");
                assertEquals("Error en cálculo", notaGuardada.getMotivo(),
                                "El motivo debe coincidir");

                // 2. Verificar que se revertieron los servicios a PENDIENTE
                verify(servicioDeLaCuentaRepository, times(1)).saveAll(any());
                assertEquals(EstadoServicio.PENDIENTE, SDC1_1.getEstadoServicio(),
                                "El servicio SDC1_1 debe revertir a PENDIENTE");
                assertEquals(EstadoServicio.PENDIENTE, SDC1_2.getEstadoServicio(),
                                "El servicio SDC1_2 debe revertir a PENDIENTE");
        }

        @Test
        void anularFactura_FacturaYaAnulada_LanzaExcepcion() {
                // ARRANGE - Crear una factura con NotaDeCredito existente
                Factura facturaAnulada = Factura.builder()
                                .idFactura(2)
                                .montoTotal(28730.0)
                                .notaDeCredito(new NotaDeCredito()) // YA ANULADA
                                .build();

                AnulacionDto anulacionDto = AnulacionDto.builder()
                                .idFactura(2)
                                .motivo("Error")
                                .build();

                when(facturacionRepository.findById(2)).thenReturn(Optional.of(facturaAnulada));

                // ACT & ASSERT
                assertThrows(ExcepcionNegocio.class,
                                () -> facturacionService.anularFactura(anulacionDto),
                                "Debe lanzar excepción si la factura ya está anulada");
        }

        @Test
        void anularFactura_AnulacionDtoNulo_LanzaExcepcion() {
                // ARRANGE - DTO nulo
                AnulacionDto anulacionDto = null;

                // ACT & ASSERT
                assertThrows(ExcepcionNegocio.class,
                                () -> facturacionService.anularFactura(anulacionDto),
                                "Debe lanzar excepción si el DTO es nulo");
        }

        @Test
        void anularFactura_IdFacturaNulo_LanzaExcepcion() {
                // ARRANGE - ID de factura nulo
                AnulacionDto anulacionDto = AnulacionDto.builder()
                                .idFactura(null)
                                .motivo("Error")
                                .build();

                // ACT & ASSERT
                assertThrows(ExcepcionNegocio.class,
                                () -> facturacionService.anularFactura(anulacionDto),
                                "Debe lanzar excepción si el ID de factura es nulo");
        }

        @Test
        void anularFactura_MotivoVacio_LanzaExcepcion() {
                // ARRANGE - Motivo vacío
                AnulacionDto anulacionDto = AnulacionDto.builder()
                                .idFactura(1)
                                .motivo("") // Vacío
                                .build();

                // ACT & ASSERT
                assertThrows(ExcepcionNegocio.class,
                                () -> facturacionService.anularFactura(anulacionDto),
                                "Debe lanzar excepción si el motivo está vacío");
        }

        @Test
        void anularFactura_MotivoNulo_LanzaExcepcion() {
                // ARRANGE - Motivo nulo
                AnulacionDto anulacionDto = AnulacionDto.builder()
                                .idFactura(1)
                                .motivo(null) // Nulo
                                .build();

                // ACT & ASSERT
                assertThrows(ExcepcionNegocio.class,
                                () -> facturacionService.anularFactura(anulacionDto),
                                "Debe lanzar excepción si el motivo es nulo");
        }

        @Test
        void anularFactura_FacturaNoEncontrada_LanzaExcepcion() {
                // ARRANGE - Factura no existe
                AnulacionDto anulacionDto = AnulacionDto.builder()
                                .idFactura(999)
                                .motivo("Error")
                                .build();

                when(facturacionRepository.findById(999)).thenReturn(Optional.empty());

                // ACT & ASSERT
                assertThrows(ExcepcionNegocio.class,
                                () -> facturacionService.anularFactura(anulacionDto),
                                "Debe lanzar excepción si la factura no existe");
        }

        @Test
        void anularFactura_ConMultiplesDetalles_RevierteTodos() {
                // ARRANGE - Usar factura1 que ya tiene 2 detalles (Detalle1 y Detalle2)
                // Establecer estado de servicios a FACTURADO
                SDC1_1.setEstadoServicio(EstadoServicio.FACTURADO);
                SDC1_2.setEstadoServicio(EstadoServicio.FACTURADO);

                AnulacionDto anulacionDto = AnulacionDto.builder()
                                .idFactura(1)
                                .motivo("Cancelación")
                                .build();

                when(facturacionRepository.findById(1)).thenReturn(Optional.of(factura1));

                // ACT
                facturacionService.anularFactura(anulacionDto);

                // ASSERT
                // 1. Verificar que se creó la nota de crédito
                verify(notaDeCreditoRepository, times(1)).save(any(NotaDeCredito.class));

                // 2. Verificar que se actualizaron todos los servicios
                @SuppressWarnings("unchecked")
                ArgumentCaptor<Iterable<ServicioDeLaCuenta>> serviciosCaptor = ArgumentCaptor.forClass(Iterable.class);
                verify(servicioDeLaCuentaRepository, times(1)).saveAll(serviciosCaptor.capture());

                List<ServicioDeLaCuenta> serviciosActualizados = new ArrayList<>();
                serviciosCaptor.getValue().forEach(serviciosActualizados::add);

                assertEquals(2, serviciosActualizados.size(),
                                "Debe haber actualizado 2 servicios");
                assertTrue(serviciosActualizados.stream()
                                .allMatch(s -> s.getEstadoServicio() == EstadoServicio.PENDIENTE),
                                "Todos los servicios deben estar en estado PENDIENTE");
        }
}