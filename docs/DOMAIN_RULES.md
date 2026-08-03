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

## Futuras reglas

Este documento crecerá conforme se implementen nuevas funcionalidades:

- Federaciones.
- Competiciones.
- Recibos.
- Pagos.
- Usuarios y Seguridad.