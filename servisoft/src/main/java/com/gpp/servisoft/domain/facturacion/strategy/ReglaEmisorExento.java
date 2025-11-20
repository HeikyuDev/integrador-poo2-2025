package com.gpp.servisoft.domain.facturacion.strategy;

import com.gpp.servisoft.model.enums.CondicionFrenteIVA;
import com.gpp.servisoft.model.enums.TipoComprobante;

/**
 * Estrategia fiscal cuando el Emisor es Exento.
 *
 * Regla AFIP:
 *  - Siempre corresponde Factura C
 */
public class ReglaEmisorExento implements ReglaFacturaStrategy {

    @Override
    public TipoComprobante determinar(CondicionFrenteIVA emisor, CondicionFrenteIVA receptor) {
        return TipoComprobante.C;
    }
}
