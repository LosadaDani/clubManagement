# Domain Rules — Seguridad y roles

> Este archivo forma parte de las reglas de dominio del proyecto. Ver también el índice en `DOMAIN_RULES.md`.
>
> Estado: **esqueleto**. Las reglas se añadirán en estilo declarativo (igual que en
> el resto de `DOMAIN_*.md`) a medida que cada issue de seguridad del Sprint 5 se
> implemente y quede decidida, no de forma anticipada. Ver `ARCHITECTURE_DECISIONS.md`
> (ADR-016) para la decisión técnica de *cómo* se autentica — este documento recoge
> *qué puede hacer cada rol*, que es regla de negocio del club, no una decisión de
> arquitectura.

## Roles

Decisión tomada. Pendiente de implementación (Issue 2 del Sprint 5).

Existirán tres roles:

ADMIN: socios completos que pertenecen a la junta del club. Acceso completo a todas las funcionalidades del sistema.

USER: socios permanentes, abonados, y personas en formación de iniciación o formación permanente. Pueden ver y modificar sus propios datos personales, los de sus perros y sus licencias de competición. Pueden visualizar (sin modificar) sus propios recibos y líneas de recibo.

ENTRENADOR: socios permanentes que ejercen de formadores. Mismos permisos que USER, más la capacidad de dar de alta personas de formación iniciación y formación permanente.

### Asignación de roles

- Toda alta de usuario se crea siempre con rol USER por defecto, independientemente de lo que se envíe en la petición de alta.
- El cambio a otro rol (ADMIN o ENTRENADOR) es una operación distinta, restringida a usuarios con rol ADMIN.
- El rol ADMIN solo podrá asignarse a personas cuyo MembershipType sea FULL_PARTNER

*(Pendiente definir (backlog) — qué ocurre con el rol `ADMIN` de una persona si su `MembershipType` deja de ser `FULL_PARTNER` (por ejemplo, si deja la junta o cambia de tipo de membresía) — no
se contempla ninguna transición automática de rol por este motivo, queda fuera del alcance actual. El resto del mapeo entre roles y `MembershipType` (`USER`/`ENTRENADOR`) tampoco se deriva 
automáticamente; el rol se asigna de forma explícita al dar de alta o modificar las credenciales de la persona.*

## Autenticación

*(Pendiente — se rellenará al implementar la Issue 6, "Proteger endpoints": qué
rutas son públicas y cuáles requieren un usuario autenticado.)*

## Autorización por rol

*(Pendiente — se rellenará al implementar la Issue 7, "Restringir operaciones según
el rol", con el estilo: "el rol X puede/no puede hacer Y sobre Z".)*

---

## Referencias cruzadas

- La decisión técnica de autenticación (JWT) se documenta en `ARCHITECTURE_DECISIONS.md`
  (ADR-016).
- La relación entre roles y `MembershipType` de `Person` se documenta, cuando se
  decida, con referencia cruzada a `DOMAIN_PERSONAS.md`.
