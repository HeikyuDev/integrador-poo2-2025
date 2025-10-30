# Roadmap de desarrollo
El presente *roadmap* toma como base el ya establecido documento de especificaciones de requisitos (`erp.md`) y adopta una estructura en dos fases (una por cada iteración). Priorizando los módulos esenciales para que el sistema sea funcional y entregue valor básico rápidamente.

---
## Backlog de iteración
## Fase 1: Mínimo Producto Viable (MVP)

**Objetivo:** Establecer la base del sistema, incluyendo la gestión de usuarios, clientes, servicios y la funcionalidad de facturación individual y pagos. El sistema debe ser mínimamente funcional para emitir y cobrar una factura.

### 1. Gestión de clientes
- **Como** empleado
- **Quiero** registrar, consultar, editar y dar de baja clientes que estén registrados en el sistema
- **Para** mantener el registro de clientes actualizado, emitir facturas solo a clientes activos y disponer de su información fiscal y de contacto para las operaciones.

### 2. Gestión de servicios
- **Como** empleado
- **Quiero** registrar, consultar, editar y dar de baja servicios que estén registrados en el sistema
- **Para** contar con un catálogo de servicios actualizado con sus costos y alícuotas de IVA correctas, permitiendo la facturación precisa de los mismos.

### 3. Gestión de empleados
- **Como** adminsitrador
- **Quiero** registrar, consultar, editar y dar de baja empleados que estén registrados en el sistema
- **Para** identificar a la persona que realiza cada operación crítica (trazabilidad) y mantener el control sobre el personal autorizado para gestionar el sistema.

### 4. Gestión de cuentas
- **Como** empleado
- **Quiero** registrar, consultar, suspender y dar de baja las cuentas de los clientes
- **Para** vincular los servicios contratados al cliente, controlar el estado de servicio (activo, suspendido, baja) e impedir que las cuentas dadas de baja sean incluidas en facturación masiva.

### 5. Facturación individual
- **Como** empleado
- **Quiero** emitir una factura individual y que el sistema calcule el IVA según la alícuota del servicio y la condición fiscal del cliente
- **Para** documentar la venta de un servicio específico fuera del ciclo masivo de facturación, cumpliendo con la legislación fiscal.

### 6. Registro de pago total
- **Como** empleado 
- **Quiero** registrar el pago total de una determinada factura 
- **Para** reflejar correctamente la cuenta corriente del cliente en cuestión.

---

## Fase 2: Funciones avanzadas, Refactoring y Testeo

**Objetivo:** Incorporar funcionalidades extra más avanzadas, como la Facturación Masiva, visualización de registros de pago, entre otras. Junto al refactoring de las funciones implementadas y finalmente el testeo del sistema, previo al empaquetamiento.

### 1. Facturación Masiva
- **Como** empleado 
- **Quiero** quiero presionar el botón **“FACTURAR MASIVO”** y que el sistema genere facturas de todos los servicios correspondientes a **clientes activos**, registrando la fecha de emisión, vencimiento y cantidad de facturas generadas
- **Para** automatizar el proceso de emisión de facturas periódicas y generar todos los comprobantes de clientes activos en un solo proceso eficiente.

### 2. Anulación de facturas
- **Como** empleado 
- **Quiero** anular una factura errónea generando automáticamente una nota de crédito del mismo importe, vinculada a la factura original
- **Para** corregir errores en comprobantes ya emitidos, manteniendo la coherencia contable y la trazabilidad de la operación mediante la nota de crédito.

### 3. Registro de pagos por adelantado
- **Como** empleado 
- **Quiero** registrar pagos anticipados
- **Para** reflejar el dinero recibido como saldo a favor del cliente y que pueda ser aplicado a futuras facturas.

### 4. Registro de pago parcial
- **Como** empleado 
- **Quiero** registrar pagos parciales de una determinada factura
- **Para** registrar el abono de una parte del monto total y que el sistema la considere al momento de realizar otro pago parcial, o el pago total.

### 5. Consultar reportes y estado de cuenta
- **Como** empleado
- **Quiero** visualizar los procesos de facturación masiva, pagos registrados y el estado de cuenta de cada cliente (con su historial de facturación)
- **Para** auditar las operaciones masivas, controlar la actividad de los empleados y conocer rápidamente el saldo pendiente de cada cliente.

### 6. Actualizar condición ante el IVA de un Cliente
- **Como** empleado
- **Quiero** modificar la condición fiscal de un cliente
- **Para** asegurar que el sistema considere la condición fiscal actualizada al momento de emitir nuevas facturas y calcular el IVA correctamente.