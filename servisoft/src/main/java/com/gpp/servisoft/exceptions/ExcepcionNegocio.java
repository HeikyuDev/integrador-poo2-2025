package com.gpp.servisoft.exceptions;

/**
 * Excepción personalizada para errores de lógica de negocio.
 * Se lanza cuando ocurre una violación de las reglas de negocio de la aplicación.
 * 
 * Ejemplos:
 * - No hay cuentas activas para facturar
 * - Factura no existe
 * - Método de pago no seleccionado
 * - Servicios sin estado pendiente
 */
public class ExcepcionNegocio extends RuntimeException {
    
    /**
     * Constructor con mensaje de error.
     * 
     * @param mensaje descripción del error de negocio
     */
    public ExcepcionNegocio(String mensaje) {
        super(mensaje);
    }
    
    /**
     * Constructor con mensaje de error y causa.
     * 
     * @param mensaje descripción del error de negocio
     * @param causa excepción que causó este error
     */
    public ExcepcionNegocio(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
