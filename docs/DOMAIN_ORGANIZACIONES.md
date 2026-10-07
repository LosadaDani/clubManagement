# Domain Rules — Organizaciones y Licencias de competición

> Este archivo forma parte de las reglas de dominio del proyecto. Ver también el índice en `docs/DOMAIN_RULES.md`.

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

### Campos y validaciones

- `organizationId`, `personId`, `dogId`: obligatorios. La organización, la persona y el perro deben existir (404 si no).
- `licenseNumber`: obligatorio, no vacío, máximo 50 caracteres.
- `startDate`, `endDate`: obligatorias.
- La fecha de inicio no puede ser posterior a la de fin (pueden ser iguales).

### Persona de la licencia

La persona de la licencia debe ser el **propietario actual del perro**. Esta regla se aplica a todos los roles, incluido ADMIN.

La única excepción prevista es la futura funcionalidad de cesión de perros: cuando un perro se ceda, existirá una licencia para el binomio formado por el perro y la persona a la que se cede. Las cesiones quedan fuera del alcance de la gestión actual de licencias (la cesión a una persona de otro club se decidirá cuando se implemente).

### Solapamiento de vigencia

Se considera que dos licencias de la misma organización, persona y perro se solapan cuando comparten al menos un día: `inicio1 <= fin2` y `inicio2 <= fin1` (límites **inclusivos**: el día de fin cuenta como vigente, igual que en la consulta de licencias vigentes). Por ejemplo, una licencia que termina el 31/12 y otra que empieza el 31/12 se solapan; la siguiente puede empezar el 1/1.

La comprobación se aplica al crear y al corregir una licencia (en la corrección, excluyendo la propia licencia). Si existe solapamiento se responde `409`.

### Duración por organización

*(Regla de negocio documentada; su aplicación automática queda pendiente, ver `BACKLOG.md`. Por ahora el sistema solo valida que `startDate` no sea posterior a `endDate`.)*

En la práctica las licencias son anuales para las organizaciones con las que trabaja el club:

- FCAG: empieza siempre el 1 de septiembre y termina el 31 de agosto del año siguiente. *(Pendiente de confirmar.)*
- RSCE: un año desde la fecha de inicio (termina el día anterior al primer aniversario).
- Otras organizaciones (por ejemplo RFEC): sin regla definida por ahora.

### Corrección de una licencia

La corrección existe únicamente para subsanar errores al registrar la licencia, no para renovarla ni para alterarla. Una licencia nueva (renovación) siempre es un registro nuevo.

- Solo ADMIN puede corregir licencias. Se asume que ADMIN aplica las reglas anteriores; el front deberá advertir de que la corrección solo debe usarse para errores.
- Solo se pueden corregir el número de licencia y las fechas de inicio y fin. La organización, la persona y el perro de una licencia **no se pueden modificar**: si se registró con una persona, un perro o una organización erróneos, la licencia debe anularse (borrarse) y crearse otra. La anulación/borrado (solo ADMIN) no está implementada todavía (ver `BACKLOG.md`).
- Se vuelven a validar el orden de las fechas y el solapamiento.

### Visibilidad

Solo pueden ver una licencia ADMIN y la persona de la licencia (rol USER o ENTRENADOR).

### Endpoints actuales (`/api/competition-licenses`)

- `POST /` — crear una licencia.
- `GET /dog/{dogId}` — todas las licencias de un perro.
- `GET /dog/{dogId}/current` — licencias vigentes hoy de un perro.
- `PUT /{id}` — corregir una licencia (solo ADMIN; hoy implementado como `PATCH`, pendiente de cambiar en la Issue #86).

Las reglas de quién puede invocar cada operación están en `DOMAIN_SEGURIDAD.md`.

---

## Referencias cruzadas

- La persona y el perro que forman el binomio de cada licencia se rigen por las reglas descritas en `docs/DOMAIN_PERSONAS.md` y `docs/DOMAIN_PERROS.md` respectivamente.
- Las reglas de autorización por rol de las licencias están en `DOMAIN_SEGURIDAD.md` (sección "Licencias — Issue #86").