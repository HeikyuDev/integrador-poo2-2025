# Especificación de requisitos de software

## Enunciado del problema
La empresa necesita un sistema que le permita **gestionar la facturación de los servicios que brinda a sus clientes**, ya que actualmente el proceso no está informatizado ni centralizado.  
El objetivo es disponer de una herramienta que facilite la **emisión de facturas individuales o masivas**, la **anulación de comprobantes mediante notas de crédito**, el **registro de pagos** (totales, parciales o adelantados) y la **trazabilidad completa de las operaciones**.  
Con este sistema, la empresa podrá controlar de manera más eficiente los servicios facturados, los clientes activos y los movimientos de cobro, reduciendo la posibilidad de errores e inconsistencias.

---

## Clientes potenciales
- Empresas pequeñas y medianas que prestan servicios periódicos (ej.: proveedor de internet, cable, electricidad interna a consorcios) y requieren facturación periódica/masiva.  
- Administración interna de la empresa (empleados/operadores) que registran clientes, emiten facturas y gestionan cobros.  
> El cliente final **no interactúa directamente con el sistema**; las operaciones son realizadas por los empleados.

---

## Solución propuesta
Se desarrollará una **aplicación web** que automatice la gestión de facturación y cobros.  
El sistema permitirá:
- Registrar clientes, servicios y cuentas asociadas.  
- Emitir facturas individuales o masivas con cálculo automático de IVA.  
- Generar notas de crédito ante anulaciones.  
- Registrar pagos totales, parciales o adelantados.  
- Consultar el estado de cuenta de cada cliente y el historial de facturación.  

La aplicación facilitará el control administrativo, mantendrá la coherencia contable y brindará una trazabilidad clara de todas las operaciones realizadas.

---

## Requisitos (historias de usuario)

> Cada historia se redacta según las pautas vistas en clase y se complementa con criterios de aceptación verificables.

---

### Módulo Clientes

**HU-001 — Alta de cliente**  
Como *Administrador*, quiero poder registrar un nuevo cliente con su nombre, tipo, direccion y datos de contacto, para poder  gestionarlo en el sistema y asociarle cuentas posteriormente.  

**Criterios de aceptación:**
- Todos los campos obligatorios (nombre, tipo, dirección, teléfono, correo) deben estar completos; si falta alguno, el sistema muestra un mensaje de error y no permite guardar.
- El cliente queda registrado con el estado seleccionado, ya sea Activo o Inactivo. 

---

**HU-002 — Consulta de clientes**  
Como *Administrador*, quiero visualizar el listado de clientes registrados, para poder visualizar su nombre, dirección e información de contacto dentro del sistema.  

**Criterios de aceptación:**
- El listado muestra todos los clientes activos.
- Cada cliente en el listado muestra:
    - Id → identificador único del cliente.
    - Nombre → nombre completo si es persona física, o denominación comercial si es persona jurídica.
    - Dirección → domicilio o ubicación física principal del cliente..
    - Teléfono → número de contacto.
    - Correo electrónico → email de contacto.
    - Tipo → Persona Fisica/Jurídica
    - Estado → Activo/Inactivo
- El sistema permite filtrar clientes por nombre, mostrando todos los que coincidan parcial o totalmente con el texto ingresado; si no hay coincidencias, no se muestra en la tabla.
- Se debe poder filtrar por estado.
- Se debe poder filtrar por tipo.
- Cada cliente tiene asociado un boton para dar de baja o editarlo.
- Si el cliente esta suspendido, solo se deberia poder editarlo para asi marcarlo como Activo/Inactivo nuevamente.


---

**HU-003 — Modificación de clientes**  
Como *Administrador*, quiero poder modificar los datos de un cliente registrado, para mantener actualizada su información dentro del sistema.  

**Criterios de aceptación:**
- Se pueden modificar los siguientes campos:
    - Tipo → Persona Física / Persona Jurídica.
    - Nombre → completo si es persona física, denominación comercial si es persona jurídica.
    - Dirección → domicilio o ubicación física principal del cliente..
    - Teléfono → número de contacto.
    - Correo electrónico → email de contacto.
    - Estado → Activo/Inactivo.
