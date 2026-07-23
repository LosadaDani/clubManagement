# Architecture Decision Records (ADR)

Este documento recoge las principales decisiones de arquitectura adoptadas durante el desarrollo del proyecto.

| ADR | Decisión |
|------|----------|
| ADR-001 | DTO separados |
| ADR-002 | Mapper manual |
| ADR-003 | Sin Lombok |
| ADR-004 | Validaciones en RequestDTO |
| ADR-005 | Constructor injection |
| ADR-006 | Excepciones personalizadas |
| ADR-007 | GlobalExceptionHandler |
| ADR-008 | Baja lógica de personas |
| ADR-009 | Simplicidad sobre complejidad |
| ADR-010 | SummaryDTO para relaciones |
| ADR-011 | Relaciones JPA mediante objetos |
| ADR-012 | Clasificación de DTO (Request/Response/Summary) |

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
- FederationSummaryDTO (futuro)

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