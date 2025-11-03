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
- Registrar clientes, empleados, servicios y cuentas asociadas.  
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
- El cliente queda registrado con estado Activo. 

---

**HU-002 — Consulta de clientes**  
Como *Administrador*, quiero visualizar el listado de clientes registrados, para poder visualizar su nombre, dirección e información de contacto dentro del sistema.  

**Criterios de aceptación:**
- El listado muestra todos los clientes activos.
- Cada cliente en el listado muestra:
    - Nombre → nombre completo si es persona física, o denominación comercial si es persona jurídica.
    - Dirección → domicilio o ubicación física principal del cliente..
    - Teléfono → número de contacto.
    - Correo electrónico → email de contacto.
    - Tipo → Persona Fisica/Juridica
- El sistema permite buscar clientes por nombre, mostrando todos los que coincidan parcial o totalmente con el texto ingresado; si no hay coincidencias, se muestra un mensaje indicando “No se encontraron clientes”.
- Se debe poder filtrar por tipo

---

**HU-003 — Modificación de clientes**  
Como *Administrador*, quiero poder modificar los datos de un cliente registrado, para mantener actualizada su información dentro del sistema.  

**Criterios de aceptación:**
- Solo se pueden modificar los siguientes campos:
    - Nombre → completo si es persona física, denominación comercial si es persona jurídica.
    - Tipo → Persona Física / Persona Jurídica.
    - Dirección → domicilio o ubicación física principal del cliente..
    - Teléfono → número de contacto.
    - Correo electrónico → email de contacto.
- El sistema valida que los campos obligatorios no queden vacíos; si falta alguno, muestra un mensaje de error y no guarda los cambios.
- Los cambios se reflejan inmediatamente en los listados de clientes.  

---

**HU-004 — Baja de cliente**  
Como *Administrador*, quiero dar de baja un cliente registrado, para que no se generen nuevas cuentas ni se pueda facturar a su nombre

**Criterios de aceptación:**
- Al dar de baja, el cliente cambia su estado a *Inactivo*.  
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

### Módulo Empleados

**HU-020 — Alta de empleado**  
Como *Administrador*, quiero registrar un nuevo empleado con su nombre, apellido, legajo y rol, para que pueda operar en el sistema y su actividad quede registrada para trazabilidad.  

**Criterios de aceptación:**
- Todos los campos obligatorios (nombre, apellido, legajo, rol) deben completarse; si falta alguno, el sistema muestra un mensaje de error y no guarda.
- Se permite que un empleado desempeñe varios roles.
- Al registrarse, el empleado queda con estado Activo.
- No se permite duplicar legajos.  

---

**HU-021 — Consulta de empleados**  
Como *Administrador*, quiero visualizar la lista de empleados registrados, para gestionar al personal y asegurar la trazabilidad de sus operaciones dentro del sistema.

**Criterios de aceptación:**
- La lista muestra todos los empleados activos.
- Cada empleado en el listado incluye:
    - Nombre y apellido → Datos personales del empleado.
    - Legajo → identificador único dentro del sistema.
    - Rol → Rol/Roles que desempeña el empleado
- El sistema permite buscar empleados por nombre y apellido si no hay coincidencias, muestra “No se encontraron empleados”.  

---

**HU-022 — Modificación de empleados**  
Como *Administrador*, quiero modificar los datos de un empleado registrado, para mantener actualizada su información y rol dentro del sistema.

**Criterios de aceptación:**
- ESe pueden modificar los campos nombre, apellido y rol.
- El legajo del empleado no puede modificarse, ya que actúa como identificador único.
- El sistema valida que el empleado tenga al menos un rol asignado antes de guardar los cambios.


---

**HU-023 — Baja de empleado**  
Como *Administrador*, quiero dar de baja un empleado registrado, para que no pueda realizar operaciones dentro del sistema.  

**Criterios de aceptación:**
- Al dar de baja, el empleado cambia su estado a Inactivo. 
- Un empleado inactivo no puede iniciar sesión ni realizar operaciones en el sistema.
- La información del empleado no se elimina, solo deja de mostrarse en los listados activos.  

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
Como *Administrador*, quiero emitir una factura individual para una cuenta, para registrar los servicios contratados y pendientes de pago, asegurando el cálculo correcto de impuestos.

**Criterios de aceptación:**
- Al ingresar a la sección, el sistema muestra todos los servicios asignados a todas las cuentas, ordenados por cuenta.
- La pantalla permite filtrar los servicios por cuenta, mostrando únicamente los servicios asociados a la cuenta seleccionada.
- Cada servicio listado incluye:
    - Nombre del servicio
    - Descripción
    - Monto
    - Estado de pago (pagado/impago/ ParcialmentePagado)
- Se pueden aplicar filtros adicionales:
    - Estado de pago
    - Fecha de asignación
    - Nombre del servicio
