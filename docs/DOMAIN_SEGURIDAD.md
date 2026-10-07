# Domain Rules — Seguridad y roles

> Este archivo forma parte de las reglas de dominio del proyecto. Ver también el índice en `DOMAIN_RULES.md`.
>
> Ver `ARCHITECTURE_DECISIONS.md` (ADR-016) para la decisión técnica de *cómo* se
> autentica — este documento recoge *qué puede hacer cada rol*, que es regla de
> negocio del club, no una decisión de arquitectura.

## Roles

Implementado (Issue #83 del Sprint 5 — enum `Role`).

Existirán tres roles:

ADMIN: socios completos que pertenecen a la junta del club. Acceso completo a todas las funcionalidades del sistema.

USER: socios permanentes, abonados, y personas en formación de iniciación o formación permanente. Pueden ver y modificar sus propios datos personales y los de sus perros (incluido darlos de alta). Pueden ver sus licencias de competición y darlas de alta para sus propios perros, pero no modificarlas ni borrarlas (la corrección es exclusiva de ADMIN). Pueden visualizar (sin modificar) sus propios recibos y líneas de recibo.

ENTRENADOR: socios permanentes que ejercen de formadores. Mismos permisos que USER, más la capacidad de dar de alta personas de formación iniciación y formación permanente.

### Mapeo con el código

El enum `Role` (paquete `model.enums`) usa nombres en inglés con el prefijo `ROLE_`,
requerido por la convención de Spring Security para `hasRole(...)`:

| Rol de negocio | Valor en código  |
|-----------------|-------------------|
| ADMIN           | `ROLE_ADMIN`      |
| USER            | `ROLE_USER`        |
| ENTRENADOR      | `ROLE_TRAINER`     |

El prefijo `ROLE_` se filtra tal cual al contrato de la API (campo `role` en las
respuestas JSON, por ejemplo `"ROLE_USER"`) — es un efecto colateral conocido y
aceptado de esta convención, no un error.

### Asignación de roles

- Toda alta de usuario se crea siempre con rol USER por defecto, independientemente de lo que se envíe en la petición de alta.
- El cambio a otro rol (ADMIN o ENTRENADOR) es una operación distinta, restringida a usuarios con rol ADMIN.
- El rol ADMIN solo podrá asignarse a personas cuyo MembershipType sea FULL_PARTNER

*(Pendiente definir (backlog) — qué ocurre con el rol `ADMIN` de una persona si su `MembershipType` deja de ser `FULL_PARTNER` (por ejemplo, si deja la junta o cambia de tipo de membresía) — no
se contempla ninguna transición automática de rol por este motivo, queda fuera del alcance actual. El resto del mapeo entre roles y `MembershipType` (`USER`/`ENTRENADOR`) tampoco se deriva 
automáticamente; el rol se asigna de forma explícita al dar de alta o modificar las credenciales de la persona.*

### Alta de usuario (credenciales)

Implementado (Issue #82 del Sprint 5).

Toda persona debe existir previamente en el sistema antes de poder tener un `AppUser` — la relación es obligatoria en el sentido `AppUser → Person`, nunca al revés. No existe el caso de un usuario sin persona asociada.

La contraseña no se recibe en la petición de alta: la genera el sistema de forma aleatoria en el momento de crear el `AppUser`, y se devuelve una única vez, en texto plano, en la respuesta de esa misma operación — para que quien da de alta al usuario (un `ADMIN`) pueda proporcionársela a la persona.

Validaciones de la operación de alta, en este orden: la persona indicada debe existir (404 si no), la persona no debe tener ya un usuario asignado (409), y el `username` solicitado no debe estar ya en uso (409).

*(Pendiente definir (backlog, prioridad alta): que la propia persona pueda cambiar su contraseña una vez tiene acceso, y forzar el cambio de contraseña en el primer login. Sin esto implementado, la contraseña generada por el sistema es la única que existe para ese usuario hasta que un ADMIN la regenere. La forma de generación actual (recorte de UUID) se revisará como parte de esa misma tarea.)*

## Autenticación

Implementado (Issues #56-#59 del Sprint 5 — login, generación de JWT, validación de JWT, protección de endpoints).

Login: `POST /api/auth/login`, público, recibe `username/password` y devuelve `username, role`, un resumen de la persona asociada y un token JWT (ver ADR-016 para el detalle técnico de generación y validación).

Rutas públicas (sin token): `/api/auth/login`, Swagger UI (`/swagger-ui/**`, `/swagger-ui.html`, `/v3/api-docs/**`) y la consola H2 (`/h2-console/**`, solo entorno de desarrollo).

Cualquier otra ruta exige un JWT válido en la cabecera `Authorization: Bearer <token>` (regla por defecto `anyRequest().authenticated()` en `SecurityConfig`). Sin token, o con uno inválido/caducado, la petición no queda autenticada.

Una petición sin autenticar a una ruta protegida responde `401` con el formato `ErrorResponseDTO` habitual de la API, mediante un `CustomAuthenticationEntryPoint` propio (en el paquete `security`) registrado en `SecurityConfig` vía `.exceptionHandling(...)`, sustituyendo al `Http403ForbiddenEntryPoint` por defecto de Spring Security.
## Autorización por rol

Implementado parcialmente — por entidad, a medida que se van completando las issues de la Sprint 5-6 ("Restringir operaciones según el rol").

Regla común a todas las entidades: un rechazo por rol (`hasRole`/`hasAnyRole` en `SecurityConfig`) y un rechazo por "propios datos" (`AccessDeniedException` lanzada desde el Service) responden ambos `403` con el mismo formato `ErrorResponseDTO`: el primero a través de un `CustomAccessDeniedHandler` propio registrado en `SecurityConfig`, el segundo a través de `GlobalExceptionHandler`.

### Organizaciones

Implementado (Issue #59).

ADMIN: puede crear (`POST`) y modificar (`PATCH`) organizaciones.

USER y ENTRENADOR: no pueden crear ni modificar organizaciones.

No hay restricción de "propios datos" aquí — una organización no pertenece a ningún socio.

### Personas

Implementado (Issue #84).

- ADMIN: acceso completo — puede crear, ver, listar, buscar y modificar cualquier persona, y cambiar el estado de membresía (`PATCH /{id}/status`).
- ENTRENADOR: puede crear (`POST`) personas únicamente con `MembershipType` de formación (`INITIATION_TRAINING` o `PERMANENT_TRAINING`); crear una persona con cualquier otro tipo de membresía está prohibido. Puede ver y modificar únicamente su propia persona (igual que USER). 
- USER: no puede crear personas. Puede ver (`GET /{id}`) y modificar (`PUT /{id}`) únicamente su propia persona — comparando el `id` solicitado contra la persona asociada a su propio `AppUser`, no contra el rol en sí. 
- USER y ENTRENADOR no pueden listar (`GET /api/persons`) ni buscar (`GET /api/persons/search`) personas — ambas operaciones quedan restringidas a ADMIN. 
- Cambiar el `MembershipType` de la propia persona (vía `PUT /{id}`) está prohibido para USER y ENTRENADOR, igual que cambiar el `MembershipStatus` — ambos campos se consideran decisiones administrativas, no datos que el socio controle sobre sí mismo. 
- Nadie, excepto ADMIN, puede cambiar el `MembershipStatus` de ninguna persona (`PATCH /{id}/status`), ni siquiera la propia. 
- La comprobación de "propios datos" (ownership) se hace antes de comprobar si el recurso existe: si el `id` solicitado no es el propio (y el rol no es ADMIN), se responde `403 sin revelar si esa persona existe o no.  Esta precedencia se aplica a peticiones con cuerpo válido: la validación del cuerpo (`@Valid`) se ejecuta antes que el Service, así que un `PUT` con cuerpo inválido sobre un perro ajeno o inexistente responde `400` con los errores de validación, igual para cualquier id (no revela si el perro existe).

*(Pendiente valorar (backlog): que ENTRENADOR pueda ver/listar personas en formación iniciación, más allá de su propio registro — por ahora fuera de alcance.)*

### Perros

Implementado  (Issue #85).

USER y ENTRENADOR tienen exactamente los mismos permisos sobre perros.

- ADMIN: acceso completo — puede dar de alta perros para cualquier propietario, ver cualquier perro, listar todos los perros (`GET /api/dogs`), buscar por nombre (`GET /api/dogs/name/{name}`) y por microchip (`GET /api/dogs/microchip/{microchip}`), consultar los perros de cualquier persona (`GET /api/dogs/person/{personId}`) y modificar cualquier perro (`PUT /{id}`).
- USER y ENTRENADOR: pueden dar de alta perros (`POST /api/dogs`) únicamente si el propietario indicado es su propia persona. Pueden ver (`GET /{id}`) y modificar (`PUT /{id}`) únicamente sus propios perros, y consultar los perros de una persona (`GET /person/{personId}`) únicamente si es su propia persona. La propiedad de un perro se determina por su propietario (`Person`), no por el usuario (`AppUser`) que hace la petición.
- USER y ENTRENADOR no pueden listar (`GET /api/dogs`) ni buscar por nombre o por microchip — esas tres operaciones quedan restringidas a ADMIN, incluso si el perro buscado es propio (la regla es por rol, no por propiedad).
- La comprobación de "propios datos" se hace antes de revelar si el recurso existe: si el perro, la persona o el propietario indicado no son propios (y el rol no es ADMIN), se responde `403` tanto si existen como si no. El `404` solo lo recibe ADMIN.
- El propietario de un perro no se puede modificar mediante la actualización (ver `DOMAIN_PERROS.md`): el `ownerId` enviado en un `PUT` se ignora. El traspaso entre propietarios será un caso de uso específico (pendiente, ver backlog).
- La actualización (`PUT /{id}`) sustituye el recurso completo: los campos opcionales que no se envíen (`sex`, `breed`, `pedigreeNumber`) quedan sin valor.

*(Riesgo asumido: al dar de alta o modificar un perro propio, USER y ENTRENADOR pueden deducir por la respuesta `409` si un microchip o número de pedigree ya está registrado en el club, aunque no puedan buscar por microchip. Solo se revela que existe, no a qué perro ni a qué propietario pertenece. Es inherente a la regla de unicidad: no se puede rechazar un duplicado sin indicar que lo es.)*

### Licencias — Issue #86

Pendiente de implementar (reglas acordadas; el código de licencias existe pero aún sin autorización). Las reglas de negocio de las licencias están en `DOMAIN_ORGANIZACIONES.md`.

USER y ENTRENADOR tienen exactamente los mismos permisos sobre licencias.

- ADMIN: puede crear licencias para cualquier binomio coherente (la persona debe ser el propietario del perro, también para ADMIN), consultar las licencias de cualquier perro (`GET /dog/{dogId}` y `GET /dog/{dogId}/current`) y corregir cualquier licencia (`PUT /{id}`). El borrado/anulación (solo ADMIN) no está implementado todavía (backlog).
- USER y ENTRENADOR: pueden crear licencias (`POST`) únicamente si la persona indicada es su propia persona y el perro indicado es suyo. Pueden consultar (`GET /dog/{dogId}` y `GET /dog/{dogId}/current`) únicamente las licencias de sus propios perros. **No pueden corregir ni borrar licencias**, ni siquiera las propias: la corrección queda restringida a ADMIN mediante `SecurityConfig` (`PUT /api/competition-licenses/**` con `hasRole("ADMIN")`), por lo que un USER o ENTRENADOR recibe `403` sea cual sea la licencia. El motivo es que la corrección podría usarse para renovar o alterar una licencia en lugar de registrar una nueva.
- La propiedad se determina por la persona (`Person`) propietaria del perro, no por el `AppUser`.
- Igual que en Personas y Perros, la comprobación de "propios datos" se hace antes de revelar si el recurso existe. Para USER y ENTRENADOR: si la persona o el perro indicados no son propios, se responde `403` tanto si existen como si no, y el `404` de persona o perro solo lo recibe ADMIN. Orden de comprobaciones de un alta de USER/ENTRENADOR: `@Valid` (400) → persona y perro propios (403) → organización existente (404) → reglas de negocio (fechas, solapamiento 409). La regla "el propietario del perro no coincide con la persona de la licencia" solo puede darse, por tanto, en peticiones de ADMIN.
- La corrección (`PUT /{id}`) usa un DTO propio con solo `licenseNumber`, `startDate` y `endDate`: organización, persona y perro de una licencia no se pueden modificar (ver `DOMAIN_ORGANIZACIONES.md`). Solo la ejecuta ADMIN, por lo que no necesita comprobación de "propios datos" en el Service.
- Verbo: la corrección pasa de `PATCH` a `PUT` (sustituye los campos corregibles completos, como en Personas y Perros); el matcher de `SecurityConfig` debe usar el mismo verbo.
- La visibilidad de las licencias es solo para ADMIN y la persona de la licencia (la propietaria actual del perro). El comportamiento tras una cesión (qué ve el propietario anterior) se decidirá al implementar la cesión (ver backlog).

### Recibos y líneas de recibo

Pendiente (issues separadas, aún no implementadas). Las reglas de "propios datos" previstas en la sección "Roles" de más arriba (solo lectura sobre sus propios recibos y líneas) se documentarán aquí cuando se implementen, con el mismo nivel de detalle que Personas y Perros.

---

## Referencias cruzadas

- La decisión técnica de autenticación (JWT) se documenta en `ARCHITECTURE_DECISIONS.md`
  (ADR-016).
- La relación entre roles y `MembershipType` de `Person` se documenta, cuando se
  decida, con referencia cruzada a `DOMAIN_PERSONAS.md`.