- El sistema valida que los campos obligatorios no queden vacíos; si falta alguno, muestra un mensaje de error y no guarda los cambios.
- Los cambios se reflejan inmediatamente en los listados de clientes.  

---

**HU-004 — Baja de cliente**  
Como *Administrador*, quiero dar de baja un cliente registrado, para que no se generen nuevas cuentas ni se pueda facturar a su nombre

**Criterios de aceptación:**
- Al dar de baja, el cliente cambia su estado a *Suspendido*.  
- El boton de suspender cliente desaparecera y solo se podra editar el cliente para cambiar su estado a Activo/Inactivo.
- La información del cliente (nombre, tipo, dirección postal, teléfono, correo) permanece registrada.
- Las cuentas y facturas existentes asociadas al cliente permanecen en el sistema y pueden consultarse, pero no se pueden crear nuevas operaciones mientras el cliente esté Inactivo.

---

### Módulo Servicios

**HU-010 — Alta de servicio**  
Como *Administrador*, quiero registrar un servicio indicando su nombre, descripción, monto fijo y alícuota de IVA, para poder facturarlo correctamente.  

**Criterios de aceptación:**
- Los campos obligatorios son:
    - Nombre → Nombre del Servicio
    - Monto → precio fijo del servicio.
    - Alícuota de IVA → porcentaje de impuesto aplicable.
- La descripción es opcional y permite detallar características del servicio.
- No puede haber dos servicios activos con el mismo nombre. 
- Al guardar, el servicio queda registrado con estado *Activo* y disponible para asociar a cuentas y generar facturas.  

---

**HU-011 — Consulta de servicios**  
Como *Administrador*, quiero visualizar todos los servicios registrados y activos, mostrando su nombre, descripción, monto y alícuota de IVA, para poder consultar los servicios disponibles en el sistema.

**Criterios de aceptación:**
- Solo se muestran los servicios con estado Activo.
- Cada servicio en el listado incluye:
    - Nombre → identificador corto del servicio.
    - Descripción → detalles adicionales (si los hay).
    - Monto → precio del servicio.
    - Alícuota de IVA → porcentaje de impuesto aplicable.
- El sistema permite buscar servicios por nombre, mostrando todos los que coincidan parcial o totalmente; si no hay coincidencias, se muestra el mensaje “No se encontraron servicios”.

---

**HU-012 — Modificación de servicios**  
Como *Administrador*, quiero poder modificar los datos de un servicio ya registrado, para mantener su información actualizada y correcta en el sistema.  

**Criterios de aceptación:**
- Solo se pueden modificar los siguientes campos de servicios activos:
    - Nombre → identificador corto del servicio.
    - Descripción → detalles adicionales (si los hay).
    - Monto → precio del servicio.
    - Alícuota de IVA → porcentaje de impuesto aplicable.
- El sistema valida que los campos obligatorios no queden vacíos; si falta alguno, muestra un mensaje de error y no guarda los cambios.
- No se permite que existan dos servicios activos con el mismo nombre.
- Los cambios se reflejan inmediatamente en los listados y consultas de servicios.

---

**HU-013 — Baja de servicio**  
Como *Administrador*, quiero dar de baja un servicio registrado, para que no se pueda asociar a nuevas cuentas ni generar facturas. 

**Criterios de aceptación:**
- Al dar de baja, el servicio cambia su estado a *Inactivo*.  
- La información del servicio (nombre, descripción, monto, alícuota de IVA) permanece registrada.
- No se pueden generar nuevas facturas ni asociar el servicio a nuevas cuentas mientras esté Inactivo.  

---

### Módulo Cuentas

**HU-030 — Alta de cuenta**  
Como *Administrador*, quiero crear una cuenta vinculada a un cliente, para registrar sus datos fiscales y permitir su utilización en procesos de facturación.

