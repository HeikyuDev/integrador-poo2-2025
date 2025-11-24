package com.gpp.servisoft.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gpp.servisoft.model.enums.EstadoServicio;

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
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "servicios_de_la_cuenta")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicioDeLaCuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private int idServicioDeLaCuenta;

    // Relación con Cuenta (muchos servicios pertenecen a una cuenta)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cuenta", nullable=false)
    private Cuenta cuenta;

    // Relación con Servicio (muchas cuentas pueden usar el mismo servicio)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_servicio", nullable = false)
    private Servicio servicio;


    /**
     * Cantidad, la cual desea (A modo de preferencia) Facturar un Determinado servicio
     * Utilidad: (Utilizar ested valor en la facturacion Masiva, para evitar la Facturacion Individual)
     */
    @Column(nullable = false)
    @NotNull(message = "La cantidad de preferencia es Obligatorio!!!")
    @Builder.Default
    private int cantidadDePreferencia = 1; // 1 por defecto

    /**
     * Estado en el que está actualmente el servicio que está asociado a una determinada
     * Cuenta
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "El Estado del servicio es obligatorio!!!")
    @Builder.Default
    private EstadoServicio estadoServicio = EstadoServicio.PENDIENTE;
}