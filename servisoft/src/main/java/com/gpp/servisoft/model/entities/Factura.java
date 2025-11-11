package com.gpp.servisoft.model.entities;

import java.time.LocalDate;
import java.util.List;

import com.gpp.servisoft.model.enums.Periodicidad;
import com.gpp.servisoft.model.enums.TipoComprobante;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@NoArgsConstructor
public class Factura {

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
    @OneToOne(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
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

    /**
     * muchas Facturas pueden estar involucradas en el proceso de facturacion masiva
     */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_facturacion_masiva", nullable = true)
    private FacturacionMasiva facturacionMasiva;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    @Valid
    private List<Pago> pagos;

    @PrePersist
    public void generarNumeroComprobante() {
        // Obtiene el id autogenerado recién antes de persistir
        this.nroComprobante = String.format("%s-%08d", PUNTO_VENTA, idFactura);
    }
}
