package com.gpp.servisoft.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "servicios_de_la_cuenta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicioDeLaCuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private int idServicioDeLaCuenta;

    @PositiveOrZero(message = "El precio actual no puede ser negativo")
    @Column(nullable = false)
    private Double precioActual;

    // Relación con Cuenta (muchos servicios pertenecen a una cuenta)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cuenta", nullable=false, unique = true)
    private Cuenta cuenta;

    // Relación con Servicio (uno a uno)
    @OneToOne(optional = false)
    @JoinColumn(name = "id_servicio", nullable = false, unique = true)
    private Servicio servicio;
}
