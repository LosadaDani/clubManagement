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

La renovación de una licencia no modificará una licencia existente.

Cada renovación generará un nuevo registro de licencia con su propio periodo de vigencia.

Esto permitirá conservar el histórico completo de las licencias emitidas.

El número de licencia será el asignado por la organización emisora.

No se asumirá que dicho número permanezca constante entre renovaciones, ya que algunas organizaciones pueden asignar un nuevo número de licencia.

No podrán existir dos licencias cuyos periodos de vigencia se solapen para:

- la misma organización
- la misma persona
- el mismo perro

La modificación del propietario de un perro no forma parte de la gestión de licencias.

Las cesiones de perros se implementarán mediante una funcionalidad específica, conservando el histórico de propietarios y de licencias asociadas a cada binomio.

---

## Futuras reglas

Este documento crecerá conforme se implementen nuevas funcionalidades:

- Competiciones.
- Recibos.
- Pagos.
- Usuarios y Seguridad.