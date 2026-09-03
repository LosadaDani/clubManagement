# Domain Rules — Recibos y líneas de recibo

> Este archivo forma parte de las reglas de dominio del proyecto. Ver también el índice en `docs/DOMAIN_RULES.md`.

## Recibos y líneas de recibo

El sistema permitirá gestionar los importes pendientes de cobro de las personas mediante líneas de recibo y recibos.

Durante el mes podrán añadirse líneas de recibo correspondientes a cuotas, gastos u otros conceptos que deban ser cobrados a una persona.

### Línea de recibo

Una línea de recibo representa un importe asociado a una persona que podrá ser incluido en un recibo.

Cada línea de recibo estará asociada obligatoriamente a una persona y tendrá:

- Una fecha.
- Un concepto.
- Un importe.
- Un estado.

El importe podrá ser positivo o negativo:

- Un importe positivo representa un cargo a la persona.
- Un importe negativo representa un abono a favor de la persona.

Una línea de recibo podrá encontrarse en los siguientes estados:

- `PENDING`: la línea está pendiente de ser incluida en un recibo.
- `ISSUED`: la línea ha sido incluida en un recibo emitido.
- `PAID`: el importe correspondiente a la línea ha sido abonado.

Una línea podrá modificarse o eliminarse únicamente mientras se encuentre en estado `PENDING`.

Una vez que una línea haya sido incluida en un recibo y pase a estado `ISSUED`, no podrá modificarse ni eliminarse.

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

Durante la preparación podrán incluirse líneas de importe negativo para aplicar abonos a favor de la persona.

El sistema mostrará las líneas pendientes correspondientes a cada persona para permitir preparar el siguiente recibo.

Si el importe del abono supera la cantidad que puede compensarse en el recibo actual, la parte no compensada deberá permanecer disponible para futuras preparaciones.

Las líneas aparecerán inicialmente seleccionadas para su inclusión en el recibo.

Durante la preparación podrán deseleccionarse líneas que no deban incluirse en el recibo actual.

Las líneas deseleccionadas permanecerán en estado `PENDING` y estarán disponibles para futuras preparaciones.

Durante la preparación también se propondrá la cuota correspondiente al tipo de membresía de la persona.

La cuota se mostrará como una propuesta durante la preparación, pero no se creará como una línea de recibo hasta que el recibo sea generado.

Los abonos se representarán mediante líneas de recibo con importe negativo.

Cuando un abono no pueda compensarse completamente en el recibo actual, la cantidad restante se representará mediante una nueva línea de recibo en estado `PENDING`.

La línea de abono original mantendrá su estado y asociación al recibo en el que haya sido incluida, sin modificarse ni reutilizarse.

El concepto podrá utilizarse para identificar el motivo del abono y, cuando corresponda, indicar que se trata de un abono parcial (AP).

Al generar el recibo:

- Se creará el recibo correspondiente.
- Se creará la línea correspondiente a la cuota, cuando proceda.
- Las líneas seleccionadas quedarán asociadas al recibo.
- Las líneas seleccionadas pasarán de estado `PENDING` a `ISSUED`.
- El importe total del recibo se calculará a partir de las líneas incluidas.

### Cuotas — periodicidad según tipo de membresía y estado

El campo `MembershipType` de `Person` (ver `docs/DOMAIN_PERSONAS.md`) determina si una
persona genera cuota periódica en la propuesta de recibos, y con qué frecuencia:

| `MembershipType`      | ¿Genera cuota periódica? | Frecuencia                          |
|------------------------|---------------------------|--------------------------------------|
| `INITIATION_TRAINING`  | No                         | —                                     |
| `PERMANENT_TRAINING`   | Sí                         | Mensual si activa / anual (enero) si inactiva |
| `SUBSCRIBED_MEMBER`    | Sí                         | Mensual si activa / anual (enero) si inactiva |
| `FULL_PARTNER`         | Sí                         | Mensual si activa / anual (enero) si inactiva |

> Nota: `PERMANENT_TRAINING` no puede tener `MembershipStatus` inactivo — es una
> restricción propia de `Person` (ver `docs/DOMAIN_PERSONAS.md`), no de este documento.
> Por tanto, en la práctica solo aplica la frecuencia mensual para este tipo.

La frecuencia depende del `MembershipStatus` de la persona (ver `docs/DOMAIN_PERSONAS.md`):

- **Persona activa**: se le propone la cuota correspondiente cada mes.
- **Persona inactiva**: se le propone la cuota correspondiente una vez al año, siempre
  en enero, independientemente de cuándo pasó a inactiva.

Para los tipos con cuota periódica (`PERMANENT_TRAINING`, `SUBSCRIBED_MEMBER`,
`FULL_PARTNER`), la cuota se propone independientemente de que la persona tenga o no
líneas `PENDING` en ese momento — el disparador es el tipo y estado de la persona, no
la existencia de importes pendientes previos.

`SUBSCRIBED_MEMBER` y `FULL_PARTNER` generan la misma cuota (mismo importe); la
distinción entre ambos es administrativa (los primeros 6 meses de socio) y no afecta
al cálculo del importe en ADR-014.

#### `INITIATION_TRAINING` — caso especial, sin cuota periódica

Las personas con `MembershipType = INITIATION_TRAINING` no generan cuota periódica.
Solo aparecen en la propuesta de recibos si tienen alguna `ReceiptLine` en estado
`PENDING` — correspondiente a las dos cuotas iniciales creadas automáticamente al alta
(ver `docs/DOMAIN_PERSONAS.md`). Si esas líneas ya fueron cobradas y la persona no ha
pasado a otro `MembershipType`, deja de aparecer en futuras propuestas.

> Pendiente de definir (backlog): qué ocurre con el `MembershipStatus`/`MembershipType`
> de una persona de `INITIATION_TRAINING` al cobrarse su última cuota, dado que aún
> podría convertirse en socio. No se realiza ninguna transición automática de estado
> por este motivo — queda fuera del alcance actual.

> Pendiente de definir (backlog): gestión de aplazamiento de cuotas — qué ocurre cuando
> una persona con cuota periódica no la genera o no la cobra en el mes/año que
> correspondía. No forma parte del alcance actual.

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

## Referencias cruzadas

- Cada línea de recibo y cada recibo están asociados obligatoriamente a una persona. Las reglas de la entidad Person se documentan en `docs/DOMAIN_PERSONAS.md`.
- La cuota propuesta durante la preparación de recibos depende del tipo de membresía de la persona (MembershipType); ver `docs/DOMAIN_PERSONAS.md`.