**Criterios de aceptación:**
- La cuenta debe estar asociada a un cliente existente.  
- Se deben ingresar los siguientes datos obligatorios:
    - CUIT
    - Razón social
    - Condición frente al IVA 
    - Domicilio fiscal
- El sistema valida que el CUIT no esté ya registrado en otra cuenta activa.
- Al confirmar, la cuenta se registra con estado Activo y queda disponible para operaciones de facturación. 

---

**HU-031 — Consulta de cuentas**  
Como *Administrador*, quiero visualizar todas las cuentas activas registradas en el sistema, para consultar su información fiscal y el cliente al que pertenecen.

**Criterios de aceptación:**
- El listado muestra todas las cuentas activas.
- Cada cuenta en el listado incluye:
    - CUIT
    - Razón social
    - Condición frente al IVA
    - Domicilio fiscal
    - Cliente asociado (nombre del cliente titular de la cuenta)
- Permite filtrar por cliente. (visualizar todas las cuentas correspondientes a un determinado cliente)

---

**HU-032 — Suspender cuenta**  
Como *Administrador*, quiero poder suspender la cuenta de un cliente a pedido del mismo, para detener temporalmente la generación de facturas sin perder la información fiscal asociada. 

**Criterios de aceptación:**
- Al suspender la cuenta, su estado cambia a *Suspendida*.  
- Las cuentas suspendidas no pueden incluirse en la facturación masiva ni individual.
- La cuenta suspendida permanece visible solo en la sección de “Cuentas suspendidas” del sistema. 

---

**HU-033 — Modificacion de Cuenta**

Como *Administrador*, quiero poder modificar los datos fiscales de una cuenta registrada, para mantener actualizada su información y asegurar que las futuras facturas se emitan correctamente.

**Criterios de aceptación:**

- Solo se pueden modificar los siguientes campos:
    - Razón social
    - Condición frente al IVA
    - Domicilio fiscal
- El CUIT no puede modificarse, ya que identifica de forma única la cuenta.
- El sistema valida que los campos obligatorios no queden vacíos.

---

**HU-034 — Baja de cuenta**  
Como *Administrador*, quiero dar de baja una cuenta registrada, para que no pueda ser utilizada en procesos de facturación futuros. 

**Criterios de aceptación:**
- Al dar de baja, la cuenta cambia su estado a *Inactiva*.
- Las cuentas inactivas no pueden incluirse en facturación individual ni masiva.
- La información de la cuenta no se elimina, permitiendo consultar su historial de facturación y pagos asociados.

---

### Módulo Facturación

**HU-040 — Emisión de factura individual**  
Como *Administrador*, quiero emitir una factura individual seleccionando servicios específicos de una cuenta y su periodicidad, para registrar la operación y permitir el cobro posterior.

**Criterios de aceptación:**
- El sistema debe permitir seleccionar una cuenta activa e inmediatamente muestra solo los servicios en estado **PENDIENTE** de esa cuenta.
- La lista de servicios debe muestrar para cada uno: nombre, descripción, monto unitario, cantidad y checkbox de seleccion.
- El sistema debe permitir seleccionar múltiples servicios y modificar sus cantidades antes de facturar.
- El sistema debe permitir seleccionar una periodicidad predefinida (MENSUAL, TRIMESTRAL, SEMESTRAL, ANUAL) que determina la fecha de vencimiento.
- El sistema debe validar que se seleccione al menos un servicio y una periodicidad válida antes de procesar.
- La factura generada debe incluir: número único de comprobante, fechas de emisión (hoy) y vencimiento (calculada según periodicidad), datos históricos del cliente y servicios, tipo de comprobante (A/B/C/D/E) según condición fiscal.
- El sistema debe calcular automáticamente: subtotal de cada servicio (monto unitario × cantidad), IVA de cada servicio (subtotal × alícuota), monto total de la factura.
- El sistema debe actualizar el estado de los servicios facturados a **FACTURADO**.
- El sistema debe ejecutar la operación de forma transaccional: si ocurre un error, todos los cambios se revierten.
- El sistema debe mostrar un mensaje de éxito con el número de factura generado.  

