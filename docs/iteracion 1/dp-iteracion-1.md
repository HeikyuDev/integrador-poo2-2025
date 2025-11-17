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

![image](IMAGEN DEL WIREFRAME)

# Alta de Cuenta

*   **ID:** GC-001.
*   **Descripción:** En este caso de uso, el *administrador* ingresa los datos necesarios y selecciona un **Cliente previamente registrado** para realizar el alta de una nueva cuenta. 
*   **Actor(es):** *Administrador*.

## Precondiciones
* El cliente al cual se asociará la nueva cuenta, debe haber sido registrado previamente en el sistema.

## Flujo principal de eventos
1. El administrador, estando en la página inicial con el listado, selecciona la opción **+ Nueva cuenta**.
2. El sistema redirige a una nueva página que le permite cargar los campos necesarios.
3. El administrador selecciona o busca un cliente registrado en la opción **Cliente**, luego ingresa por teclado la razón social, el CUIT y domicilio fiscal que tendrá la nueva cuenta.
4. El administrador selecciona la condición frente al IVA de la cuenta.
5. Tras haber llenado los campos del formulario, el administrador pulsa el botón **Guardar**.
6. El sistema valida que los datos ingresados sean válidos y no haya campos vacíos.
7. El sistema, tras haber validado los datos, muestra un mensaje de confirmación indicando que la cuenta fue creada exitosamente.

## Flujos alternativos
*   **Si hay campos faltantes o con errores:**
    * El sistema avisa al administrador mediante una notificación emergente debajo del campo incorrecto o faltante.
    * El administrador completa el campo faltante o lo corrige.
    * Se retorna al paso 6 de la secuencia normal.
*   **Si [otra condición]:**
    *   [Describe otro flujo alternativo]

## Poscondiciones
* Se crea el registro de una nueva cuenta en el sistema.

---

# Facturacion Individual

## Wireframe
![image](https://github.com/HeikyuDev/integrador-poo2-2025/blob/main/docs/iteracion%201/img/Modulo-Facturacion/Mockup-Facturacion-Individual.jpg?raw=true)

## Caso de Uso

*   **ID:** GC-002.
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

*   **ID:** GC-003.
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

*   **ID:** GC-004.
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

*   **ID:** GC-005.
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
- **Para** vincular los servicios contratados al cliente, mantener su estado actualizado (activo o de baja), impidiendo que las cuentas dadas de baja sean incluidas en la facturación.

### 4. Facturación individual
- **Como** administrador
- **Quiero** emitir una factura individual y que el sistema calcule el IVA según la alícuota del servicio y la condición fiscal del cliente
- **Para** para registrar los servicios contratados y pendientes de pago, asegurando el cálculo correcto de impuestos.

### 5. Registro de pago total
- **Como** administrador 
- **Quiero** registrar el pago total de una determinada factura 
- **Para** reflejar correctamente las operaciones canceladas y evitar que facturas pagadas se consideren vencidas.

### 6. Facturación Masiva
- **Como** administrador 
- **Quiero** quiero presionar el botón **“FACTURAR MASIVO”** y que el sistema genere facturas de todos los servicios aún no facturados, correspondientes a **clientes activos** y que registre la cantidad total de facturas generadas y el monto total facturado.
- **Para** registrar automáticamente los servicios pendientes de pago dentro del sistema.


