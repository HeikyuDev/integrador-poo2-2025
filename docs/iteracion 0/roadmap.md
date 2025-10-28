# 📑 Propuestas de Historias de Usuario

Las siguientes propuestas de historias de usuario están separadas en dos categorías: *Iteraciones futuras* (aquellas con funcionalidades consideradas avanzadas, más apropiadas para futuras iteraciones) y *Próximas iteraciones* (historias de usuario con funcionalidades consideradas básicas o mínimas para permitir el funcionamiento del sistema).

---

## 🚀 Próximas Iteraciones (Funcionalidad Mínima Viable)

### Historias de Usuario enfocadas en el rol de: Administrador de Cuentas

| ID | Título | Historia de Usuario |
| :--- | :--- | :--- |
| *H.U. 01* | *Alta de clientes* | **Como** Administrador de Cuentas, **quiero** dar de alta un nuevo cliente ingresando sus datos fiscales (CUIT, Razón Social) **para** registrarlo oficialmente en el sistema y poder facturarle sus respectivos servicios. |
| *H.U. 02* | *Modificación de clientes* | Como *Administrador de Cuentas, quiero modificar los datos personales de un cliente ya registrado **para* corregir posibles equivocaciones, o mantener actualizados los datos de un cliente a lo largo del tiempo. |
| *H.U. 03* | *Baja de clientes* | Como *Administrador de Cuentas, quiero dar de baja un cliente **para* realizar facturaciones únicamente de aquellos clientes que sean activos. |
| *H.U. 04* | *Asignar condiciones fiscales a clientes* | Como *Administrador de Cuentas, quiero poder asignar y modificar la condición fiscal (Responsable Inscripto, Monotributista, Exento, Consumidor Final, etc.) de cada cliente **para* que el sistema calcule correctamente el IVA según la condición del cliente. |

### Historias de Usuario enfocadas en el rol de: Operador de Facturación

| ID | Título | Historia de Usuario |
| :--- | :--- | :--- |
| *H.U. 05* | *Generar facturas (Individual)* | Como *Operador de Facturación, quiero generar una factura individual seleccionando un cliente y los servicios prestados con sus montos **para* documentar la venta de un servicio específico fuera del ciclo masivo de facturación. |
| *H.U. 06* | *Anular facturas* | Como *Operador de Facturación, quiero poder anular una factura emitida ingresando el motivo de la anulación **para* corregir un error o deshacer una operación contable de forma legal. |

---

## ✨ Iteraciones Futuras (Funcionalidad Avanzada)

### Historias de Usuario para futuras mejoras

| ID | Título | Rol | Historia de Usuario |
| :--- | :--- | :--- | :--- |
| *H.U. F01* | *Ver estado de cuenta* | Operador de Cobranzas | Como *Operador de Cobranzas, quiero ver el estado de cuenta de un cliente con el detalle de facturas emitidas, pagos recibidos y saldo pendiente **para* saber rápidamente qué facturas están pendientes de pago y el monto correspondiente. |
| *H.U. F02* | *Previsualizar facturas* | Operador de Facturación | Como *Operador de Facturación, quiero poder previsualizar el borrador de la factura con el cálculo del IVA incluido **para* asegurar que los montos, impuestos e información del cliente sean correctos antes de la emisión final. |
| *H.U. F03* | *Modificar estado de facturas* | Operador de Facturación | Como *Operador de Facturación, quiero modificar el estado de una factura (activa, anulada, pendiente, etc.) **para* mantener un estado actualizado de cada factura, según la condición que posea en ese momento. |
| *H.U. F04* | *Buscar clientes por identificador* | Administrador de Cuentas | Como *Administrador de Cuentas, quiero buscar clientes por CUIT o Razón Social **para* gestionar rápidamente la información de cuentas específicas. |

