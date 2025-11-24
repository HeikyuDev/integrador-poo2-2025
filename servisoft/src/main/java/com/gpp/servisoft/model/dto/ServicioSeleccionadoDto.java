package com.gpp.servisoft.model.dto;

import lombok.Data;

/**
 * Clase dedicada a la transferencia de Datos entre objetos
 * Se usa @Data para que sea un DTO mutable que Spring puede usar en formularios.
 */
@Data
public class ServicioSeleccionadoDto {

    // --- CAMPOS QUE SE RECIBEN (POST) ---
    // (Datos que el usuario envía en el formulario)

    // 1. Vinculado a th:field="*{items[...].idServicio}" (el input hidden)
    private Integer idServicio;

    // 2. Vinculado a th:field="*{items[...].cantidad}" (el input number)
    private Integer cantidad;

    // 3. Vinculado a th:field="*{items[...].seleccionado}" (el checkbox)
    private boolean seleccionado;


    // --- CAMPOS SOLO PARA MOSTRAR (GET) ---

    private String nombreServicio;
    private Double montoUnitario;
    private boolean tieneCantidad;
    private Integer cantidadDePreferencia;



}