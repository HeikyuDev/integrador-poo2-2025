package com.gpp.servisoft.model.entities;

import com.gpp.servisoft.model.enums.Estado;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "servicios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

/**
 * Entidad que representa un servicio ofrecido por la empresa.
 *
 * Contiene información de presentación (nombre y descripción),
 * datos económicos (monto y alícuota) y metadatos como estado,
 * si admite cantidad y periodicidad.
 */
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private int idServicio;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 50, message = "El nombre no debe superar los 50 caracteres")
    @Column(nullable = false, length = 50)
    private String nombreServicio;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(max = 50, message = "La descripción no debe superar los 50 caracteres")
    @Column(nullable = false, length = 50)
    private String descripcionServicio;

    @NotNull(message = "El monto no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = true, message = "El monto no puede ser negativo")
    @Column(nullable = false)
    private Double montoServicio;

    /**
     * Alícuota aplicada al servicio (por ejemplo 0.21 para 21%).
     * Debe ser un número no negativo y razonablemente acotado.
     */
    @NotNull(message = "La alícuota no puede ser nula")
    @DecimalMin(value = "0.0", inclusive = true, message = "La alícuota no puede ser negativa")
    @DecimalMax(value = "1.0", inclusive = true, message = "La alícuota no puede ser mayor a 1.0")
    @Column(nullable = false)
    private Double alicuota;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Estado estado = Estado.ACTIVO;

    private boolean tieneCantidad;


}
