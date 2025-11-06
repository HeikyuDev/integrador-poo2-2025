package com.gpp.servisoft.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa el detalle de una factura en el sistema.
 * Contiene información sobre los items individuales de una factura,
 * incluyendo cantidad, precio unitario, subtotal e IVA calculado.
 */
@Entity
@Table(name = "detalles_facturas")
@Data
@NoArgsConstructor
public class DetalleFactura {

    /**
     * Identificador único del detalle de factura.
     * Se genera automáticamente y no puede ser modificado manualmente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private int idDetalleFactura;

    /**
     * Cantidad de unidades del producto o servicio en este detalle.
     * Debe ser un número positivo.
     */
    @Min(1)
    private int cantidad;

    /**
     * Precio unitario del producto o servicio.
     * Debe ser un valor igual o mayor a cero.
     */
    @NotNull
    @PositiveOrZero
    private Double precioUnitario;

    /**
     * Subtotal calculado (cantidad * precio unitario).
     * Se calcula automáticamente y debe ser igual o mayor a cero.
     */
    @PositiveOrZero
    private Double subtotal;

    /**
     * Monto de IVA calculado para este detalle.
     * Se calcula automáticamente y debe ser igual o mayor a cero.
     */
    @PositiveOrZero
    private Double ivaCalculado;

    /**
     * un detalle de una Factura se asocia a un unico servicio de una determinada cuenta
     */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicio_cuenta_id", nullable= false)
    private ServicioDeLaCuenta servicioDeLaCuenta;

    /**
     * Muchos detalles de una Factura se asocia a una Factura
     */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_id") // Puede ser nulo, ya que esta instancia va a crear la Factura por asi decirlo
    private Factura factura;
}
