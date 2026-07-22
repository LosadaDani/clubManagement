# ManagementClub - Project Guide

## 1. Objetivo del proyecto

ManagementClub es una aplicación desarrollada con Java y Spring Boot cuyo objetivo es gestionar un club deportivo canino.

El proyecto tiene un doble propósito:

- Resolver un problema real de gestión del club.
- Servir como proyecto de aprendizaje para profundizar en Java, Spring Boot y buenas prácticas de desarrollo.

Por este motivo se prioriza la comprensión del código frente al uso de herramientas que automaticen su generación.

---

# 2. Objetivos técnicos

Este proyecto pretende servir como ejemplo de una aplicación Spring Boot desarrollada siguiendo buenas prácticas.

Los principales objetivos son:

- Código limpio y fácil de mantener.
- Arquitectura sencilla y escalable.
- Responsabilidad única de cada clase.
- Uso de patrones habituales en proyectos profesionales.
- Comprender el funcionamiento interno de Spring antes de utilizar herramientas automáticas.

---

# 3. Arquitectura

El proyecto sigue una arquitectura clásica basada en capas.

```
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

De forma transversal:

```
DTO
↕
Mapper
↕
Entity
```

Cada capa tiene una única responsabilidad.

---

# 4. Convenciones del proyecto

## Controllers

Responsabilidades:

- Recibir peticiones HTTP.
- Validar RequestDTO mediante Bean Validation.
- Invocar al Service.
- Devolver ResponseEntity.

No deben contener lógica de negocio.

---

## Services

Responsabilidades:

- Implementar las reglas de negocio.
- Validar condiciones funcionales.
- Lanzar excepciones personalizadas.
- Invocar Repositories.
- Utilizar Mappers.

---

## Repositories

Responsabilidades:

- Acceso a base de datos.
- Consultas derivadas de Spring Data.
- JPQL únicamente cuando aporte valor.

No deben contener lógica de negocio.

---

## Mappers

Responsabilidades:

- Conversión entre DTO y Entity.

No deben acceder a repositorios.

No deben contener reglas de negocio.

---

## DTO

Se utilizan DTO separados para Request y Response.

No se exponen directamente las entidades JPA.

---

# 5. Tecnologías utilizadas

- Java 21
- Spring Boot
- Spring Data JPA
- Spring Validation
- H2 Database
- Swagger / OpenAPI

Actualmente NO se utiliza:

- Lombok
- MapStruct
- Flyway
- Docker
- JWT (pendiente)
- MySQL (pendiente)

---

# 6. Decisiones de diseño

Se han tomado deliberadamente las siguientes decisiones:

- Mappers implementados manualmente.
- Constructores explícitos.
- Inyección por constructor.
- Excepciones personalizadas.
- GlobalExceptionHandler.
- DTO independientes.
- Validaciones mediante Bean Validation.

Estas decisiones buscan favorecer el aprendizaje y la comprensión del framework.

---

# 7. Filosofía del proyecto

Antes de introducir una nueva tecnología o patrón de diseño debe responderse:

- ¿Resuelve un problema real?
- ¿Reduce complejidad?
- ¿Hace el código más legible?
- ¿Está justificado para el tamaño del proyecto?

Se evita introducir complejidad innecesaria.

---

# 8. Roadmap

## Sprint 1

✔ CRUD Persona

✔ Swagger

✔ DTO

✔ Mapper

✔ GlobalExceptionHandler

✔ Validaciones

---

## Sprint 2

- Gestión de Perros
- Relación Persona → Perros
- CRUD Perro
- Diseño de relaciones JPA

---

## Sprint 3

- Federaciones
- Competiciones
- Históricos

---

## Sprint 4

- Recibos
- Pagos
- Automatizaciones

---

## Sprint 5

- Seguridad
- Usuarios
- Roles
- JWT

---

# 9. Guía para asistentes de IA

Si una IA analiza este proyecto debe tener en cuenta las siguientes normas.

## Prioridades

- Explicar antes que generar código.
- Justificar todas las recomendaciones.
- Priorizar simplicidad.
- Mantener la arquitectura existente.

## No proponer automáticamente

- Lombok
- MapStruct
- Clean Architecture
- CQRS
- Microservicios
- Patrones complejos

salvo que exista una justificación técnica clara.

El objetivo principal del proyecto es aprender.

---

# 10. Estado actual

El proyecto se encuentra en desarrollo activo.

Cada Sprint debe finalizar con:

- Revisión completa del código.
- Refactorización si aporta valor.
- Actualización de Swagger.
- Actualización de esta documentación.

# Sprint Review

## Sprint 1

### Objetivos alcanzados

- CRUD completo de Persona.
- Excepciones personalizadas.
- Swagger.
- Validaciones.
- Búsqueda por nombre y apellidos.
- Cambio de estado mediante PATCH.

### Decisiones importantes

- Uso de DTO separados.
- Mapper manual.
- GlobalExceptionHandler.
- No utilizar Lombok.
- No utilizar MapStruct.

### Lecciones aprendidas

- Diferencia entre Entity y DTO.
- Uso de Optional.
- orElseThrow.
- Bean Validation.
- JPQL.
- Responsabilidad de cada capa.