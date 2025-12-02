# Trabajo en equipo
Para el desarrollo de la iteracion 1 se dividió el trabajo de la siguiente manera:

### **Gallo Guillermo**
- Modulo de Cuentas

### **Pergher Lucas Maurice**
- Modulo de Clientes y Servicios

### **Pedernera Theisen Nahuel Thomas**
- Modulo de Facturacion y Pagos

# Diseño OO
![image](https://github.com/HeikyuDev/integrador-poo2-2025/blob/main/docs/iteracion%201/img/DiagramaClasesPOOII.jpg?raw=true)

---
# Wireframe y caso de uso

![image](https://github.com/HeikyuDev/integrador-poo2-2025/blob/main/docs/iteracion%201/img/Modulo-Cuentas/nueva-cuenta.png)

# Alta de Cuenta

*   **ID:** CU-001.
*   **Descripción:** En este caso de uso, el administrador crea una nueva cuenta vinculada a un cliente existente, registrando sus datos fiscales para futuras operaciones de facturación.
*   **Actor(es):** *Administrador*.

## Precondiciones
* Debe existir al menos un cliente registrado en el sistema.

## Flujo principal de eventos
1. El administrador accede a la opción **Crear cuenta**.
2. El sistema redirige a una nueva página que le permite cargar los campos necesarios.
3. El administrador selecciona el cliente existente al que se asociará la cuenta.
4. El administrador ingresa:
    * CUIT
    * Razón social
    * Condición frente al IVA
    * Domicilio fiscal
5. Tras haber llenado los campos del formulario, el administrador pulsa el botón **Guardar**.
6. El sistema valida que todos los campos estén completos.
7. El sistema, tras haber validado los datos, muestra un mensaje de confirmación indicando que la cuenta fue creada exitosamente y registra la cuenta con estado **Activo**.
8. Se termina el caso de uso.

## Flujos alternativos
*   **Si hay campos faltantes o con errores:**
    * El sistema avisa al administrador mediante una notificación emergente debajo del campo incorrecto o faltante.
    * El administrador completa el campo faltante o lo corrige.
    * Se retorna al paso 4 de la secuencia normal.

## Poscondiciones
* Se crea el registro de una nueva cuenta en el sistema.

---

![image](https://github.com/HeikyuDev/integrador-poo2-2025/blob/main/docs/iteracion%201/img/Modulo-Cuentas/listado-cuentas.png)

# Consulta de cuentas

*   **ID:** CU-002.
*   **Descripción:** En este caso de uso, el administrador visualiza todas las cuentas registradas en el sistema, con la posibilidad de filtrar según estado.
*   **Actor(es):** *Administrador*.

## Precondiciones
* Deben existir cuentas registradas previamente.

## Flujo principal de eventos
1. El administrador accede a la opción **Cuentas**.
2. El sistema muestra el listado completo de cuentas registradas.
3. El administrador puede aplicar filtros por estado:
    * Activa
    * Suspendida
    * Inactiva
    * Todas
4. El sistema muestra el listado filtrado con:
    * ID
    * CUIT
    * Razón social
    * Condición frente al IVA
    * Domicilio fiscal
    * Estado
5. Se termina el caso de uso.

---

# Modificar Cuenta

*   **ID:** GC-003.
*   **Descripción:** En este caso de uso, el administrador modifica una cuenta ya creada, editando datos de la cuenta o los servicios asociados de la misma.
*   **Actor(es):** *Administrador*.

## Precondiciones
* La cuenta debe haber sido previamente registrada.

## Flujo principal de eventos
1. El administrador selecciona la opción para editar una cuenta del listado.
2. El sistema muestra los campos editables:
    * Razón social
    * Condición frente al IVA
    * Domicilio fiscal
3. El administrador realiza las modificaciones necesarias.
4. El administrador selecciona o deselecciona servicios asociados.
5. Si selecciona un servicio con cantidad variable, el sistema solicita ingresarla.
6. El administrador confirma los cambios.
7. El sistema valida que todos los campos estén completos.
8. El sistema actualiza los datos y asociaciones.
9. El sistema muestra un mensaje de confirmación.
10. Se termina el caso de uso.

## Flujos alternativos
*   **Si hay campos faltantes o con errores:**
    * El sistema avisa al administrador mediante una notificación emergente debajo del campo incorrecto o faltante.
    * El administrador completa el campo faltante o lo corrige.
    * Se retorna al paso 3 de la secuencia normal.

## Poscondiciones
* Se actualiza el registro de una cuenta.
* Los servicios agregados o eliminados quedan reflejados.
* Se registra la preferencia o cantidad cuando aplique.

---

# Baja de Cuenta

*   **ID:** CU-003.
*   **Descripción:** En este caso de uso, el administrador da de baja una cuenta para impedir su uso en facturación, manteniendo su información histórica.
*   **Actor(es):** *Administrador*.

## Precondiciones
* La cuenta debe haber sido previamente registrada.
* La cuenta debe estar en estado **Activo** o **Suspendido**

## Flujo principal de eventos
1. El administrador selecciona la cuenta a dar de baja y presiona la opción para editar.
2. El sistema muestra los datos de la cuenta, sus servicios asociados y el botón **Marcar como inactivo**.
3. El administrador presiona el botón.
4. El sistema pregunta si desea confirmar la operación.
5. El administrador confirma la operación.
6. El sistema actualiza el estado a Inactiva.
7. El sistema muestra mensaje de baja exitosa.
8. Se termina el caso de uso.

## Flujos alternativos
*   **Si la cuenta ya está inactiva:**
    * El sistema impide la acción y notifica al administrador.

## Poscondiciones
* La cuenta pasa a estado Inactiva.
* No puede usarse en procesos de facturación.

---

# Facturacion Individual

## Wireframe
![image](https://github.com/HeikyuDev/integrador-poo2-2025/blob/main/docs/iteracion%201/img/Modulo-Facturacion/Mockup-Facturacion-Individual.jpg?raw=true)

## Caso de Uso

*   **ID:** CU-005.
* **Descripción:** En este caso de uso, el *administrador* selecciona una **cuenta de cliente** y un **período** a facturar. El sistema muestra los servicios pendientes de esa cuenta. El administrador selecciona los servicios, ajusta las cantidades si es necesario, y genera la factura.
*   **Actor(es):** *Administrador*.

## Precondiciones
- La cuenta del cliente debe existir y estar en estado ACTIVO en el sistema.
- Los servicios asociados a dicha cuenta deben estar registrados, en estado ACTIVO (no dados de baja), y tener un estado de facturación PENDIENTE.

## Flujo principal de eventos
1. Este caso de uso inicia cuando el administrador presiona la sección Facturación Individual en la barra de navegación. O en la seccion de atajos del home
2. El sistema redirige al administrador a la pantalla de "Facturación Individual".
3. El administrador selecciona una Cuenta activa desde el primer menú desplegable.
4. El sistema recarga la página (debido al evento onchange) y muestra en una tabla todos los servicios asociados a esa cuenta que están en estado PENDIENTE.
5. El administrador selecciona un Período del segundo menú desplegable (ej: MENSUAL, TRIMESTRAL).
6. El administrador marca los checkboxes "Facturar" de los servicios que desea incluir y, si corresponde, ajusta la Cantidad de cada uno.
7. El administrador presiona el botón FACTURAR.
8. El sistema valida los datos (que se haya seleccionado un período y al menos un servicio).
9. El sistema procesa la solicitud: crea la Factura, los DetallesFactura (con sus montos, IVA, etc.), genera los datos históricos (como DatosClienteFactura) y actualiza el estado de los ServicioDeLaCuenta a "FACTURADO".
10. El sistema redirige a la misma página y muestra una notificación de éxito: "Facturación procesada correctamente". Se finaliza el caso de uso.


## Flujos alternativos
*   **Si el cliente no tiene servicios asociadas en estado Pendiente:**
    *   El sistema le notifica que no tiene Servicios Pendientes.
* **Si el cliente no selecciona ningun servicio y preciona facturar:**
    *   El sistema le notifica que debe seleccionar almenos un servicio para poder realizar la facturacion individual

## Poscondiciones
* El sistema crea una instancia de Factura, con uno/varios detalles asociadas a la misma
* El sistema cambia el estado de los servicios seleccionados a **FACTURADO**

---

# Facturacion Masiva

## Wireframe
![image](https://github.com/HeikyuDev/integrador-poo2-2025/blob/main/docs/iteracion%201/img/Modulo-Facturacion/Mockup-Facturacion-Masiva.jpg?raw=true)

## Caso de Uso

*   **ID:** CU-006.
* **Descripción:** En este caso de uso, el *administrador*, selecciona un periodo de Facturacion, y preciona el boton *Facturacion Masiva* para facturar a cada cuenta que este en el estado *Activo*.
*   **Actor(es):** *Administrador*.

## Precondiciones
- Las cuentas deben estar registradas en el sistema y en estado *Activo*
- Los servicios asociados a dicha cuenta deben estar registrados, en estado ACTIVO, y tener un estado de facturación PENDIENTE.

## Flujo principal de eventos
1. Este caso de uso inicia cuando el administrador presiona la sección **Facturación Masiva** en la barra de navegación o en la sección de atajos del Home.
2. El sistema redirige al administrador a la pantalla de **Facturación Masiva**.
3. El administrador selecciona un **Período** del menú desplegable (ej: MENSUAL, TRIMESTRAL, SEMESTRAL, ANUAL).
4. El administrador presiona el botón **FACTURACIÓN MASIVA**.
5. El sistema valida que se haya seleccionado un período y que exista al menos una **Cuenta** en estado **ACTIVO** que contenga **ServicioDeLaCuenta** en estado **PENDIENTE**.
6. El sistema procesa la solicitud: obtiene todas las cuentas activas, itera sobre cada una, agrupa todos los servicios pendientes, crea una factura única por cuenta con sus detalles (calculando IVA, montos y guardando datos históricos), y actualiza el estado de los servicios procesados a **FACTURADO**.
7. El sistema crea un registro de facturación masiva que contiene la fecha de emisión, la cantidad total de facturas generadas y el monto total recaudado.
8. Al finalizar, el sistema redirige a la misma página y muestra una notificación de éxito: "Facturación Masiva procesada correctamente". Se finaliza el caso de uso.

## Flujos alternativos
*   **Si no hay cuentas activas con servicios pendientes:**
    * El sistema le notifica que no hay cuentas con servicios pendiente de facturación.

## Poscondiciones
* El sistema crea una instancia de facturación masiva donde guarda la fecha de emisión, la cantidad de facturas generadas y el monto total.
* El sistema cambia el estado de todos los servicios procesados a **FACTURADO**. 
---

# Pago Total

## Wireframe
![image](https://github.com/HeikyuDev/integrador-poo2-2025/blob/main/docs/iteracion%201/img/Modulo-Facturacion/Mockup-Pago-total.jpg?raw=true)

## Caso de Uso

*   **ID:** CU-007.
*   **Descripción:** En este caso de uso, el *administrador* selecciona una **factura pendiente de pago** y registra el pago total de la misma indicando el **método de pago** utilizado.
*   **Actor(es):** *Administrador*.

## Precondiciones
* Debe existir al menos una factura en estado PENDIENTE en el sistema.
* La factura no debe haber sido pagada totalmente con anterioridad.

## Flujo principal de eventos
1. El administrador, estando en la sección de gestión de pagos, accede a la opción **Ver Pagos** o **Gestionar Pagos** en la barra de navegación.
2. El sistema redirige al administrador a la pantalla de **Pagos**, que muestra un listado de todas las facturas pendientes de pago.
3. El administrador puede filtrar por **Cuenta** o por **Número de Comprobante** para encontrar la factura que desea pagar.
4. El administrador selecciona una factura del listado presionando el botón **TOTAL**.
5. El sistema redirige a una página que muestra los detalles de la factura (número, monto total, fecha de emisión, etc.).
6. El administrador selecciona el **Método de Pago** del menú desplegable (ej: EFECTIVO, TARJETA_CREDITO, TRANSFERENCIA, CHEQUE).
7. El administrador presiona el botón **PROCESAR PAGO**.
8. El sistema valida que se haya seleccionado un método de pago y que la factura sea válida.
9. El sistema procesa el pago: crea un registro de pago con el monto total de la factura, la fecha actual, el método de pago seleccionado y el tipo de pago TOTAL.
10. El sistema redirige a la pantalla de Pagos y muestra una notificación de éxito: "Pago procesado exitosamente. Monto: $[monto]". Se finaliza el caso de uso.

## Flujos alternativos
*   **Si no hay facturas pendientes:**
    * El sistema muestra un mensaje indicando que no hay facturas pendientes de pago.
*   **Si el administrador no selecciona un método de pago:**
    * El sistema le notifica que debe seleccionar un método de pago para procesar el pago.

## Poscondiciones
* Se crea un registro de Pago en el sistema asociado a la factura.
* El pago registra la fecha actual, el monto pagado y el método de pago utilizado.

---

# Consulta de Facturacion

## Wireframe
![image](https://github.com/HeikyuDev/integrador-poo2-2025/blob/main/docs/iteracion%201/img/Modulo-Facturacion/Mockup-Facturacion-Consulta.jpg?raw=true)

## Caso de Uso

*   **ID:** CU-008.
*   **Descripción:** En este caso de uso, el *administrador* consulta y filtra el listado de todas las facturas emitidas en el sistema, pudiendo buscar por **Cuenta** o por **Número de Comprobante**.
*   **Actor(es):** *Administrador*.

## Precondiciones
* Debe existir al menos una factura en el sistema.

## Flujo principal de eventos
1. El administrador presiona la opción **Consulta de Facturación** en la barra de navegación.
2. El sistema redirige al administrador a la pantalla de **Consulta de Facturación**.
3. El sistema muestra un listado paginado de todas las facturas emitidas, ordenadas por fecha de emisión (más recientes primero).
4. El administrador puede utilizar los filtros disponibles: seleccionar una **Cuenta** del menú desplegable o ingresar un **Número de Comprobante** en el campo de búsqueda.
5. El sistema recarga la página con los filtros aplicados y muestra solo las facturas que coinciden con los criterios seleccionados.
6. El administrador puede navegar entre páginas utilizando los botones de paginación para ver más facturas.
7. El administrador selecciona una factura del listado presionando el botón **Ver** o haciendo clic en la fila correspondiente.
8. El sistema redirige a la página de **Detalle de Factura**, mostrando todos los datos de la factura seleccionada (número de comprobante, cliente, fecha, detalles, monto total, estado, etc.).
9. El administrador puede volver al listado presionando el botón **Volver a Consulta**.

## Flujos alternativos
*   **Si no hay facturas en el sistema:**
    * El sistema muestra un mensaje indicando que no hay facturas registradas.
*   **Si no hay resultados para los filtros aplicados:**
    * El sistema muestra un mensaje indicando que no se encontraron facturas que coincidan con los criterios de búsqueda.

## Poscondiciones
* El administrador visualiza el listado de facturas filtradas y paginadas.
* El administrador puede acceder al detalle de cualquier factura seleccionada.
---

# Modulo de Servicios

# Alta de Servicio

## Wireframe
![Lista de Servicios](img/Modulo-Servicios/formulario-servicio.png)

## Caso de uso

*   **ID:** CU-009.
*   **Descripción:** En este caso de uso, el administrador registra un nuevo servicio completando sus datos básicos, valores económicos (monto y alícuota) y su estado.
*   **Actor(es):** *Administrador*.

## Precondiciones
* No se requieren precondiciones.

## Flujo principal de eventos
1. El administrador ingresa a **Servicios** y presiona **Nuevo Servicio**.
2. El sistema redirige al formulario **Nuevo Servicio**.
3. El administrador completa los campos:
   * Nombre del servicio (obligatorio)
   * Descripción (opcional)
   * Monto ($) — numérico, mayor o igual a 0 (obligatorio)
   * Alícuota (IVA) — opciones: 0%, 10.5%, 21%, 27% (obligatorio)
   * Estado — ACTIVO o INACTIVO (obligatorio)
   * Admite cantidad variable — conmutador (opcional)
4. A medida que ingresa Monto y Alícuota, el sistema actualiza la **Vista previa del cálculo** (Monto base, IVA y Total).
5. El administrador presiona **Guardar Servicio**.
6. El sistema valida los datos obligatorios y formatos numéricos.
7. El sistema persiste el servicio, redirige al listado y muestra una notificación de éxito.
8. Se termina el caso de uso.

## Flujos alternativos
*   **Campos faltantes o inválidos:**
    * El sistema muestra mensajes de error por campo y/o un mensaje general.
    * El administrador corrige y reintenta guardar.

## Poscondiciones
* Se crea el registro del nuevo servicio con el estado seleccionado.
* La vista de listado queda disponible para administrar el servicio recién creado.

---

# Consulta de Servicios

## Wireframe
![Lista de Servicios](img/Modulo-Servicios/lista-servicios.png)

## Caso de uso

*   **ID:** CU-010.
*   **Descripción:** En este caso de uso, el administrador visualiza el listado de servicios y puede buscar por nombre y filtrar por estado (Activos, Inactivos, Todos).
*   **Actor(es):** *Administrador*.

## Precondiciones
* Debe existir al menos un servicio registrado en el sistema.

## Flujo principal de eventos
1. El administrador accede a **Servicios**.
2. El sistema muestra el **Listado de Servicios**, con acciones y filtros disponibles.
3. El administrador puede:
   * Escribir en **Buscar servicio…** para filtrar por nombre en la tabla.
   * Seleccionar **Estado** (ACTIVO, INACTIVO, ALL); el sistema aplica el filtro recargando la página con parámetros de consulta.
4. El administrador puede iniciar acciones desde la tabla: **Editar**, **Desactivar** (si corresponde) o **Nuevo Servicio** desde la cabecera.
5. Se termina el caso de uso.

## Flujos alternativos
*   **No hay servicios registrados:**
    * El sistema muestra el renglón informativo: “No hay servicios registrados”.
*   **No hay coincidencias con la búsqueda/filtro:**
    * El listado no muestra filas que coincidan con los criterios aplicados.

## Poscondiciones
* Ninguna. Se trata de una visualización y navegación.

---
# Modificar Servicio

## Caso de uso

*   **ID:** CU-011.
*   **Descripción:** En este caso de uso, el administrador edita los datos de un servicio registrado, incluyendo su monto, alícuota, estado y configuración de cantidad variable.
*   **Actor(es):** *Administrador*.

## Precondiciones
* El servicio debe estar previamente registrado.

## Flujo principal de eventos
1. En el **Listado de Servicios**, el administrador presiona **Editar** en la fila del servicio deseado.
2. El sistema muestra el formulario con los datos precargados y el encabezado **Editar Servicio**.
3. El administrador modifica los campos necesarios: Nombre, Descripción, Monto, Alícuota, Estado y/o **Admite cantidad variable**.
4. La **Vista previa del cálculo** se actualiza según Monto y Alícuota.
5. El administrador presiona **Actualizar Servicio**.
6. El sistema valida los campos (obligatoriedad y formatos numéricos).
7. El sistema guarda los cambios, redirige al listado y muestra una notificación de éxito.
8. Se termina el caso de uso.

## Flujos alternativos
*   **Campos faltantes o inválidos:**
    * El sistema muestra mensajes de error por campo y/o un mensaje general.
    * El administrador corrige y reintenta.
*  **Cancelar edición:**
    * El administrador presiona **Cancelar**.
    * El sistema redirige al listado sin guardar cambios.

## Poscondiciones
* Se actualizan los datos del servicio.
* Los cambios de estado se reflejan en el listado y reglas del sistema (servicios INACTIVOS no se podrán asignar a nuevas cuentas).

---

# Desactivar (Baja) Servicio

## Wireframe
![Lista de Servicios](img/Modulo-Servicios/lista-servicios.png)

## Caso de uso

*   **ID:** CU-012.
*   **Descripción:** En este caso de uso, el administrador desactiva un servicio para impedir nuevas asignaciones, manteniendo su información histórica.
*   **Actor(es):** *Administrador*.

## Precondiciones
* El servicio debe estar previamente registrado.
* El servicio no debe estar ya en estado **INACTIVO** (en ese caso, el botón no se muestra).

## Flujo principal de eventos
1. En el **Listado de Servicios**, el administrador presiona el botón **Desactivar** de la fila del servicio.
2. El sistema muestra un **modal de confirmación**, exhibiendo el nombre del servicio afectado.
3. El administrador confirma la operación presionando **Desactivar** en el modal.
4. El sistema procesa la solicitud (POST a `/servicios/eliminar/{id}`) y actualiza el estado del servicio a **INACTIVO**.
5. El sistema cierra el modal, recarga el listado y muestra una notificación de éxito.
6. Se termina el caso de uso.

## Flujos alternativos
*   **Cancelar en el modal:**
    * No se realiza ninguna modificación.
*   **Servicio ya inactivo:**
    * El botón **Desactivar** no se muestra en la tabla para ese registro.

## Poscondiciones
* El servicio queda en estado **INACTIVO**.
* No estará disponible para nuevas asignaciones.

---

# Modulo de Clientes

# Alta de Cliente

## Wireframe
![Lista de Servicios](img/Modulo-Clientes/formulario-cliente.png)

## Caso de uso

*   **ID:** CU-013.
*   **Descripción:** En este caso de uso, el administrador registra un nuevo cliente completando sus datos personales, de contacto y estado inicial.
*   **Actor(es):** *Administrador*.

## Precondiciones
* No se requieren precondiciones.

## Flujo principal de eventos
1. El administrador ingresa a la sección **Clientes** y presiona el botón **Nuevo Cliente**.
2. El sistema redirige al formulario **Nuevo Cliente**.
3. El administrador selecciona el **Tipo de Cliente**: Persona Física o Persona Jurídica.
4. El administrador completa los campos obligatorios:
   * Nombre completo
   * Dirección
   * Teléfono (formato argentino; se muestra prefijo +54 como ayuda visual)
   * Correo electrónico
5. El administrador selecciona el **Estado** inicial (ACTIVO, INACTIVO o SUSPENDIDO).
6. El administrador presiona **Guardar Cliente**.
7. El sistema valida que los campos obligatorios estén completos, el formato del teléfono y del correo electrónico sea válido y, de aplicar, que el correo no esté ya registrado.
8. El sistema persiste el cliente, redirige al listado y muestra una notificación de éxito.
9. Se termina el caso de uso.

## Flujos alternativos
*   **Campos faltantes o inválidos:**
    * El sistema muestra mensajes de error debajo de cada campo y/o un mensaje de error general.
    * El administrador corrige y reintenta guardar.
    * Se retorna al paso 3 del flujo principal.
*   **Correo ya existente:**
    * El sistema informa el conflicto mediante un mensaje de error general.
    * El administrador ingresa un correo distinto y reintenta.

## Poscondiciones
* Se crea el registro del nuevo cliente con el estado seleccionado.
* El correo electrónico queda reservado para ese cliente (unicidad).
---

# Consulta de Clientes

## Wireframe
![Lista de Clientes](img/Modulo-Clientes/lista-clientes.png)

## Caso de uso

*   **ID:** CU-014.
*   **Descripción:** En este caso de uso, el administrador visualiza el listado de clientes y puede filtrar por Estado (Activos, Inactivos, Suspendidos, Todos), por Tipo (Persona Física/Jurídica) y buscar por nombre.
*   **Actor(es):** *Administrador*.

## Precondiciones
* Debe existir al menos un cliente registrado en el sistema.

## Flujo principal de eventos
1. El administrador accede a la opción **Clientes**.
2. El sistema muestra el **Listado de Clientes** con acciones y filtros disponibles.
3. El administrador puede:
   * Escribir en el campo de **búsqueda** para filtrar por nombre (filtro inmediato en la tabla).
   * Seleccionar **Estado** (ACTIVO, INACTIVO, SUSPENDIDO, ALL); el sistema aplica filtros recargando la página con parámetros de consulta.
   * Seleccionar **Tipo** (FISICO, JURIDICO, ALL); el sistema aplica filtros recargando la página con parámetros de consulta.
4. El administrador puede iniciar acciones desde la tabla: **Editar** o **Suspender** (si corresponde), o **Nuevo Cliente** desde la cabecera.
5. Se termina el caso de uso.

## Flujos alternativos
*   **No hay clientes registrados:**
    * El sistema muestra un renglón informativo: “No hay clientes registrados”.
*   **No hay coincidencias con los filtros/búsqueda:**
    * El listado no muestra filas que coincidan con los criterios aplicados.

## Poscondiciones
* Ninguna. Se trata de una visualización y navegación.

---

# Modificar Cliente

## Caso de uso

*   **ID:** CU-015.
*   **Descripción:** En este caso de uso, el administrador edita los datos de un cliente ya registrado, incluyendo su estado.
*   **Actor(es):** *Administrador*.

## Precondiciones
* El cliente debe estar previamente registrado.

## Flujo principal de eventos
1. En el **Listado de Clientes**, el administrador presiona **Editar** en la fila del cliente deseado.
2. El sistema muestra el formulario con los datos precargados y el encabezado **Editar Cliente**.
3. El administrador modifica los campos necesarios (Tipo de cliente, Nombre, Dirección, Teléfono, Correo electrónico, Estado).
4. El administrador presiona **Actualizar Cliente**.
5. El sistema valida los campos (obligatoriedad, formatos y, de aplicar, unicidad del correo).
6. El sistema guarda los cambios, redirige al listado y muestra una notificación de éxito.
7. Se termina el caso de uso.

## Flujos alternativos
*   **Campos faltantes o inválidos:**
    * El sistema muestra mensajes de error por campo y/o un mensaje general.
    * El administrador corrige y reintenta.
*   **Correo usado por otro cliente:**
    * El sistema informa el conflicto mediante un mensaje de error general.
    * El administrador ajusta el dato y reintenta.

## Poscondiciones
* Se actualizan los datos del cliente.
* Los cambios de estado se reflejan en el listado y en las reglas del sistema (por ejemplo: clientes inactivos no podrán tener cuentas activas asociadas).

---

# Baja (Suspender) de Cliente

## Wireframe
![Lista de Clientes](img/Modulo-Clientes/lista-clientes.png)

## Caso de uso
*   **ID:** CU-016.
*   **Descripción:** En este caso de uso, el administrador suspende un cliente para impedir su uso en operaciones, manteniendo su información y permitiendo reactivación posterior.
*   **Actor(es):** *Administrador*.

## Precondiciones
* El cliente debe estar previamente registrado.
* El cliente no debe estar ya en estado **SUSPENDIDO** (en ese caso, el botón no se muestra).

## Flujo principal de eventos
1. En el **Listado de Clientes**, el administrador presiona el botón **Suspender** de la fila del cliente.
2. El sistema muestra un **modal de confirmación**, exhibiendo el nombre del cliente afectado.
3. El administrador confirma la operación presionando **Suspender** en el modal.
4. El sistema procesa la solicitud (POST a `/clientes/eliminar/{id}`) y actualiza el estado del cliente a **SUSPENDIDO**.
5. El sistema cierra el modal, recarga el listado y muestra una notificación de éxito.
6. Se termina el caso de uso.

## Flujos alternativos
*   **Cancelar en el modal:**
    * No se realiza ninguna modificación.
*   **Cliente ya suspendido:**
    * El botón **Suspender** no se muestra en la tabla para ese registro.

## Poscondiciones
* El cliente queda en estado **SUSPENDIDO**.
* El cliente puede reactivarse posteriormente desde **Editar Cliente** cambiando su estado a **ACTIVO**.

---

# Backlog de iteraciones

* Las historias de usuario que se van a implementar en esta iteracion son:

### 1. Gestión de clientes
- **Como** administrador
- **Quiero** registrar, consultar, editar y dar de baja clientes que estén registrados en el sistema
- **Para** asociar cuentas a los clientes registrados, mantener su registro actualizado, emitir facturas solo a clientes activos y disponer de su información fiscal y de contacto para las operaciones.

### 2. Gestión de servicios
- **Como** administrador
- **Quiero** registrar, consultar, editar y dar de baja servicios que estén registrados en el sistema
- **Para** contar con un catálogo de servicios actualizado con sus costos y alícuotas de IVA correctas, para brindar la facturación precisa de los mismos.

### 3. Gestión de cuentas
- **Como** administrador
- **Quiero** registrar, consultar, modificar y dar de baja las cuentas de los clientes
- **Para** vincular los servicios contratados al cliente, mantener su estado actualizado (activo o inactivo), impidiendo que las cuentas dadas de baja (inactivas) sean incluidas en la facturación.


### 4. Facturación individual
- **Como** administrador
- **Quiero** emitir una factura individual seleccionando servicios de una cuenta
- **Para** registrar los servicios contratados y permitir su cobro posterior.

### 5. Registro de pago total
- **Como** administrador 
- **Quiero** registrar un pago que cancele completamente una factura
- **Para** reflejar que ha sido pagada en su totalidad.

### 6. Facturación Masiva
- **Como** administrador 
- **Quiero** generar facturas masivamente para todas las cuentas activas
- **Para** automatizar la facturación de servicios pendientes.

### 7. Consulta de facturación masiva
- **Como** administrador
- **Quiero** visualizar un listado de todos los procesos de facturación masiva realizados
- **Para** consultar el historial y verificar los resultados de cada proceso.

### 8. Consulta de facturas
- **Como** administrador
- **Quiero** visualizar un listado de todas las facturas emitidas con opciones de filtrado y acceso al detalle
- **Para** consultar y analizar el historial de facturación de los clientes.
