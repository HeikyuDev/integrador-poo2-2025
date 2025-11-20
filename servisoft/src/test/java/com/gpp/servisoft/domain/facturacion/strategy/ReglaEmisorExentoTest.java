package com.gpp.servisoft.domain.facturacion.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.gpp.servisoft.model.enums.CondicionFrenteIVA;
import com.gpp.servisoft.model.enums.TipoComprobante;

/**
 * Tests unitarios para ReglaEmisorExento.
 * 
 * Valida la lógica fiscal cuando el EMISOR es Exento.
 * 
 * Regla AFIP:
 * - Siempre corresponde Factura C (sin discriminación de IVA)
 */
@DisplayName("Regla Fiscal: Emisor Exento")
class ReglaEmisorExentoTest {

    private ReglaEmisorExento regla = new ReglaEmisorExento();

    @Test
    @DisplayName("Exento a RI debe emitir Factura C")
    void testExentoARI_RetornaFacturaC() {
        // Arrange
        CondicionFrenteIVA emisor = CondicionFrenteIVA.EXENTO;
        CondicionFrenteIVA receptor = CondicionFrenteIVA.RESPONSABLE_INSCRIPTO;

        // Act
        TipoComprobante resultado = regla.determinar(emisor, receptor);

        // Assert
        assertEquals(TipoComprobante.C, resultado);
    }

    @Test
    @DisplayName("Exento a Monotributista debe emitir Factura C")
    void testExentoAMonotributa_RetornaFacturaC() {
        // Arrange
        CondicionFrenteIVA emisor = CondicionFrenteIVA.EXENTO;
        CondicionFrenteIVA receptor = CondicionFrenteIVA.MONOTRIBUTISTA;

        // Act
        TipoComprobante resultado = regla.determinar(emisor, receptor);

        // Assert
        assertEquals(TipoComprobante.C, resultado);
    }

    @Test
    @DisplayName("Exento a Exento debe emitir Factura C")
    void testExentoAExento_RetornaFacturaC() {
        // Arrange
        CondicionFrenteIVA emisor = CondicionFrenteIVA.EXENTO;
        CondicionFrenteIVA receptor = CondicionFrenteIVA.EXENTO;

        // Act
        TipoComprobante resultado = regla.determinar(emisor, receptor);

        // Assert
        assertEquals(TipoComprobante.C, resultado);
    }

    @Test
    @DisplayName("Exento a Consumidor Final debe emitir Factura C")
    void testExentoAConsumidorFinal_RetornaFacturaC() {
        // Arrange
        CondicionFrenteIVA emisor = CondicionFrenteIVA.EXENTO;
        CondicionFrenteIVA receptor = CondicionFrenteIVA.CONSUMIDOR_FINAL;

        // Act
        TipoComprobante resultado = regla.determinar(emisor, receptor);

        // Assert
        assertEquals(TipoComprobante.C, resultado);
    }

    @Test
    @DisplayName("Exento a No Responsable debe emitir Factura C")
    void testExentoANoResponsable_RetornaFacturaC() {
        // Arrange
        CondicionFrenteIVA emisor = CondicionFrenteIVA.EXENTO;
        CondicionFrenteIVA receptor = CondicionFrenteIVA.NO_RESPONSABLE;

        // Act
        TipoComprobante resultado = regla.determinar(emisor, receptor);

        // Assert
        assertEquals(TipoComprobante.C, resultado);
    }

    @Test
    @DisplayName("Receptor nulo es manejado (regla siempre retorna C)")
    void testReceptorNulo_SiempreRetornaC() {
        // La regla Exento no valida entrada, siempre retorna C
        TipoComprobante resultado = regla.determinar(CondicionFrenteIVA.EXENTO, null);
        assertEquals(TipoComprobante.C, resultado);
    }
}
