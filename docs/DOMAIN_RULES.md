# Domain Rules

## Personas

- Una persona puede tener varios perros.
- Las personas nunca se eliminan físicamente.
- La baja de una persona se realiza mediante MembershipStatus.

---

## Perros

- Todo perro pertenece a una única persona.
- El microchip identifica de forma única al perro.
- El microchip se almacena como String y es único.
- La fecha de nacimiento es obligatoria para comprobar si puede comenzar a competir.
- El sexo es opcional.
- La raza es opcional.
- El número de pedigree es opcional y es único.
- El número de federación no pertenece a Dog, sino a la futura entidad CompeticionPerro.
- Un perro puede modificar sus datos personales (nombre, raza, fecha de nacimiento, sexo, microchip y número de pedigree). 
- El propietario del perro no podrá modificarse mediante la funcionalidad de actualización. La cesión de un perro entre propietarios se implementará como un caso de uso específico para mantener el histórico de propietarios.

---

## Organizaciones

Las organizaciones representan las entidades emisoras de licencias para la competición.

Una organización puede ser, por ejemplo:

- Federación Catalana (FCAG)
- Real Sociedad Canina de España (RSCE)
- Real Federación Española de Caza (RFEC)

Las organizaciones podrán mantenerse mediante operaciones de administración (alta, modificación y baja), aunque se espera que su contenido cambie muy pocas veces.
Las organizaciones no se eliminarán físicamente del sistema.
En caso de que una organización deje de estar operativa, se conservará su registro para mantener el histórico de licencias y relaciones asociadas. La gestión de su disponibilidad para nuevas operaciones se realizará mediante un estado de actividad cuando esta funcionalidad sea implementada.

---

## Licencias de competición

Las licencias de competición permiten que un binomio formado por una persona y un perro participe en competiciones organizadas por una organización.

Cada licencia pertenece obligatoriamente a:

- una organización
- una persona
- un perro

Las licencias siempre estarán asociadas al binomio persona-perro.

Cada licencia tendrá una fecha de inicio y una fecha de fin de vigencia.

La vigencia de una licencia se determinará exclusivamente mediante dichas fechas.

No se almacenará un estado (activa, caducada, etc.), ya que esta información puede obtenerse a partir del periodo de vigencia.

La renovación de una licencia nunca modificará una licencia existente.

Cada renovación generará un nuevo registro con su propio periodo de vigencia, permitiendo conservar el histórico completo de licencias.

El número de licencia será asignado por la organización emisora.

El número de licencia no constituye un identificador único del sistema. Cada organización podrá seguir sus propias reglas de numeración, por lo que el mismo número podrá existir en organizaciones distintas o variar entre renovaciones.

No podrán existir dos licencias cuyos periodos de vigencia se solapen para el mismo binomio (persona-perro) dentro de una misma organización.

La modificación del propietario de un perro no forma parte de la gestión de licencias.

Las cesiones de perros se implementarán mediante una funcionalidad específica, conservando el histórico tanto de propietarios como de licencias asociadas a cada binomio.

---

## Recibos y líneas de recibo

El sistema permitirá gestionar los importes pendientes de cobro de las personas mediante líneas de recibo y recibos.

Durante el mes podrán añadirse líneas de recibo correspondientes a cuotas, gastos u otros conceptos que deban ser cobrados a una persona.

### Línea de recibo

Una línea de recibo representa un importe que debe ser cobrado a una persona.

Cada línea de recibo estará asociada obligatoriamente a una persona y tendrá:

- Una fecha.
- Un concepto.
- Un importe.
- Un estado.

Una línea de recibo podrá encontrarse en los siguientes estados:

- `PENDING`: la línea está pendiente de ser incluida en un recibo.
- `ISSUED`: la línea ha sido incluida en un recibo emitido.
- `PAID`: el importe correspondiente a la línea ha sido abonado.

Una línea podrá marcarse como `PAID` cuando haya sido abonada, independientemente del medio de pago utilizado.

Actualmente el sistema no registrará el medio mediante el cual se ha realizado el pago.

