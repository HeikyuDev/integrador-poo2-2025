# Trabajo en equipo
Para el desarrollo de la iteracion 2 se dividió el trabajo de la siguiente manera:

### **Gallo Guillermo**
- Refactorizacion y mejora del Modulo de Cuentas: agregadas las opciones *ver detalle de cuenta* y *suspender cuenta*.

### **Pergher Lucas Maurice**
- Refactorizacion y mejora del Modulo de Clientes y Servicios, Y desarrollo del Historial del Pagos

### **Pedernera Theisen Nahuel Thomas**
- Refactorizacion y mejora del Modulo de Facturacion y Pagos,  desarrollo de la anulacion de facturas (Notas de credito), y testing del modulo de Facturacion

# Diseño OO
![image](https://github.com/HeikyuDev/integrador-poo2-2025/blob/ce184cf4b9fa257d109dd1839452390f48a36ee8/docs/iteracion%202/img/DiagramaClasesPOOII.jpg)

---
# Anulación de Facturas

## Wireframe
![image](https://github.com/HeikyuDev/integrador-poo2-2025/blob/ce184cf4b9fa257d109dd1839452390f48a36ee8/docs/iteracion%202/img/Modulo-Facturacion/Mockup-Facturacion-Anulacion.jpg)

## Casos de Uso

*   **ID:** GC-006.
*   **Descripción:** En este caso de uso, el *administrador* selecciona una determinada factura procesada en estado PENDIENTE o PARCIALMENTE PAGADA y procede a anularla generando una nota de crédito como comprobante de dicha anulación, además de revertir los servicios asociados a estado PENDIENTE para permitir refacturación.
*   **Actor(es):** *Administrador*.

## Precondiciones
* La factura debe estar registrada en el sistema.
* La factura debe estar en estado PENDIENTE o PARCIALMENTE PAGADA.
* La factura no debe estar previamente anulada (sin nota de crédito asociada).
* Deben existir servicios asociados a la factura en estado FACTURADO.

## Flujo principal de eventos
1. El administrador, navegando por el sistema, accede a la sección **Anulación de Facturas** en la barra de navegación.
2. El sistema redirige al administrador a la pantalla de **Anulación de Facturas**, mostrando un listado paginado de todas las facturas en estado PENDIENTE o PARCIALMENTE PAGADAS.
3. El administrador puede filtrar por **Cuenta** o por **Número de Comprobante** para encontrar la factura que desea anular.
4. El administrador presiona el botón **Anular** en la factura seleccionada.
5. El sistema muestra una ventana modal de confirmación, solicitando al administrador que ingrese un **Motivo de Anulación**.
6. El administrador ingresa un motivo descriptivo (entre 10 y 100 caracteres) que explique la razón de la anulación.
7. El administrador presiona el botón **Confirmar Anulación** en el modal.
8. El sistema valida que el motivo cumpla con los requisitos (10-100 caracteres) y que la factura aún sea anulable.
9. El sistema anula la factura realizando las siguientes operaciones:
   - Crea una nueva instancia de **Nota de Crédito** asociada a la factura
   - Asigna a la nota de crédito el monto total de la factura
   - Registra la fecha actual como fecha de emisión de la nota de crédito
   - Determina el tipo de comprobante de la nota de crédito según la condición fiscal del cliente
   - Revierte el estado de todos los servicios asociados a la factura a **PENDIENTE** para permitir refacturación
10. El sistema persiste todos los cambios de forma transaccional (atómicamente).
11. El sistema redirige al administrador a la pantalla de **Anulación de Facturas** e informa que la anulación se realizó correctamente.

## Flujos alternativos
*   **Si la factura ya está anulada:**
    * El sistema muestra un mensaje de error indicando que no es posible anular una factura que ya tiene una nota de crédito asociada.

*   **Si el administrador no ingresa un motivo:**
    * El sistema muestra un error indicando que el motivo de anulación es obligatorio.

*   **Si el motivo tiene menos de 10 caracteres o más de 100:**
    * El sistema muestra un error indicando que el motivo debe contener entre 10 y 100 caracteres.

*   **Si no se selecciona una factura válida:**
    * El sistema muestra un error indicando que debe seleccionar una factura válida para continuar con la anulación.

*   **Si la factura no existe en el sistema:**
    * El sistema muestra un error indicando que la factura seleccionada no pudo ser encontrada en la base de datos.

*   **Si no hay facturas pendientes o parcialmente pagadas:**
    * El sistema muestra un mensaje indicando que no hay facturas disponibles para anular.

## Poscondiciones
* Se crea una nueva instancia de **Nota de Crédito** asociada a la factura anulada.
* La nota de crédito registra:
  - La fecha de emisión actual
  - El monto total igual al monto total de la factura
  - El motivo de anulación ingresado por el administrador
  - El tipo de comprobante según la condición fiscal del cliente (ej: Nota de Crédito A, B, C)
* Todos los servicios de la factura son revertidos a estado **PENDIENTE**, permitiendo su refacturación en futuras operaciones.
* La factura permanece en estado **PENDIENTE** o **PARCIALMENTE PAGADA**, pero ahora tiene asociada una nota de crédito que la marca como anulada.
* El sistema registra la transacción de anulación de forma atómica, garantizando la consistencia de los datos.

---
# Pago Parcial

## Wireframe
![image](https://github.com/HeikyuDev/integrador-poo2-2025/blob/main/docs/iteracion%201/img/Modulo-Facturacion/Mockup-Pago-total.jpg?raw=true)

## Caso de Uso

*   **ID:** GC-007.
*   **Descripción:** En este caso de uso, el *administrador* selecciona una determinada factura en estado PENDIENTE o PARCIALMENTE PAGADA y procede a registrar un pago parcial de la misma, ingresando un monto específico menor al saldo pendiente.
*   **Actor(es):** *Administrador*.

## Precondiciones
* Debe existir al menos una factura en estado PENDIENTE o PARCIALMENTE PAGADA en el sistema.
* La factura no debe haber sido pagada totalmente con anterioridad.
* Debe existir al menos un método de pago registrado en el sistema.

## Flujo principal de eventos
1. El administrador, estando en la sección de gestión de pagos, accede a la opción **Ver Pagos** o **Gestionar Pagos** en la barra de navegación.
2. El sistema redirige al administrador a la pantalla de **Pagos**, que muestra un listado de todas las facturas pendientes y parcialmente pagadas.
3. El administrador puede filtrar por **Cuenta** o por **Número de Comprobante** para encontrar la factura que desea pagar parcialmente.
4. El administrador presiona el botón **Pago Parcial** en la factura seleccionada.
5. El sistema redirige al administrador al formulario de **Pago Parcial**, mostrando los detalles de la factura (número de comprobante, cuenta, saldo pendiente).
6. El administrador ingresa el monto a pagar, que debe ser menor o igual al saldo pendiente de la factura.
7. El administrador selecciona el **Método de Pago** (transferencia, efectivo, cheque, etc.).
8. El administrador confirma el pago parcial presionando el botón **Confirmar Pago**.
9. El sistema valida que el monto sea válido y no exceda el saldo pendiente.
10. El sistema registra el pago parcial en la base de datos, calculando el nuevo saldo pendiente de la factura.
11. El sistema informa al administrador que el pago parcial se realizó correctamente, mostrando el monto pagado y el nuevo saldo.

## Flujos alternativos
*   **Si no hay facturas pendientes o parcialmente pagadas:**
    * El sistema muestra un mensaje indicando que no hay facturas disponibles para registrar pagos.

*   **Si el administrador no selecciona un método de pago:**
    * El sistema le notifica que debe seleccionar un método de pago para procesar el pago parcial.

*   **Si el monto ingresado es mayor al saldo pendiente:**
    * El sistema muestra un mensaje de error indicando que el monto no puede exceder el saldo pendiente de la factura.

*   **Si el monto ingresado es menor o igual a cero:**
    * El sistema muestra un mensaje de error indicando que el monto debe ser un valor positivo.

*   **Si la factura ya se encuentra pagada totalmente:**
    * El sistema impide registrar un pago parcial y muestra un mensaje indicando que la factura ya está cancelada.

## Poscondiciones
* Se crea un nuevo registro de Pago en el sistema asociado a la factura.
* El pago registra la fecha actual (fecha de pago), el monto pagado parcialmente y el método de pago utilizado.
* El saldo pendiente de la factura se actualiza restando el monto del pago parcial.
* Si el saldo pendiente es igual a cero, el estado de la factura cambia a PAGADA.
* Si el saldo pendiente es mayor a cero, el estado de la factura permanece en PARCIALMENTE PAGADA.

---

# Suspender cuenta

*   **ID:** GC-005.
*   **Descripción:** En este caso de uso, el administrador suspende temporalmente una cuenta sin eliminar información fiscal, impidiendo que se incluya en facturaciones.histórica.
*   **Actor(es):** *Administrador*.

## Precondiciones
* La cuenta debe haber sido previamente registrada.
* La cuenta debe estar en estado **Inactivo** o **Suspendido**

## Flujo principal de eventos
1. El administrador selecciona la cuenta a suspender y presiona la opción para editar.
2. El sistema muestra los datos de la cuenta, sus servicios asociados y el botón **Marcar como suspendido**.
3. El administrador presiona el botón.
4. El sistema pregunta si desea confirmar la operación.
5. El administrador confirma la operación.
6. El sistema actualiza el estado a **Suspendido**.
7. El sistema muestra mensaje de cambios realizados.
8. Se termina el caso de uso.

## Flujos alternativos
*   **Si la cuenta ya está suspendida:**
    * El sistema impide la acción y notifica al administrador.

## Poscondiciones
* La cuenta queda con estado Suspendida.
* La cuenta deja de aparecer en facturación masiva e individual.

---

![image](link)

# Ver detalle de cuenta

*   **ID:** GC-006.
*   **Descripción:** En este caso de uso, el administrador solicita al sistema visualizar toda la información fiscal, los datos del cliente asociado y los servicios vinculados a una cuenta.
*   **Actor(es):** *Administrador*.

## Precondiciones
* La cuenta debe haber sido previamente registrada.

## Flujo principal de eventos
1. El administrador selecciona una cuenta del listado y presiona el botón para ver el detalle de la cuenta.
2. El sistema carga y muestra el detalle completo:
    * ID de Cuenta
    * CUIT
    * Razón social
    * Condición frente al IVA
    * Domicilio fiscal
    * Estado
    * Nombre del cliente
    * Tipo de cliente
    * Dirección del cliente
    * Correo
    * Teléfono
    * Servicios asociados con precio y estado (Facturado / Pendiente)
3. El administrador revisa la información.
4. Se termina el caso de uso.

---

# Backlog de iteraciones

Las historias de usuario que se van a implementar en esta iteración son:

### 1. Suspensión de cuenta
- **Como** administrador
- **Quiero** poder suspender la cuenta de un cliente a pedido del mismo
- **Para** detener temporalmente la generación de facturas sin perder la información fiscal asociada

### 2. Anulación de factura (Nota de Crédito)
- **Como** administrador
- **Quiero** generar una nota de crédito para corregir una factura errónea
- **Para** que el monto de la factura quede ajustado correctamente dentro del sistema

### 3. Registro de pago parcial
- **Como** administrador
- **Quiero** registrar pagos parciales de una factura
- **Para** poder reflejar en el sistema los abonos realizados por un cliente antes de saldar el total

### 4. Consulta de pagos
- **Como** administrador
- **Quiero** visualizar todos los pagos realizados en el sistema
- **Para** poder consultar el historial de cobros de cada cliente y factura

### 5. Ver detalle cuenta
- **Como** administrador 
- **Quiero** quiero poder ver el detalle de una cuenta para consultar los datos registrados, información del cliente asociado y un listado de los servicios asociados
- **Para** para consultar más a detalle toda la información relacionada a una cuenta.


