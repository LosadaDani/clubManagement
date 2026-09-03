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

---

## Referencias cruzadas

- La persona y el perro que forman el binomio de cada licencia se rigen por las reglas descritas en `docs/DOMAIN_PERSONAS.md` y `docs/DOMAIN_PERROS.md` respectivamente.
