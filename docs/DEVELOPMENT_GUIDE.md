# Development Guide

## 1. Arquitectura por capas

### Controller

Responsabilidades:

- Recibir peticiones HTTP.
- Validar RequestDTO.
- Invocar al Service.
- Devolver ResponseEntity.

No contiene lógica de negocio.

Cuando un Controller implemente una interfaz `*ControllerDocs`, las
restricciones de Bean Validation aplicadas a los parámetros de los métodos
deberán mantener una configuración consistente entre la interfaz y la
implementación.

Las restricciones como `@Valid`, `@NotBlank`, `@NotNull`, etc. deberán
mantenerse en ambos lados cuando formen parte de la validación del método.

Las anotaciones propias de Spring MVC, como `@RequestBody`, `@PathVariable`,
`@RequestParam`, etc., se aplicarán en la implementación del Controller.

Si el Controller utiliza validación sobre parámetros de sus métodos, por
ejemplo mediante `@NotBlank` o `@NotNull`, deberá utilizar `@Validated`.

Esta configuración evita conflictos de Hibernate Validator al validar
métodos que sobrescriben los definidos en `*ControllerDocs`.

---

### Service

Responsabilidades:

- Implementar reglas de negocio.
- Validar condiciones funcionales.
- Lanzar excepciones.
- Utilizar Repositories y Mappers.

### Validación de reglas de negocio

Las reglas de negocio que no puedan expresarse mediante restricciones de la base de datos o anotaciones JPA deberán implementarse en la capa Service.

Ejemplos:

- Validación de periodos de vigencia.
- Comprobación de solapamiento entre licencias.
- Reglas temporales.
- Validaciones que involucren varias entidades.

No deben utilizarse restricciones JPA para resolver reglas de negocio que requieran comparar varios registros o aplicar lógica temporal. Estas validaciones deberán realizarse siempre en el Service.

---

### Repository

Responsabilidades:

- Acceso a base de datos.
- Consultas derivadas de Spring Data.
- Consultas JPQL cuando sea necesario.

No contiene lógica de negocio.

---

### Mapper

Responsabilidades:

- Conversión entre DTO y Entity.

No accede a base de datos.

---

### DTO

Se utilizan DTO independientes para Request y Response.

Nunca se exponen directamente las entidades JPA.

Cuando un ResponseDTO necesite información de otra entidad relacionada, se utilizará un SummaryDTO en lugar del ResponseDTO completo.

Mientras los casos de uso de creación y actualización compartan el mismo contrato de entrada, se reutilizará el mismo RequestDTO. Solo se crearán DTO específicos cuando las necesidades funcionales sean diferentes.

---

## 2. Convenciones del proyecto

- Constructor Injection.
- Mapper manual.
- DTO Request y Response separados.
- Bean Validation en RequestDTO.
- Excepciones personalizadas.
- Swagger documentado.
- Código limpio y legible.
- Operaciones transaccionales mediante `@Transactional` cuando una operación implique múltiples modificaciones persistentes.

---

## 3. Flujo de desarrollo

Cada nueva funcionalidad sigue el siguiente orden:

1. Revisar las reglas de dominio aplicables.
2. Revisar las decisiones arquitectónicas relacionadas.
3. Entity
4. Repository
5. DTO
6. Mapper
7. Service
8. Controller
9. Postman
10. Swagger
11. Pruebas con Postman
12. Revisión de código
13. Actualización de documentación cuando corresponda.
14. Commit
15. Cierre de la Issue

---

## 4. Definition of Done

Una historia se considera finalizada cuando:

- Funcionalidad implementada.
- Código revisado.
- Swagger actualizado.
- Pruebas completadas.
- Documentación actualizada cuando corresponda.
- Commit realizado.
- Issue cerrada.

---

## 5. Guía para asistentes IA

Las herramientas de IA utilizadas en el proyecto deben:

- Explicar antes de generar código.
- Justificar las recomendaciones.
- Mantener la arquitectura existente.
- Priorizar soluciones sencillas.

No deben proponer automáticamente:

- Lombok
- MapStruct
- Clean Architecture
- CQRS
- Microservicios
- Patrones innecesariamente complejos

El objetivo principal es comprender el funcionamiento de las tecnologías utilizadas.