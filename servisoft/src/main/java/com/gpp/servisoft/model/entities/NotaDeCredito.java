package com.gpp.servisoft.model.entities;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Table(name = "nota_de_credito")
@NoArgsConstructor
public class NotaDeCredito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private int idNotaCredito;

    /**
     * Monto de anulacion que tiene la nota de credito,
     */
    @DecimalMin(value = "0.0", inclusive = true, message = "El monto total no puede ser negativo")
    private Double monto;

    /**
     * Fecha de emisión de la nota de credito. No puede ser nula y no puede estar en
     * el futuro.
     */
    @NotNull(message = "La fecha de emisión es obligatoria")
    @PastOrPresent(message = "La fecha de emisión no puede ser en el futuro")
    private LocalDate fechaEmision;

    /**
     * Motivo de la nota de crédito. Obligatorio, entre 10 y 100 caracteres.
     */
    @NotBlank(message = "El motivo no puede estar vacío")
    @Size(min = 10, max = 100, message = "El motivo debe tener entre 10 y 100 caracteres")
    private String motivo;


    /**
     * una nota de credito se asocia a una factura, de forma unica
     */
    @OneToOne(optional = false)
    @JoinColumn(name = "id_factura", nullable = false, unique = true)
    private Factura factura;


}
