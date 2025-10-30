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
Como *empleado*, quiero registrar un nuevo cliente con sus datos personales, fiscales y de contacto, para poder emitirle facturas.  

**Criterios de aceptación:**
- Se debe validar que el CUIT/DNI no esté ya registrado.  
- Todos los campos obligatorios (nombre, razón social, CUIT, condición IVA, dirección, teléfono o email) deben estar completos.  
- Al confirmar, el cliente aparece en el listado general con estado *Activo*.  

---

**HU-002 — Consulta de clientes**  
Como *empleado*, quiero visualizar el listado de clientes con su razón social, CUIT/DNI, condición frente al IVA y estado, para poder seleccionar uno al momento de facturar.  

**Criterios de aceptación:**
- La consulta debe listar todos los clientes activos.  
- Debe permitir filtrar por nombre, CUIT o estado.  
- Al seleccionar un cliente, se muestran sus datos detallados.  

---

**HU-003 — Modificación de clientes**  
Como *empleado*, quiero modificar la información de contacto de un cliente ya registrado, para mantener su información actualizada.  

**Criterios de aceptación:**
- Solo pueden modificarse los datos de contacto.
- El sistema debe registrar la fecha y empleado que realizó la modificación.  

---

**HU-004 — Baja de cliente**  
Como *empleado*, quiero dar de baja un cliente para que no se le generen nuevas facturas, manteniendo su historial de operaciones.  

**Criterios de aceptación:**
- Al dar de baja, el cliente cambia su estado a *Inactivo*.  
- El cliente no debe aparecer en los listados de facturación.  
- No se elimina la información ni el historial de facturas.  

---

### Módulo Servicios

**HU-010 — Alta de servicio**  
Como *empleado*, quiero registrar un servicio indicando su concepto, monto fijo y alícuota de IVA, para poder facturarlo.  

**Criterios de aceptación:**
- Los campos “concepto”, “monto” y “alícuota” son obligatorios.  
- No puede haber dos servicios con el mismo concepto activo.  
- Al guardarse, el servicio se muestra como *Activo*.  

---

**HU-011 — Consulta de servicios**  
Como *empleado*, quiero visualizar todos los servicios disponibles con su monto y alícuota de IVA, para asociarlos a los clientes.  

**Criterios de aceptación:**
- Se listan solo los servicios activos.  
- Debe permitir filtrar por nombre o alícuota.  

---

**HU-012 — Modificación de servicios**  
Como *empleado*, quiero modificar el concepto y/o costos de un servicio ya registrado, para mantenerlo actualizado.  

**Criterios de aceptación:**
- Solo puede modificarse si el servicio está activo.  
- El sistema debe registrar el empleado y la fecha de la modificación.  

---

**HU-013 — Baja de servicio**  
Como *empleado*, quiero dar de baja un servicio que ya no se ofrece, sin afectar las facturas previas.  

**Criterios de aceptación:**
- El servicio pasa a estado *Inactivo*.  
- No puede seleccionarse para facturas nuevas.  

---

### Módulo Empleados

**HU-020 — Alta de empleado**  
Como *administrador*, quiero registrar empleados con su nombre, apellido, legajo y rol, para identificar quién realiza cada operación.  

**Criterios de aceptación:**
- Los campos legajo y rol son obligatorios.  
- No se permite duplicar legajos.  

---

**HU-021 — Consulta de empleados**  
Como *administrador*, quiero visualizar la lista de empleados y sus roles, para gestionar las tareas internas.  

**Criterios de aceptación:**
- Debe permitir filtrar por nombre o rol.  
- Muestra solo empleados activos.  

---

**HU-022 — Modificación de empleados**  
Como *administrador*, quiero modificar el rol de un empleado ya registrado, para asignarle nuevas funciones.  

**Criterios de aceptación:**
- El sistema registra fecha y usuario que realizó el cambio.  
- El cambio impacta de inmediato en los permisos del empleado.  

---

**HU-023 — Baja de empleado**  
Como *administrador*, quiero dar de baja un empleado, para mantener la trazabilidad de las operaciones que haya realizado.  

**Criterios de aceptación:**
- El empleado pasa a estado *Inactivo* y no puede iniciar sesión.  
- Sus acciones pasadas permanecen registradas.  