---

**HU-041 — Anulación de factura (nota de crédito)**  
Como *Administrador*, quiero anular una factura mediante una nota de crédito, para corregir errores.

**Criterios de aceptación:**
- El sistema debe permitir filtrar facturas por cuenta y número de comprobante (búsqueda parcial).
- El sistema debe mostrar solo facturas en estado **PENDIENTE** o **PARCIALMENTE_PAGADA**.
- El sistema debe permitir ingresar un motivo de anulación con validación de 10 a 100 caracteres.
- El sistema debe validar que la factura no esté ya anulada (no tenga nota de crédito asociada).
- El sistema debe crear automáticamente una nota de crédito vinculada a la factura con: monto igual al total, motivo ingresado, fecha de emisión (hoy), tipo de comprobante (A/B/C/D/E) según condición fiscal.
- El sistema debe revertir los servicios facturados a estado **PENDIENTE** para permitir refacturación.
- El sistema debe ejecutar la operación de forma transaccional: si ocurre un error, todos los cambios se revierten.
- El sistema debe mostrar un mensaje de éxito confirmando la anulación.

---

**HU-042 — Facturación masiva**  
Como *Administrador*, quiero generar facturas masivamente para todas las cuentas activas, para automatizar la facturación de servicios pendientes.

**Criterios de aceptación:**
- El sistema debe mostrar un formulario con desplegable de periodicidad  (MENSUAL, TRIMESTRAL, SEMESTRAL, ANUAL) y botón "Facturar".
- El sistema debe validar que se haya seleccionado una periodicidad válida antes de procesar.
- El sistema debe identificar automáticamente todas las cuentas en estado **ACTIVO** que tengan servicios en estado **PENDIENTE**.
- El sistema debe crear una factura para cada cuenta con servicios pendientes, incluyendo: número único de comprobante, fecha de emisión (hoy), fecha de vencimiento (calculada según periodicidad seleccionada), servicios pendientes de esa cuenta, IVA calculado por cada servicio, datos históricos del cliente y servicios, tipo de comprobante (A/B/C/D/E) según condición fiscal.
- El sistema debe actualizar a **FACTURADO** el estado de los servicios incluidos en cada factura.
- El sistema no debe generar factura para cuentas que no tengan servicios pendientes.
- El sistema debe rechazar la operación si no hay servicios pendientes en ninguna cuenta activa.
- El sistema debe registrar un registro de facturación masiva con: cantidad total de facturas generadas, monto total facturado (suma de montos totales de todas las facturas), fecha del proceso (hoy).
- El sistema debe ejecutar la operación de forma transaccional: si ocurre un error, todos los cambios se revierten.
- El sistema debe mostrar un mensaje de éxito indicando cantidad de facturas generadas y monto total facturado.

---

**HU-043 — Consulta de facturas**  
Como *Administrador*, quiero visualizar un listado completo de facturas con opciones de filtrado y acceder a los detalles de cada una, para poder consultar y analizar el historial de facturación de los clientes.

**Criterios de aceptación:**
- El sistema debe mostrar todas las facturas registradas.
- El sistema debe permitir filtrar por cuenta (desplegable) y por número de comprobante (búsqueda).
- El sistema debe ordenar el listado por fecha de emisión en orden descendente (más recientes primero).
- El sistema debe mostrar en cada fila: número de comprobante, razón social, fechas de emisión y vencimiento y monto total.
- El sistema debe permitir acceder al detalle completo de una factura.
- El sistema debe mostrar en el detalle: número y tipo de comprobante (A/B/C/D/E), datos del cliente (CUIT, razón social, domicilio fiscal, condición frente al IVA), cuenta asociada, servicios facturados (nombre, cantidad, precio unitario, subtotal, alícuota IVA e IVA de cada servicio), subtotal, total IVA y monto total.
- El sistema debe mostrar, si la factura está anulada, la nota de crédito asociada con motivo y fecha de emisión.
---

