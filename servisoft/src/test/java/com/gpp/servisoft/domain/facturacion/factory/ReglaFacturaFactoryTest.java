package com.gpp.servisoft.domain.facturacion.factory;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.gpp.servisoft.domain.facturacion.strategy.ReglaEmisorExento;
import com.gpp.servisoft.domain.facturacion.strategy.ReglaEmisorMonotributo;
import com.gpp.servisoft.domain.facturacion.strategy.ReglaEmisorRI;
import com.gpp.servisoft.domain.facturacion.strategy.ReglaFacturaStrategy;
import com.gpp.servisoft.model.enums.CondicionFrenteIVA;

/**
 * Tests unitarios para ReglaFacturaFactory.
 * 
 * Valida que la factory retorne la estrategia correcta
 * según la condición del emisor.
 */
@DisplayName("Factory: Selección de Estrategia Fiscal")
class ReglaFacturaFactoryTest {

    @Test
    @DisplayName("Para RI debe retornar ReglaEmisorRI")
    void testGetStrategy_ResponsableInscripto_RetornaReglaRI() {
        // Act
        ReglaFacturaStrategy resultado = ReglaFacturaFactory
            .getStrategy(CondicionFrenteIVA.RESPONSABLE_INSCRIPTO);

        // Assert
        assertNotNull(resultado, "Factory no debe retornar null");
        assertInstanceOf(ReglaEmisorRI.class, resultado, 
            "Debe retornar una instancia de ReglaEmisorRI");
    }

    @Test
    @DisplayName("Para Monotributista debe retornar ReglaEmisorMonotributo")
    void testGetStrategy_Monotributista_RetornaReglaMonotributo() {
        // Act
        ReglaFacturaStrategy resultado = ReglaFacturaFactory
            .getStrategy(CondicionFrenteIVA.MONOTRIBUTISTA);

        // Assert
        assertNotNull(resultado, "Factory no debe retornar null");
        assertInstanceOf(ReglaEmisorMonotributo.class, resultado, 
            "Debe retornar una instancia de ReglaEmisorMonotributo");
    }

    @Test
    @DisplayName("Para Exento debe retornar ReglaEmisorExento")
    void testGetStrategy_Exento_RetornaReglaExento() {
        // Act
        ReglaFacturaStrategy resultado = ReglaFacturaFactory
            .getStrategy(CondicionFrenteIVA.EXENTO);

        // Assert
        assertNotNull(resultado, "Factory no debe retornar null");
        assertInstanceOf(ReglaEmisorExento.class, resultado, 
            "Debe retornar una instancia de ReglaEmisorExento");
    }

    @Test
    @DisplayName("Para condición no soportada debe lanzar excepción")
    void testGetStrategy_CondicionNoSoportada_LanzaExcepcion() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            ReglaFacturaFactory.getStrategy(CondicionFrenteIVA.CONSUMIDOR_FINAL),
            "Debe lanzar excepción para condición no soportada");
    }

    @Test
    @DisplayName("Para condición nula debe lanzar NullPointerException")
    void testGetStrategy_Nulo_LanzaNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> 
            ReglaFacturaFactory.getStrategy(null),
            "No debe permitir condición nula");
    }

    @Test
    @DisplayName("Instancias retornadas deben ser nuevas (no singleton)")
    void testGetStrategy_RetornaNuevasInstancias() {
        // Act
        ReglaFacturaStrategy instancia1 = ReglaFacturaFactory
            .getStrategy(CondicionFrenteIVA.RESPONSABLE_INSCRIPTO);
        ReglaFacturaStrategy instancia2 = ReglaFacturaFactory
            .getStrategy(CondicionFrenteIVA.RESPONSABLE_INSCRIPTO);

        // Assert
        assertNotSame(instancia1, instancia2, 
            "Debe crear nuevas instancias cada vez");
    }
}
