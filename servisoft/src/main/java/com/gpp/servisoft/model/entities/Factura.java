package com.gpp.servisoft.model.entities;

import java.time.LocalDate;
import java.util.List;

import com.gpp.servisoft.model.enums.EstadoFactura;
import com.gpp.servisoft.model.enums.Periodicidad;
import com.gpp.servisoft.model.enums.TipoComprobante;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa una factura en el sistema.
 *
 * Contiene el identificador, el monto total y las fechas relevantes
 * (emisión y vencimiento).
 */
@Entity
@Data
@Table(name = "factura")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Factura {

    // COSNTANTES
    private static final String PUNTO_VENTA = "0001";
    /**
     * Identificador único de la factura. Se genera automáticamente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_factura")
    @SequenceGenerator(name = "seq_factura", sequenceName = "seq_factura", allocationSize = 1)
    @Setter(AccessLevel.NONE)
    private int idFactura;

    /**
     * Monto total de la factura. Debe ser un valor mayor o igual a 0.0.
     * Se permite 0.0 para facturas sin cargo.
     */
    @DecimalMin(value = "0.0", inclusive = true, message = "El monto total no puede ser negativo")
    private Double montoTotal;

    /**
     * Fecha de emisión de la factura. No puede ser nula y no puede estar en el
     * futuro.
     */
    @NotNull(message = "La fecha de emisión es obligatoria")
    @PastOrPresent(message = "La fecha de emisión no puede ser en el futuro")
    private LocalDate fechaEmision;

    /**
     * Nro De COmprobante de la Factura
     */
    private String nroComprobante;

    /**
     * Periodo el cual va a durar la factura
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "La periodicidad es obligatoria")
    private Periodicidad periodicidad;

    /**
     * Fecha de vencimiento de la factura. No puede ser nula y debe ser hoy o en el
     * futuro.
     */
    @NotNull(message = "La fecha de vencimiento es obligatoria")
    @FutureOrPresent(message = "La fecha de vencimiento debe ser hoy o una fecha futura")
    private LocalDate fechaVencimiento;

    /**
     * Tipo de la factura, depende de la condicion frente al iva del CLiente
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoComprobante tipoComprobante;

    /**
     * Una Factura involucra muchos detalles de Facturas
     */
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true) // Si un detalle de factura se
                                                                                      // elimina y no tiene un padre
                                                                                      // relacion abz
    @Valid // Valida internamente cada detalleDeFactura
    private List<DetalleFactura> detallesFacturas;

    /**
     * Una factura Almacena de forma Historica los datos Fiscales del Cliente
     * Para una Futura Refacturacion/ Gestion Historica
     */
    @Embedded
    @Valid
    private DatosClienteFactura datosClienteFactura;

    /**
     * Una factura Almacena de forma Historica los datos de un Servicio
     * Para una Futura Refacturacion/ Gestion Historica
     */

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_factura")
    private List<DatosServicioFactura> datosServicioFactura;

    /**
     * Para la cancelacion de una factura es necesario la creacion de
     * una nota de credito
     */

    @OneToOne(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    private NotaDeCredito notaDeCredito;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    @Valid
    private List<Pago> pagos;

    @PrePersist
    public void generarNumeroComprobante() {
        // Obtiene el id autogenerado recién antes de persistir
        this.nroComprobante = String.format("%s-%08d", PUNTO_VENTA, idFactura);
    }

    // METODOS

    /**
     * Calcula el monto total pagado de una factura.
     * Suma todos los montos de los pagos asociados a la factura.
     * Si no hay pagos o la factura es nula, retorna 0.
     * 
     * @param factura la factura para la cual calcular el saldo pagado
     * @return la suma total de los montos pagados, o 0 si no hay pagos
     * @throws IllegalArgumentException si la factura es nula
     */
    private double calcularTotalPagado() {
        // Si la factura no tiene pagos, retorna 0
        if (this.getPagos() == null || this.getPagos().isEmpty()) {
            return 0.0;
        }

        // Suma todos los montos de los pagos
        return this.getPagos()
                .stream()
                .mapToDouble(p -> {
                    Double monto = p.getMonto();
                    return monto != null ? monto : 0.0;
                })
                .sum();
    }

    public double getSaldo() {
        return this.getMontoTotal() - calcularTotalPagado();
    }

    // Determinacion del Estado de Una Factura

    /**
     * Calcula el estado de la factura basándose en sus pagos, notas de crédito y
     * fecha de vencimiento.
     * 
     * @param factura La entidad Factura con sus datos completos
     * @return EstadoFactura correspondiente según la lógica de negocio
     */
    public  EstadoFactura calcularEstado() {
        
        // --- Lógica 1: ANULADA ---
        // Si existe nota de crédito, la factura está anulada
        if (this.getNotaDeCredito() != null) {
            return EstadoFactura.ANULADA;
        }

        // Validar que el monto total sea válido
        Double monto = this.getMontoTotal();

        if (monto == null || monto < 0.0) {
            throw new IllegalStateException("El monto total de la factura es inválido: " + montoTotal);
        }

        // --- Lógica 2: PAGADA ---
        double totalPagado = 0.0;

        // Si existen pagos asociados a una factura, a totalPagado le asigno
        // el total del monto pagado de cada pago
        if (this.getPagos() != null && !this.getPagos().isEmpty()) {
            totalPagado = calcularTotalPagado();
        }

        // Tolerancia para comparación de doubles (1 centavo)
        double epsilon = 0.01;

        if (totalPagado >= montoTotal) {
            return EstadoFactura.PAGADA;
        }

        // --- Lógica 3: VENCIDA ---
        // Comparamos contra la fecha actual (HOY)
        LocalDate hoy = LocalDate.now();
        boolean estaVencida = this.getFechaVencimiento() != null &&
                this.getFechaVencimiento().isBefore(hoy);

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

    /**
     * Calcula el monto total de la factura (subtotal + IVA)
     * 
     * @param detalles Lista de detalles de la factura
     * @return Monto total incluyendo subtotales e IVA
     * @throws IllegalArgumentException si no tiene asociado Detalles
     */

    public Double calcularMontoTotalFactura() {
        if (this.detallesFacturas == null || this.detallesFacturas.isEmpty()) {
            throw new IllegalArgumentException("No hay Detalles Asociados a la Factura");
        }

        return detallesFacturas.stream()
                .mapToDouble(detalle -> detalle.getSubtotal() + detalle.getIvaCalculado())
                .sum();
    }

}