**HU-044 — Consulta de facturación masiva**  
Como *Administrador*, quiero visualizar un listado de todos los procesos de facturación masiva realizados con opción de acceder a los detalles, para consultar el historial y verificar los resultados de cada proceso.

**Criterios de aceptación:**
- El sistema debe mostrar todos los procesos de facturación masiva registrados.
- El sistema debe mostrar para cada proceso en el listado: fecha de emisión, cantidad de facturas generadas y monto total facturado.
- El sistema debe ordenar el listado de forma predeterminada por fecha de emisión en orden descendente (más recientes primero).
- El sistema debe permitir acceder al detalle completo de cada proceso, mostrando la información general (fecha, cantidad y monto total) y el listado de todas las facturas generadas con sus datos (número de comprobante, razón social, monto y fecha de vencimiento), permitiendo acceder al detalle individual de cada factura.
- El sistema no debe permitir modificar ni eliminar registros desde esta sección (solo consulta).

---

### Módulo Pagos

**HU-050 — Registro de pago total**  
Como *Administrador*, quiero registrar un pago que cancela completamente una factura, para reflejar que ha sido pagada en su totalidad.

**Criterios de aceptación:**
- El sistema debe validar que la factura seleccionada tenga saldo pendiente.
- El sistema debe calcular automáticamente el monto del pago como el saldo restante de la factura (monto total menos pagos registrados anteriormente).
- El sistema debe permitir seleccionar el método de pago (efectivo, tarjeta, transferencia, etc.).
- El sistema debe registrar la fecha del pago como la fecha actual.
- El sistema debe persistir el pago de forma transaccional.
- El sistema debe mostrar un mensaje de éxito confirmando el registro del pago y el monto registrado.

---

**HU-051 — Registro de pago parcial**  
Como *Administrador*, quiero registrar un pago parcial de una factura, para reflejar los abonos que realiza un cliente en cuotas.

**Criterios de aceptación:**
- El sistema debe validar que la factura seleccionada tenga saldo pendiente.
- El sistema debe validar que el monto ingresado sea mayor a cero.
- El sistema debe validar que el monto ingresado no exceda el saldo pendiente de la factura.
- El sistema debe permitir seleccionar el método de pago (efectivo, tarjeta, transferencia, etc.).
- El sistema debe registrar la fecha del pago como la fecha actual.
- El sistema debe permitir registrar múltiples pagos parciales para la misma factura.
- El sistema debe persistir el pago de forma transaccional.
- El sistema debe mostrar un mensaje de éxito confirmando el registro del pago y el monto registrado.

---

**HU-052 — Consulta de pagos**  
Como *Administrador*, quiero visualizar un listado de todos los pagos registrados con opciones de filtrado y ordenamiento, para consultar el historial completo de cobros realizados.

**Criterios de aceptación:**
- El sistema debe mostrar todos los pagos registrados en el sistema.
- El sistema debe mostrar para cada pago: monto abonado, fecha de pago, método de pago, razón social del cliente y número de comprobante de la factura asociada.
- El sistema debe permitir filtrar por cuenta (desplegable) seleccionando una cuenta específica para ver solo sus pagos.
- El sistema debe permitir filtrar por número de comprobante (búsqueda) mostrando solo los pagos asociados a esa factura.
- El sistema debe permitir filtrar por rango de fechas (fecha desde y fecha hasta) validando que la fecha desde no sea posterior a la fecha hasta.
- El sistema debe ordenar el listado de forma predeterminada por fecha de pago en orden descendente (más recientes primero).
- El sistema debe permitir cambiar el ordenamiento por fecha (ascendente o descendente) o por monto (ascendente o descendente).
- El sistema debe mostrar el listado de forma paginada con 10 registros por página.
- El sistema no debe permitir modificar ni eliminar registros de pago desde esta sección (solo lectura).

---



