package com.gpp.servisoft.model.entities;

import java.time.LocalDate;

import com.gpp.servisoft.model.enums.MetodoPago;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Table(name = "pagos")
@NoArgsConstructor
public class Pago {

    /**
     * Identificador único del pago. Se genera automáticamente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer idPago;

    /**
     * Monto pagado. Debe ser un valor mayor o igual a 0.0.
     * Se permite 0.0 para facturas sin cargo.
     */
    @NotNull(message="El monto es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El monto total no puede ser negativo")
    private Double monto;

    /**
     * Fecha en la que se realizo el pago
     */

    @NotNull(message = "La fecha de Pago es obligatoria")
    @PastOrPresent(message = "La fecha de Pago no puede ser en el futuro")
    private LocalDate fechaPago;

    /**
     * Metodo de pago utilizado por el cliente para realizar la transaccion
     */
    @Enumerated(EnumType.STRING)
    @NotNull(message = "El metodo de Pago es obligatorio")
    private MetodoPago metodoPago;

    /**
     * Un pago esta relacionado con una sola factura
     */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="factura_id")
    private Factura factura;
}
