package com.gpp.servisoft.domain.facturacion.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.gpp.servisoft.model.enums.CondicionFrenteIVA;
import com.gpp.servisoft.model.enums.TipoComprobante;

/**
 * Tests unitarios para ReglaEmisorMonotributo.
 * 
 * Valida la lógica fiscal cuando el EMISOR es Monotributista.
 * 
 * Regla AFIP:
 * - Siempre corresponde Factura C (sin discriminación de IVA)
 */
@DisplayName("Regla Fiscal: Emisor Monotributista")
class ReglaEmisorMonotributoTest {

    private ReglaEmisorMonotributo regla = new ReglaEmisorMonotributo();

    @Test
    @DisplayName("Monotributista a RI debe emitir Factura C")
    void testMonotributaARI_RetornaFacturaC() {
        // Arrange
        CondicionFrenteIVA emisor = CondicionFrenteIVA.MONOTRIBUTISTA;
        CondicionFrenteIVA receptor = CondicionFrenteIVA.RESPONSABLE_INSCRIPTO;

        // Act
        TipoComprobante resultado = regla.determinar(emisor, receptor);

        // Assert
        assertEquals(TipoComprobante.C, resultado);
    }

    @Test
    @DisplayName("Monotributista a Monotributista debe emitir Factura C")
    void testMonotributaAMonotributa_RetornaFacturaC() {
        // Arrange
        CondicionFrenteIVA emisor = CondicionFrenteIVA.MONOTRIBUTISTA;
        CondicionFrenteIVA receptor = CondicionFrenteIVA.MONOTRIBUTISTA;

        // Act
        TipoComprobante resultado = regla.determinar(emisor, receptor);

        // Assert
        assertEquals(TipoComprobante.C, resultado);
    }

    @Test
    @DisplayName("Monotributista a Exento debe emitir Factura C")
    void testMonotributaAExento_RetornaFacturaC() {
        // Arrange
        CondicionFrenteIVA emisor = CondicionFrenteIVA.MONOTRIBUTISTA;
        CondicionFrenteIVA receptor = CondicionFrenteIVA.EXENTO;

        // Act
        TipoComprobante resultado = regla.determinar(emisor, receptor);

        // Assert
        assertEquals(TipoComprobante.C, resultado);
    }

    @Test
    @DisplayName("Monotributista a Consumidor Final debe emitir Factura C")
    void testMonotributaAConsumidorFinal_RetornaFacturaC() {
        // Arrange
        CondicionFrenteIVA emisor = CondicionFrenteIVA.MONOTRIBUTISTA;
        CondicionFrenteIVA receptor = CondicionFrenteIVA.CONSUMIDOR_FINAL;

        // Act
        TipoComprobante resultado = regla.determinar(emisor, receptor);

        // Assert
        assertEquals(TipoComprobante.C, resultado);
    }

    @Test
    @DisplayName("Monotributista a No Responsable debe emitir Factura C")
    void testMonotributaANoResponsable_RetornaFacturaC() {
        // Arrange
        CondicionFrenteIVA emisor = CondicionFrenteIVA.MONOTRIBUTISTA;
        CondicionFrenteIVA receptor = CondicionFrenteIVA.NO_RESPONSABLE;

        // Act
        TipoComprobante resultado = regla.determinar(emisor, receptor);

        // Assert
        assertEquals(TipoComprobante.C, resultado);
    }

    @Test
    @DisplayName("Receptor nulo es manejado (regla siempre retorna C)")
    void testReceptorNulo_SiempreRetornaC() {
        // La regla Monotributo no valida entrada, siempre retorna C
        TipoComprobante resultado = regla.determinar(CondicionFrenteIVA.MONOTRIBUTISTA, null);
        assertEquals(TipoComprobante.C, resultado);
    }
}
