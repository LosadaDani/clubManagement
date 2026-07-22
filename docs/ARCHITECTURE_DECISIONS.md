# Architecture Decision Records (ADR)

Este documento recoge las principales decisiones de arquitectura adoptadas durante el desarrollo del proyecto.

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