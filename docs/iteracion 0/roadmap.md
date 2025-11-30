# Roadmap de desarrollo
El presente *roadmap* toma como base el ya establecido documento de especificaciones de requisitos (`erp.md`) y adopta una estructura en dos fases (una por cada iteración). Priorizando los módulos esenciales para que el sistema sea funcional y entregue valor básico rápidamente.

---
## Backlog de iteración
## Fase 1: Mínimo Producto Viable (MVP)

**Objetivo:** Establecer la base del sistema, incluyendo la gestión de usuarios, clientes, servicios y la funcionalidad de facturación individual y pagos. El sistema debe ser mínimamente funcional para emitir y cobrar una factura.

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
---

## Fase 2: Funciones avanzadas, Refactoring y Testeo

**Objetivo:** Incorporar funcionalidades extra más avanzadas, como la Facturación Masiva, visualización de registros de pago, entre otras. Junto al refactoring de las funciones implementadas y finalmente el testeo del sistema, previo al empaquetamiento.

### 1. Suspender cuenta
- **Como** administrador 
- **Quiero** poder suspender la cuenta de un cliente a pedido del mismo
- **Para** detener temporalmente la generación de facturas sin perder la información fiscal asociada.

### 2. Anulación de facturas
- **Como** administrador 
- **Quiero** anular una factura errónea generando automáticamente una nota de crédito del mismo importe, vinculada a la factura original
- **Para** corregir errores en comprobantes ya emitidos, manteniendo la coherencia contable y la trazabilidad de la operación mediante la nota de crédito.

### 3. Registro de pago parcial
- **Como** administrador 
- **Quiero** registrar pagos parciales de una determinada factura
- **Para** registrar el abono de una parte del monto total y que el sistema la considere al momento de realizar otro pago parcial, o el pago total.

### 4. Consulta de pagos
- **Como** administrador
- **Quiero** visualizar un listado de todos los pagos registrados con opciones de filtrado y ordenamiento
- **Para** consultar el historial completo de cobros realizados.