package com.gpp.servisoft.domain.facturacion.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.gpp.servisoft.model.enums.CondicionFrenteIVA;
import com.gpp.servisoft.model.enums.TipoComprobante;

/**
 * Tests unitarios para ReglaEmisorRI.
 * 
 * Valida la lógica fiscal cuando el EMISOR es Responsable Inscripto (RI).
 * 
 * Regla AFIP:
 * - Receptor RI → Factura A (IVA discriminado)
 * - Cualquier otro receptor → Factura B (IVA incluido)
 */
@DisplayName("Regla Fiscal: Emisor Responsable Inscripto")
class ReglaEmisorRITest {

    private ReglaEmisorRI regla = new ReglaEmisorRI();

    // CASO 1: Receptor es RI → Factura A
    @Test
    @DisplayName("Cuando receptor es RI, debe retornar Factura A")
    void testReceptorResponsableInscripto_RetornaFacturaA() {
        // Arrange
        CondicionFrenteIVA emisor = CondicionFrenteIVA.RESPONSABLE_INSCRIPTO;
        CondicionFrenteIVA receptor = CondicionFrenteIVA.RESPONSABLE_INSCRIPTO;

        // Act
        TipoComprobante resultado = regla.determinar(emisor, receptor);

        // Assert
        assertEquals(TipoComprobante.A, resultado, 
            "RI a RI debe emitir Factura A");
    }

    // CASO 2: Receptor es Monotributista → Factura B
    @Test
    @DisplayName("Cuando receptor es Monotributista, debe retornar Factura B")
    void testReceptorMonotributista_RetornaFacturaB() {
        // Arrange
        CondicionFrenteIVA emisor = CondicionFrenteIVA.RESPONSABLE_INSCRIPTO;
        CondicionFrenteIVA receptor = CondicionFrenteIVA.MONOTRIBUTISTA;

        // Act
        TipoComprobante resultado = regla.determinar(emisor, receptor);

        // Assert
        assertEquals(TipoComprobante.B, resultado, 
            "RI a Monotributista debe emitir Factura B");
    }

    // CASO 3: Receptor es Exento → Factura B
    @Test
    @DisplayName("Cuando receptor es Exento, debe retornar Factura B")
    void testReceptorExento_RetornaFacturaB() {
        // Arrange
        CondicionFrenteIVA emisor = CondicionFrenteIVA.RESPONSABLE_INSCRIPTO;
        CondicionFrenteIVA receptor = CondicionFrenteIVA.EXENTO;

        // Act
        TipoComprobante resultado = regla.determinar(emisor, receptor);

        // Assert
        assertEquals(TipoComprobante.B, resultado, 
            "RI a Exento debe emitir Factura B");
    }

    // CASO 4: Receptor es Consumidor Final → Factura B
    @Test
    @DisplayName("Cuando receptor es Consumidor Final, debe retornar Factura B")
    void testReceptorConsumidorFinal_RetornaFacturaB() {
        // Arrange
        CondicionFrenteIVA emisor = CondicionFrenteIVA.RESPONSABLE_INSCRIPTO;
        CondicionFrenteIVA receptor = CondicionFrenteIVA.CONSUMIDOR_FINAL;

        // Act
        TipoComprobante resultado = regla.determinar(emisor, receptor);

        // Assert
        assertEquals(TipoComprobante.B, resultado, 
            "RI a Consumidor Final debe emitir Factura B");
    }

    // CASO 5: Receptor es No Responsable → Factura B
    @Test
    @DisplayName("Cuando receptor es No Responsable, debe retornar Factura B")
    void testReceptorNoResponsable_RetornaFacturaB() {
        // Arrange
        CondicionFrenteIVA emisor = CondicionFrenteIVA.RESPONSABLE_INSCRIPTO;
        CondicionFrenteIVA receptor = CondicionFrenteIVA.NO_RESPONSABLE;

        // Act
        TipoComprobante resultado = regla.determinar(emisor, receptor);

        // Assert
        assertEquals(TipoComprobante.B, resultado, 
            "RI a No Responsable debe emitir Factura B");
    }

    // CASOS DE NULIDAD
    @Test
    @DisplayName("Receptor nulo no coincide con RI, retorna B")
    void testReceptorNulo_RetornaB() {
        // El if (receptor == RESPONSABLE_INSCRIPTO) es false cuando receptor es null
        // Por lo tanto retorna TipoComprobante.B
        TipoComprobante resultado = regla.determinar(CondicionFrenteIVA.RESPONSABLE_INSCRIPTO, null);
        assertEquals(TipoComprobante.B, resultado);
    }

    @Test
    @DisplayName("Emisor nulo no afecta la regla (se ignora)")
    void testEmisorNulo_SeIgnora() {
        // La regla RI no valida el emisor, solo el receptor
        TipoComprobante resultado = regla.determinar(null, CondicionFrenteIVA.RESPONSABLE_INSCRIPTO);
        assertEquals(TipoComprobante.A, resultado);
    }
}
