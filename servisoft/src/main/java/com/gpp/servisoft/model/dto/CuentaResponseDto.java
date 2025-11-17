package com.gpp.servisoft.model.dto;

import com.gpp.servisoft.model.enums.CondicionFrenteIVA;
import com.gpp.servisoft.model.enums.Estado;

/**
 * DTO para la respuesta de creación/actualización de cuenta
 * Evita problemas de serialización al no incluir relaciones lazy
 */
public class CuentaResponseDto {
    private int idCuenta;
    private String razonSocial;
    private String cuit;
    private CondicionFrenteIVA condicionFrenteIVA;
    private String domicilioFiscal;
    private Estado estado;

    public CuentaResponseDto(int idCuenta, String razonSocial, String cuit, 
                            CondicionFrenteIVA condicionFrenteIVA, String domicilioFiscal, 
                            Estado estado) {
        this.idCuenta = idCuenta;
        this.razonSocial = razonSocial;
        this.cuit = cuit;
        this.condicionFrenteIVA = condicionFrenteIVA;
        this.domicilioFiscal = domicilioFiscal;
        this.estado = estado;
    }

    // Getters
    public int getIdCuenta() {
        return idCuenta;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public String getCuit() {
        return cuit;
    }

    public CondicionFrenteIVA getCondicionFrenteIVA() {
        return condicionFrenteIVA;
    }

    public String getDomicilioFiscal() {
        return domicilioFiscal;
    }

    public Estado getEstado() {
        return estado;
    }
}
