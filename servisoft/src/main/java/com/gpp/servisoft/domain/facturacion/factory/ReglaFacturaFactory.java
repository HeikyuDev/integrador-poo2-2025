package com.gpp.servisoft.domain.facturacion.factory;

import com.gpp.servisoft.domain.facturacion.strategy.ReglaEmisorExento;
import com.gpp.servisoft.domain.facturacion.strategy.ReglaEmisorMonotributo;
import com.gpp.servisoft.domain.facturacion.strategy.ReglaEmisorRI;
import com.gpp.servisoft.domain.facturacion.strategy.ReglaFacturaStrategy;
import com.gpp.servisoft.model.enums.CondicionFrenteIVA;

/**
 * Factory que selecciona la estrategia fiscal correspondiente
 * según la condición del Emisor.
 *
 * Permite escalar y agregar nuevas reglas sin modificar código existente.
 */
public class ReglaFacturaFactory {

    /**
     * Retorna la estrategia fiscal correspondiente al emisor.
     *
     * @param emisor condición fiscal del emisor
     * @return una estrategia ReglaFacturaStrategy
     */
    public static ReglaFacturaStrategy getStrategy(CondicionFrenteIVA emisor) {

        switch (emisor) {
            case RESPONSABLE_INSCRIPTO -> {
                return new ReglaEmisorRI();
            }
            case MONOTRIBUTISTA -> {
                return new ReglaEmisorMonotributo();
            }
            case EXENTO -> {
                return new ReglaEmisorExento();
            }
            default -> throw new IllegalArgumentException(
                    "Condición de emisor no soportada: " + emisor);
        }
    }
}
