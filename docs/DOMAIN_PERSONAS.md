# Domain Rules — Personas

> Este archivo forma parte de las reglas de dominio del proyecto. Ver también el índice en `docs/DOMAIN_RULES.md`.

## Personas

- Una persona puede tener varios perros.
- Las personas nunca se eliminan físicamente.
- La baja de una persona se realiza mediante MembershipStatus.

---

## Fecha de alta (`memberSince`)

Implementado.

`memberSince` es la fecha en que la persona entró en el club. Es obligatoria en la base de datos, pero opcional en la petición, y no puede ser futura.

- En el alta (`POST /api/persons`): si no se envía, se asigna la fecha del día.
- En la modificación (`PUT /api/persons/{id}`): si no se envía, se conserva la fecha que ya tiene la persona; si se envía, se actualiza. Así se puede corregir una fecha mal introducida, y enviar el campo vacío nunca la borra.

---

## Alta de personas — curso de iniciación

Al dar de alta una persona con `MembershipType = INITIATION_TRAINING`, el sistema
genera automáticamente dos líneas de recibo (`ReceiptLine`) en estado `PENDING`:

- "1ª cuota Iniciación" — 150,00 €, con fecha la del alta.
- "2ª cuota Iniciación" — 100,00 €, con fecha un mes después del alta.

Esto refleja la forma de pago habitual del club para cursos esporádicos:
la primera cuota al inicio del curso, la segunda al mes siguiente.

---

## Referencias cruzadas

- Las licencias de competición (`CompetitionLicense`) están asociadas obligatoriamente a una persona, dentro del binomio persona-perro. Las reglas completas de licencias se documentan en `docs/DOMAIN_ORGANIZACIONES.md` (sección "Licencias de competición").
- Durante la preparación de recibos se propone la cuota correspondiente al tipo de membresía de la persona (MembershipType). Las reglas completas de recibos y cuotas se documentan en `docs/DOMAIN_RECIBOS.md` (sección "Preparación de recibos").
