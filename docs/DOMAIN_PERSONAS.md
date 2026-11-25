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

- En el alta (`POST /api/persons`): si no se envía, se asigna la fecha del día (tomada del `Clock` inyectado, ver ADR-018).
- En la modificación (`PUT /api/persons/{id}`): si no se envía, se conserva la fecha que ya tiene la persona; si se envía, se actualiza. Así se puede corregir una fecha mal introducida, y enviar el campo vacío nunca la borra.
---

## Estado inicial en el alta

Implementado (comportamiento fijado por los tests de la Issue #111).

Toda persona se da de alta con `MembershipStatus = ACTIVE`. La petición de alta no incluye el estado, así que no hay forma de crear una persona con otro estado: el cambio a `INACTIVE` o `CANCELLED` es una operación posterior (ver «Cambio de estado»).
 
---

## Email único

Implementado (comportamiento fijado por los tests de la Issue #111).

El email identifica a la persona dentro del sistema y no puede repetirse.

- **Alta:** si ya existe una persona con ese email, se responde `409`.
- **Modificación:** solo se comprueba la unicidad cuando el email cambia. Si el nuevo email solo se diferencia del actual en mayúsculas o minúsculas (`A@x.com` → `a@x.com`), se considera el mismo email de la propia persona y no se consulta a la base de datos.
  *(Pendiente, ver `BACKLOG.md`, «Unicidad sensible a mayúsculas y espacios»: la consulta de unicidad distingue mayúsculas, mientras que la comparación con el email de la propia persona no. Hoy dos emails que solo se diferencian en mayúsculas pueden coexistir en personas distintas.)*

---

## Modificación de una persona

Implementado.

`PUT /api/persons/{id}` sustituye los datos personales (nombre, apellidos, teléfono, email, `memberSince` y tipo de membresía). **No modifica el estado de membresía**: la petición no lo incluye, y el estado solo cambia mediante `PATCH /api/persons/{id}/status`.

El cambio de tipo de membresía solo lo puede hacer un ADMIN. Las reglas de quién puede ver y modificar qué están en `DOMAIN_SEGURIDAD.md` (sección «Personas»).
 
---

## Cambio de estado

Implementado (comportamiento fijado por los tests de la Issue #111).

`PATCH /api/persons/{id}/status` cambia el `MembershipStatus` de una persona (`ACTIVE`, `INACTIVE` o `CANCELLED`). Solo puede invocarlo un ADMIN (matcher de `SecurityConfig`).

- Si la persona no existe, se responde `404`.
- Si el estado indicado es el que ya tiene la persona, no se guarda nada y se devuelve la persona tal cual.
- Hoy cualquier cambio de estado es posible para cualquier tipo de membresía: no hay transiciones prohibidas.
  *(Pendiente de decidir, ver `BACKLOG.md`: `PERMANENT_TRAINING` no puede tener estado `INACTIVE` según `DOMAIN_RECIBOS.md`, pero esa restricción no está implementada ni definida aquí. Hay que decidir también si afecta a `INITIATION_TRAINING`. Hasta que se decida y se documente aquí, no hay test de esta regla.)*

---

## Búsqueda por nombre

Implementado (comportamiento fijado por los tests de la Issue #111).

`GET /api/persons/search` (solo ADMIN) busca por nombre y apellidos. Antes de consultar, el texto se normaliza: se eliminan los espacios al principio y al final y los espacios repetidos entre palabras se reducen a uno.

*(El Service no defiende un texto `null`: el parámetro es obligatorio en el Controller.)*
 
---

## Alta de personas — curso de iniciación

Al dar de alta una persona con `MembershipType = INITIATION_TRAINING`, el sistema
genera automáticamente dos líneas de recibo (`ReceiptLine`) en estado `PENDING`:

- "1ª cuota Iniciación" — 150,00 €, con fecha la del alta.
- "2ª cuota Iniciación" — 100,00 €, con fecha un mes después del alta.
  Esto refleja la forma de pago habitual del club para cursos esporádicos:
  la primera cuota al inicio del curso, la segunda al mes siguiente.

Con cualquier otro tipo de membresía no se genera ninguna línea.
 
---

## Referencias cruzadas

- Las licencias de competición (`CompetitionLicense`) están asociadas obligatoriamente a una persona, dentro del binomio persona-perro. Las reglas completas de licencias se documentan en `docs/DOMAIN_ORGANIZACIONES.md` (sección "Licencias de competición").
- Durante la preparación de recibos se propone la cuota correspondiente al tipo de membresía de la persona (MembershipType). Las reglas completas de recibos y cuotas se documentan en `docs/DOMAIN_RECIBOS.md` (sección "Preparación de recibos").
- 