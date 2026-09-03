# Domain Rules — Perros

> Este archivo forma parte de las reglas de dominio del proyecto. Ver también el índice en `docs/DOMAIN_RULES.md`.

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

## Referencias cruzadas

- Las licencias de competición (`CompetitionLicense`) están asociadas obligatoriamente a un perro, dentro del binomio persona-perro, y las cesiones de perros entre propietarios conservan el histórico de licencias asociadas a cada binomio. Las reglas completas de licencias se documentan en `docs/DOMAIN_ORGANIZACIONES.md` (sección "Licencias de competición").