- Al seleccionar los servicios y confirmar, el sistema:
    - Calcula automáticamente el IVA según el servicio y la condición fiscal de la cuenta.
    - Genera la factura digitalmente dentro del sistema, asignándole número único, fecha de emisión y fecha de vencimiento.
    - La factura no se emite en papel, solo queda registrada para consultas y trazabilidad.
- La factura se genera con número único y fecha de emisión.  

---

**HU-041 — Anulación de factura (nota de crédito)**  
Como *Administrador*, quiero generar una nota de crédito para corregir una factura errónea, de manera que el monto de la factura quede ajustado correctamente dentro del sistema.

**Criterios de aceptación:**
- Solo pueden anularse facturas activas.
- Al anular una factura, el sistema genera automáticamente una nota de crédito por el valor total de la factura, vinculada a la factura original.

---

**HU-042 — Facturación masiva**  
Como *Administrador*, quiero generar facturas de manera masiva para todas las cuentas activas, para registrar automáticamente los servicios pendientes de pago dentro del sistema.

**Criterios de aceptación:**
- La facturación masiva solo incluye cuentas en estado *Activo*..  
- Para cada cuenta, se incluyen los servicios pendientes de pago, calculando automáticamente el IVA según la condición fiscal de la cuenta.
- El sistema registra:
    - Cantidad total de facturas generadas
    - Monto total facturado
    - Empleado responsable del proceso

---

**HU-043 — Consulta de facturas**  
Como *empleado*, quiero visualizar todas las facturas emitidas, para poder consultar los servicios incluidos, los montos y los pagos realizados dentro del sistema.

**Criterios de aceptación:**
- Permite filtrar por cliente, cuenta, estado del pago y fecha de emisión.  
- Muestra, para cada factura:
    - Número de factura
    - Cuenta y cliente asociado
    - Servicios incluidos
    - Monto total
    - Pagos registrados y método de pago
    - Empleado que la emitió
- Permite acceder al detalle completo de cada factura.

---

### Módulo Pagos

**HU-050 — Registro de pago total**  
Como *Administrador*, quiero registrar pagos totales de facturas, para reflejar correctamente las operaciones canceladas y evitar que facturas pagadas se consideren vencidas.


**Criterios de aceptación:**
- Solo pueden registrarse pagos sobre facturas con saldo pendiente.
- Al registrar el pago total:
    - El sistema toma automáticamente el monto restante de la factura como monto del pago.
    - Registra la fecha del pago y el medio utilizado.
- Si el pago se realiza después del vencimiento, el sistema conserva la fecha real de pago.
- No se permite modificar ni eliminar el pago una vez confirmado.

---

**HU-051 — Registro de pago parcial**  
Como *Administrador*, quiero registrar pagos parciales de una factura, para poder reflejar en el sistema los abonos realizados por un cliente antes de saldar el total.

**Criterios de aceptación:**
- El sistema recalcula automáticamente el saldo restante de la factura.
- Permite registrar pagos sucesivos hasta cubrir el monto total.
- Cada pago queda registrado con su fecha, monto y método de pago.
- La factura se considera pagada cuando el saldo pendiente calculado (total de la factura menos la suma de los pagos registrados) es igual a cero.

---

**HU-052 — Registro de pago adelantado**  
Como *Administrador*, quiero registrar pagos anticipados para un cliente, para que puedan aplicarse automáticamente a futuras facturas. 

**Criterios de aceptación:**
- El pago registrado queda disponible como saldo a favor del cliente.  
- El sistema asocia automáticamente el saldo al cliente correspondiente.
- Al generar futuras facturas, el saldo a favor se puede aplicar para reducir el monto a pagar.

---

**HU-053 — Consulta de pagos**  
Como *Administrador*, quiero visualizar todos los pagos realizados en el sistema, para poder consultar el historial de cobros de cada cliente y factura.

**Criterios de aceptación:**
- El listado muestra, para cada pago:
    - Monto abonado
    - Fecha
    - Método de pago (efectivo, tarjeta, transferencia, etc.)
    - Cliente asociado
    - Factura asociada 
- Permite filtrar por cliente, factura o rango de fechas.
- Permite ordenar los pagos por fecha o monto.
- No se permite modificar ni eliminar los registros de pago desde esta sección (solo consulta).

---

### Módulo Reportes

**HU-060 — Reporte de facturación masiva**  
Como *Administrador*, quiero visualizar un listado de los procesos de facturación masiva realizados, para consultar sus resultados y verificar los montos y cantidad de facturas generadas.

**Criterios de aceptación:**
- El listado muestra, para cada proceso:
    - Fecha del proceso
    - Cantidad de facturas generadas
    - Monto total facturado
    - Empleado responsable del proceso
---

