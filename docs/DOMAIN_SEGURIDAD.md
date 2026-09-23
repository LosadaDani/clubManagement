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

*(Pendiente — de momento solo existe el rol `ADMIN`. El resto de roles reales del
club, y su relación con `MembershipType` de `Person`, están en backlog. Se añadirán
aquí como reglas declarativas cuando se decidan — ver `BACKLOG.md`.)*

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
