# Consulta de Pagos — Iteración 2

Basado en la vista `servisoft/src/main/resources/templates/pagos/consulta.html`.

---

## Wireframe
![image](https://github.com/HeikyuDev/integrador-poo2-2025/blob/main/docs/iteracion%202/img/Modulo-Pagos/Mockup-Pagos-Consulta.jpg)

## Caso de Uso

*   **ID:** GC-008.
*   **Descripción:** El administrador consulta el historial de pagos registrados aplicando filtros por cliente/cuenta, número de comprobante, rango de fechas y criterios de orden, visualizando resultados paginados.
*   **Actor(es):** *Administrador*.

## Precondiciones
* Debe existir al menos un pago registrado en el sistema.

## Flujo principal de eventos
1. El administrador accede a **Historial de Pagos** desde la navegación.
2. El sistema muestra la pantalla con el **formulario de filtros** y el **listado** de resultados.
3. El administrador configura uno o más filtros:
   * **Cliente / Cuenta** (`cuentaId`): selecciona una cuenta específica o “Todos los clientes”.
   * **Factura (Comprobante)** (`comprobante`): ingresa el número (ej.: 0001-00000001).
   * **Fecha desde** y **Fecha hasta** (`fechaDesde`, `fechaHasta`): establece el rango temporal.
   * **Ordenar por** (`orden`): elige `fechaDesc`, `fechaAsc`, `montoDesc` o `montoAsc`.
4. El administrador presiona **Aplicar filtros**.
5. El sistema valida parámetros, ejecuta la consulta y muestra el listado con columnas: **Fecha**, **Monto**, **Método**, **Cliente**, **Factura** (número e ID si corresponde).
6. Si los resultados superan el tamaño de página, el sistema muestra **paginación** con navegación: **Anterior**, páginas numeradas y **Siguiente**.
7. El administrador puede ajustar filtros y volver a aplicar para refinar la búsqueda.
8. Se termina el caso de uso.

## Flujos alternativos
*   **Sin resultados:**
    * El sistema muestra: “No se encontraron pagos con los filtros seleccionados”.
*   **Parámetros inválidos (formato de comprobante, fechas incoherentes):**
    * El sistema muestra un mensaje de error y no ejecuta la consulta hasta corregir los parámetros.

## Poscondiciones
* El administrador visualiza el listado de pagos filtrado y ordenado.
* El sistema mantiene los filtros y el orden seleccionados al navegar entre páginas.
