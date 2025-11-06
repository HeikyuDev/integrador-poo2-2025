package com.gpp.servisoft.model.entities;

import java.util.List;

import com.gpp.servisoft.model.enums.CondicionFrenteIVA;
import com.gpp.servisoft.model.enums.Estado;

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
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cuentas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private int idCuenta;

    @NotBlank(message = "El domicilio fiscal no puede estar vacío")
    @Size(max = 50, message = "El domicilio fiscal no debe superar los 50 caracteres")
    @Column(nullable = false, length = 50)
    private String domicilioFiscal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CondicionFrenteIVA condicionFrenteIVA;

    @NotBlank(message = "El CUIT no puede estar vacío")
    @Pattern(regexp = "^[0-9]{11}$", message = "El CUIT debe tener 11 dígitos numéricos")
    @Column(nullable = false, unique = true, length = 11)
    private String cuit;

    @NotBlank(message = "La razón social no puede estar vacía")
    @Size(max = 50, message = "La razón social no debe superar los 50 caracteres")
    @Column(nullable = false, length = 50)
    private String razonSocial;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.ACTIVO;

    // Asociación con Cliente (1 cliente * cuentas)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    // Una cuenta tiene uno o muchos serviciosSeleccionados
    @OneToMany(mappedBy="cuenta", cascade=CascadeType.ALL, orphanRemoval=true) // si elimino un servicio de la lista de serviciosdelacuenta, automaticamente jpa la elimina de la bd
    @Valid
    private List<ServicioDeLaCuenta> serviciosDeLaCuenta;
}