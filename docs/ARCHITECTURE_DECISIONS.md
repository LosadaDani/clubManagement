# Architecture Decision Records (ADR)

Este documento recoge las principales decisiones de arquitectura adoptadas durante el desarrollo del proyecto.

| ADR | Decisión                                        |
|------|-------------------------------------------------|
| ADR-001 | DTO separados                                   |
| ADR-002 | Mapper manual                                   |
| ADR-003 | Sin Lombok                                      |
| ADR-004 | Validaciones en RequestDTO                      |
| ADR-005 | Constructor injection                           |
| ADR-006 | Excepciones personalizadas                      |
| ADR-007 | GlobalExceptionHandler                          |
| ADR-008 | Baja lógica de personas                         |
| ADR-009 | Simplicidad sobre complejidad                   |
| ADR-010 | SummaryDTO para relaciones                      |
| ADR-011 | Relaciones JPA mediante objetos                 |
| ADR-012 | Clasificación de DTO (Request/Response/Summary) |
| ADR-013 | Operaciones transaccionales                     |

---

## ADR-001 - DTO separados

### Decisión

Utilizar DTO independientes para Request y Response.

### Motivo

Controlar los datos de entrada y salida de la API sin exponer directamente las entidades JPA.

---

## ADR-002 - Mapper manual

### Decisión

Implementar los mappers manualmente.

### Motivo

Priorizar el aprendizaje y comprender el proceso completo de transformación entre Entity y DTO.

---

## ADR-003 - Sin Lombok

### Decisión

No utilizar Lombok.

### Motivo

Mantener el código explícito y comprender el funcionamiento generado automáticamente por la herramienta.

---

## ADR-004 - Bean Validation

### Decisión

Las validaciones de entrada se realizan exclusivamente en los RequestDTO.

Las entidades contienen únicamente restricciones relacionadas con la persistencia.

### Motivo

Separar claramente la validación de la API del modelo de persistencia.

---

## ADR-005 - Constructor Injection

### Decisión

Todas las dependencias se inyectan mediante constructor.

### Motivo

Favorece la inmutabilidad, facilita las pruebas y sigue las recomendaciones de Spring.

---

## ADR-006 - Excepciones personalizadas

### Decisión

Utilizar excepciones específicas para los errores de negocio.

### Motivo

Facilitar el mantenimiento y ofrecer respuestas HTTP coherentes.

---

## ADR-007 - GlobalExceptionHandler

### Decisión

Centralizar el tratamiento de excepciones.

### Motivo

Evitar duplicación de código y unificar las respuestas de error.

---

## ADR-008 - Baja lógica de Personas

### Decisión

Las personas nunca se eliminan físicamente.

### Motivo

Un antiguo socio puede reincorporarse al club y se desea conservar su histórico.

---

## ADR-009 - Simplicidad sobre complejidad

### Decisión

Solo se incorporarán nuevas tecnologías cuando aporten un beneficio claro al proyecto.

### Motivo

Mantener un proyecto comprensible, coherente y orientado al aprendizaje.

---

## ADR-010

### DTO de resumen para relaciones

Cuando una entidad deba devolver información de otra entidad relacionada, se utilizarán DTO específicos de resumen (Summary DTO) en lugar de devolver la entidad completa o únicamente su identificador.

Ejemplos:

- PersonSummaryDTO
- DogSummaryDTO (futuro)
- OrganizationSummaryDTO (futuro)

### Motivo

- Evita referencias circulares.
- Reduce el tamaño de las respuestas.
- Desacopla la API del modelo de persistencia.
- Expone únicamente la información necesaria.
- Facilita la evolución de la API.

---

## ADR-011

### Relaciones JPA

Las entidades se relacionarán mediante referencias a objetos, nunca mediante identificadores.

Ejemplo:

Correcto:

Dog
→ Person owner

Incorrecto:

Dog
→ Long ownerId

Los identificadores únicamente aparecerán en los DTO.

