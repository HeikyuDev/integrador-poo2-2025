package com.gpp.servisoft.model.entities;

import java.time.LocalDate;

import com.gpp.servisoft.model.enums.TipoComprobante;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
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

    private static final String PUNTO_VENTA = "0001";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_nota_credito")
    @SequenceGenerator(name = "seq_nota_credito", sequenceName = "seq_nota_credito", allocationSize = 1)
    @Setter(AccessLevel.NONE)
    private int idNotaCredito;

    /**
     * Monto de la nota de crédito. 
     * No puede ser negativo.
     */
    @DecimalMin(value = "0.0", inclusive = true, message = "El monto total no puede ser negativo")
    private Double monto;

    /**
     * Fecha de emisión de la nota de crédito.
     */
    @NotNull(message = "La fecha de emisión es obligatoria")
    @PastOrPresent(message = "La fecha de emisión no puede ser en el futuro")
    private LocalDate fechaEmision;

    /**
     * Motivo de la nota de crédito. 
     */
    @NotBlank(message = "El motivo no puede estar vacío")
    @Size(min = 10, max = 100, message = "El motivo debe tener entre 10 y 100 caracteres")
    private String motivo;

    /**
     * Tipo de comprobante (ej: NOTA_CREDITO_A, NOTA_CREDITO_B, etc.)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoComprobante tipoComprobante;

    /**
     * Número completo del comprobante.
     * Ejemplo: "0001-00000023"
     */
    private String nroComprobante;

    /**
     * Relación uno a uno con la factura que anula.
     */
    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_factura", nullable = false, unique = true)
    private Factura factura;

    /**
     * Antes de persistir, genera el número de comprobante completo.
     */
    @PrePersist
    public void generarNumeroComprobante() {
        this.nroComprobante = String.format("%s-%08d", PUNTO_VENTA, idNotaCredito);
    }
}
