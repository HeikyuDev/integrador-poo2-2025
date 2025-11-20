package com.gpp.servisoft.domain.facturacion.strategy;

import com.gpp.servisoft.model.enums.CondicionFrenteIVA;
import com.gpp.servisoft.model.enums.TipoComprobante;

/**
 * Estrategia fiscal cuando el Emisor es Responsable Inscripto.
 *
 * Reglas:
 *  - Cliente RI → Factura A
 *  - Cliente Monotributo, Exento, Consumidor Final, No alcanzado → Factura B
 */
public class ReglaEmisorRI implements ReglaFacturaStrategy {

    @Override
    public TipoComprobante determinar(CondicionFrenteIVA emisor, CondicionFrenteIVA receptor) {

        if (receptor == CondicionFrenteIVA.RESPONSABLE_INSCRIPTO) {
            return TipoComprobante.A;
        }

        return TipoComprobante.B;
    }
}
