# Development Guide

## 1. Arquitectura por capas

### Controller

Responsabilidades:

- Recibir peticiones HTTP.
- Validar RequestDTO.
- Invocar al Service.
- Devolver ResponseEntity.

No contiene lógica de negocio.

---

### Service

Responsabilidades:

- Implementar reglas de negocio.
- Validar condiciones funcionales.
- Lanzar excepciones.
- Utilizar Repositories y Mappers.

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

---

## 2. Convenciones del proyecto

- Constructor Injection.
- Mapper manual.
- DTO Request y Response separados.
- Bean Validation en RequestDTO.
- Excepciones personalizadas.
- Swagger documentado.
- Código limpio y legible.

---

## 3. Flujo de desarrollo

Cada nueva funcionalidad sigue el siguiente orden:

1. Entity
2. Repository
3. DTO
4. Mapper
5. Service
6. Controller
7. Swagger
8. Pruebas con Postman
9. Revisión de código
10. Commit
11. Cierre de la Issue

---

## 4. Definition of Done

Una historia se considera finalizada cuando:

- Funcionalidad implementada.
- Código revisado.
- Swagger actualizado.
- Pruebas completadas.
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