# Domain Rules — Perros

> Este archivo forma parte de las reglas de dominio del proyecto. Ver también el índice en `docs/DOMAIN_RULES.md`.

## Perros

- Todo perro pertenece a una única persona.
- El microchip identifica de forma única al perro.
- El microchip se almacena como String, es obligatorio, es único y tiene exactamente 15 dígitos numéricos.
- La fecha de nacimiento es obligatoria para comprobar si puede comenzar a competir, y debe ser anterior a la fecha actual.
- El sexo es opcional.
- La raza es opcional.
- El número de pedigree es opcional y es único. Si se envía vacío o solo con espacios se trata como "sin pedigree" y se guarda sin valor (`null`); varios perros sin pedigree pueden coexistir. La unicidad solo se comprueba entre números de pedigree informados.
- El número de federación no pertenece a Dog, sino a la futura entidad CompeticionPerro.
- Un perro puede modificar sus datos personales (nombre, raza, fecha de nacimiento, sexo, microchip y número de pedigree). 
- La actualización de un perro (`PUT /api/dogs/{id}`) sustituye el recurso completo: los campos opcionales que no se envíen (sexo, raza, número de pedigree) quedan sin valor.
- El propietario del perro no podrá modificarse mediante la funcionalidad de actualización. La cesión de un perro entre propietarios se implementará como un caso de uso específico para mantener el histórico de propietarios. Por simplicidad, la petición de actualización comparte DTO con el alta, así que el campo `ownerId` sigue siendo obligatorio en el cuerpo, pero su valor se ignora.

---

## Referencias cruzadas

- Las licencias de competición (`CompetitionLicense`) están asociadas obligatoriamente a un perro, dentro del binomio persona-perro, y las cesiones de perros entre propietarios conservan el histórico de licencias asociadas a cada binomio. Las reglas completas de licencias se documentan en `docs/DOMAIN_ORGANIZACIONES.md` (sección "Licencias de competición").