Las líneas que no sean incluidas en un recibo durante su preparación permanecerán en estado `PENDING` y podrán incluirse en un recibo posterior.

### Recibo

Un recibo representa un conjunto de importes emitidos conjuntamente para una persona.

Cada recibo estará asociado obligatoriamente a una persona y tendrá:

- Una fecha de emisión.
- Un importe total.
- Un estado.

Un recibo podrá encontrarse en los siguientes estados:

- `ISSUED`: el recibo ha sido generado y emitido.
- `PAID`: el recibo ha sido abonado.
- `RETURNED`: el recibo ha sido devuelto.

Un recibo se considerará pagado íntegramente.

Actualmente no se contempla el pago parcial de un recibo.

Cuando un recibo se marque como `PAID`, las líneas asociadas al recibo pasarán a estado `PAID`.

Cuando un recibo sea marcado como `RETURNED`, las líneas asociadas mantendrán su estado `ISSUED`.

### Relación entre recibos y líneas de recibo

Una persona podrá tener varios recibos y varias líneas de recibo.

Una línea de recibo podrá estar asociada como máximo a un único recibo.

Una línea en estado `PENDING` no estará asociada a ningún recibo.

Cuando una línea sea incluida en un recibo, quedará asociada a dicho recibo y pasará a estado `ISSUED`.

Una vez asociada a un recibo, una línea no podrá trasladarse ni reutilizarse en otro recibo.

La asociación entre una línea de recibo y un recibo se mantendrá desde la línea de recibo.

Las líneas correspondientes a un recibo podrán obtenerse consultando las líneas asociadas a dicho recibo.

### Preparación de recibos

La preparación de recibos se realizará a partir de las líneas de recibo que se encuentren en estado `PENDING`.

El sistema mostrará las líneas pendientes correspondientes a cada persona para permitir preparar el siguiente recibo.

Las líneas aparecerán inicialmente seleccionadas para su inclusión en el recibo.

Durante la preparación podrán deseleccionarse líneas que no deban incluirse en el recibo actual.

Las líneas deseleccionadas permanecerán en estado `PENDING` y estarán disponibles para futuras preparaciones.

Durante la preparación también se propondrá la cuota correspondiente al tipo de membresía de la persona.

La cuota se mostrará como una propuesta durante la preparación, pero no se creará como una línea de recibo hasta que el recibo sea generado.

Al generar el recibo:

- Se creará el recibo correspondiente.
- Se creará la línea correspondiente a la cuota, cuando proceda.
- Las líneas seleccionadas quedarán asociadas al recibo.
- Las líneas seleccionadas pasarán de estado `PENDING` a `ISSUED`.
- El importe total del recibo se calculará a partir de las líneas incluidas.

### Devolución de recibos

Cuando un recibo sea devuelto, el recibo pasará a estado `RETURNED`.

Las líneas asociadas al recibo original permanecerán asociadas a dicho recibo y conservarán su estado `ISSUED`, manteniendo así el histórico de los conceptos que formaban parte del recibo devuelto.

La devolución de un recibo generará una nueva línea de recibo en estado `PENDING`.

Esta nueva línea representará el importe pendiente correspondiente al recibo devuelto y tendrá un importe formado por:

- El importe total del recibo devuelto.
- La penalización correspondiente a la devolución, cuando sea aplicable.

La nueva línea podrá incluirse posteriormente en otro recibo como cualquier otra línea pendiente.

Las líneas originales del recibo devuelto no se incluirán nuevamente de forma individual en futuros recibos.

Si el importe pendiente derivado de un recibo devuelto es abonado por otra vía, la línea correspondiente podrá marcarse como `PAID`.

### Histórico

Las líneas de recibo y los recibos no se reutilizarán para representar nuevos cobros.

Cada recibo conservará históricamente las líneas que fueron emitidas con él.

En caso de devolución, el recibo original y las líneas asociadas se mantendrán para conservar el histórico.

Los nuevos intentos de cobro derivados de una devolución se representarán mediante nuevas líneas de recibo y, posteriormente, nuevos recibos.

---

## Futuras reglas

Este documento crecerá conforme se implementen nuevas funcionalidades:

- Usuarios y Seguridad.