---

### Módulo Cuentas

**HU-030 — Alta de cuenta**  
Como *empleado*, quiero crear una cuenta vinculada a un cliente, donde se asocien los servicios contratados.  

**Criterios de aceptación:**
- No puede haber más de una cuenta activa por cliente.  
- Se deben poder asociar uno o más servicios.  
- La cuenta queda en estado *Activa*.  

---

**HU-031 — Consulta de cuentas**  
Como *empleado*, quiero ver las cuentas existentes con su estado y servicios asociados.  

**Criterios de aceptación:**
- Debe mostrar el nombre del cliente, los servicios y el estado de la cuenta.  
- Permite filtrar por estado o cliente.  

---

**HU-032 — Suspender cuenta**  
Como *empleado*, quiero suspender la cuenta de un cliente a pedido del mismo, para que no se generen nuevas facturas.  

**Criterios de aceptación:**
- La cuenta pasa a estado *Suspendida*.  
- No se permite incluirla en facturación masiva.  

---

**HU-033 — Baja de cuenta**  
Como *empleado*, quiero dar de baja una cuenta para impedir que sea incluida en facturación.  

**Criterios de aceptación:**
- La cuenta pasa a *Inactiva*.
- No se permite incluirla en facturación masiva.

---

### Módulo Facturación

**HU-040 — Emisión de factura individual**  
Como *empleado*, quiero emitir una factura individual para una cuenta.  

**Criterios de aceptación:**
- Se debe calcular automáticamente el IVA según el servicio y la condición fiscal del cliente.  
- La factura se genera con número único y fecha de emisión.  

---

**HU-041 — Anulación de factura (nota de crédito)**  
Como *empleado*, quiero anular una factura errónea generando una nota de crédito asociada.  

**Criterios de aceptación:**
- Solo pueden anularse facturas activas.  
- Se crea una nota de crédito del mismo importe con vínculo a la factura original.  

---

**HU-042 — Facturación masiva**  
Como *empleado*, quiero presionar el botón “FACTURAR MASIVO” para generar facturas de todos los clientes activos.  

**Criterios de aceptación:**
- Solo incluye cuentas *Activas*.  
- Genera facturas con fecha y vencimiento.  
- Registra cantidad total emitida y empleado responsable.  

---

**HU-043 — Consulta de facturas**  
Como *empleado*, quiero visualizar todas las facturas emitidas y su estado.  

**Criterios de aceptación:**
- Permite filtrar por fecha o cliente.  
- Muestra pagos y notas de crédito asociadas.  

---

### Módulo Pagos

**HU-050 — Registro de pago total**  
Como *empleado*, quiero registrar pagos totales para evitar que una factura pagada se venza.

**Criterios de aceptación:**
- Solo puede realizarse sobre facturas impagas.   

---

**HU-051 — Registro de pago parcial**  
Como *empleado*, quiero registrar pagos parciales de una factura.  

**Criterios de aceptación:**
- El sistema recalcula el saldo restante.  
- Permite registrar pagos sucesivos hasta el monto total.  

---

**HU-052 — Registro de pago adelantado**  
Como *empleado*, quiero registrar pagos anticipados como saldo a favor.  

**Criterios de aceptación:**
- El saldo queda disponible para futuras facturas.  
- Se asocia automáticamente al cliente.  

---

**HU-053 — Consulta de pagos**  
Como *empleado*, quiero visualizar los pagos realizados con su monto, fecha y método.  

**Criterios de aceptación:**
- Debe permitir filtrar por cliente, factura o fecha.  
- Muestra el método de pago y empleado que lo registró.  

---

### Módulo Reportes

**HU-060 — Reporte de facturación masiva**  
Como *administrador*, quiero consultar los procesos de facturación masiva con su fecha, vencimiento, cantidad de facturas y empleado responsable, para auditar los procesos del sistema.  

**Criterios de aceptación:**
- El reporte muestra fecha, total de facturas y monto total.  

---

**HU-061 — Actualización de condición frente al IVA**  
Como *empleado*, quiero modificar la condición fiscal del cliente y que el sistema la considere en futuras facturas, para asegurar el correcto cálculo de impuestos.  

**Criterios de aceptación:**
- Solo se aplica a facturas nuevas.   