---

## ADR-012

### Tipos de DTO

El proyecto utiliza tres tipos de DTO:

- RequestDTO
- ResponseDTO
- SummaryDTO

Cada uno tiene una finalidad diferente.

---

## ADR-013 - Operaciones transaccionales

### Decisión

Las operaciones de negocio que impliquen múltiples modificaciones dependientes entre sí se ejecutarán dentro de una transacción mediante `@Transactional`.

### Motivo

Garantizar que todas las modificaciones se completen correctamente o que, en caso de producirse un error, no queden cambios parciales persistidos en la base de datos.

---

## ADR-014 — Cuotas hardcodeadas mediante lógica condicional, sin entidad de configuración

### Estado

Decisión tomada. Pendiente de implementación (issue aún no codificada).

### Contexto

El club aplica distintas cuotas según el tipo de membresía. Los importes de cada
cuota son valores fijos y conocidos de antemano, no configurables ni sujetos a
cambio frecuente.

### Decisión

Las cuotas se resuelven mediante lógica condicional en el código (según el tipo
de membresía de la persona), con los importes hardcodeados directamente. No se
crea una entidad de configuración (por ejemplo, `MembershipType` con un campo
`amount`) para representar estos importes.

Esta es una decisión de diseño permanente, no una solución temporal a sustituir
más adelante.

### Alternativa descartada

Modelar los tipos de membresía y sus importes como una entidad configurable en
base de datos. Se descarta por no aportar valor real dado el tamaño del proyecto:
los importes no cambian con frecuencia, y añadir una entidad de configuración solo
para esto introduciría complejidad sin necesidad (ver `PROJECT_OVERVIEW.md`:
"¿está justificado para el tamaño del proyecto?").

### Consecuencias

* Un cambio en el importe de una cuota requiere modificar código y desplegar,
  no una simple edición de datos.
* La lógica condicional de cuotas debe mantenerse centralizada (idealmente en
  el Service), no repetida en varios puntos del código.
* Si en el futuro cambiaran las condiciones actuales (frecuencia de cambio de
  importes, necesidad de gestión desde fuera del código), esta decisión debería
  revisarse explícitamente como una decisión nueva, no como continuación de esta.

---

## ADR-015 — Fetch type explícito LAZY en relaciones @ManyToOne

### Estado

Decisión tomada e implementada.

### Contexto

JPA establece `EAGER` como valor por defecto para relaciones `@ManyToOne` y `@OneToOne`,
mientras que `@OneToMany` y `@ManyToMany` ya usan `LAZY` por defecto. Con `EAGER`, cargar
una entidad implica cargar automáticamente todas sus relaciones `@ManyToOne`, aunque no se
necesiten en ese caso de uso. Este coste se acumula cuando una entidad tiene varias
relaciones (por ejemplo, `CompetitionLicense`, con relaciones a `Organization`, `Person`
y `Dog`).

### Decisión

Todas las relaciones `@ManyToOne` del proyecto se declaran explícitamente con
`fetch = FetchType.LAZY`, en lugar de depender del `EAGER` por defecto de JPA.

Las relaciones `@OneToMany` no requieren esta anotación explícita, ya que su valor
por defecto en JPA ya es `LAZY`.

### Consecuencias

* La carga de una entidad relacionada (`Person`, `Organization`, `Dog`, `Receipt`)
  solo ocurre cuando se accede explícitamente a ella, no de forma automática.
* El mapeo a DTO debe ocurrir dentro del Service, antes de que finalice la transacción
  implícita de Spring Data (o la explícita de un método `@Transactional`), para evitar
  `LazyInitializationException` al acceder a una relación `LAZY` fuera de sesión.
* No afecta a la arquitectura de relaciones mediante objetos ya definida en ADR-011.
* Aplica a todo código nuevo del proyecto: cualquier nueva relación `@ManyToOne` debe
  declarar `fetch = FetchType.LAZY` de forma explícita.

---

