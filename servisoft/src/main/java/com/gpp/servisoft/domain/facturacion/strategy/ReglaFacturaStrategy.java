package com.gpp.servisoft.domain.facturacion.strategy;

import com.gpp.servisoft.model.enums.CondicionFrenteIVA;
import com.gpp.servisoft.model.enums.TipoComprobante;

/**
 * Contrato para determinar el tipo de comprobante AFIP
 * según la condición del emisor y del receptor.
 */
public interface ReglaFacturaStrategy {

    /**
     * Determina el tipo de comprobante aplicando las reglas AFIP.
     *
     * @param emisor   condición fiscal del emisor (empresa)
     * @param receptor condición fiscal del cliente
     * @return tipo de comprobante correspondiente
     */
    TipoComprobante determinar(CondicionFrenteIVA emisor, CondicionFrenteIVA receptor);
}