# ManagementClub - Project Guide

## 1. Objetivo del proyecto

ManagementClub es una aplicación desarrollada con Java y Spring Boot para la gestión de un club deportivo canino.

El proyecto tiene un doble propósito:

- Resolver una necesidad real de gestión del club.
- Servir como proyecto de aprendizaje para aplicar buenas prácticas de desarrollo backend.

Durante el desarrollo se prioriza la comprensión del código, la mantenibilidad y la simplicidad frente al uso de herramientas que automaticen la implementación.

---

# 2. Objetivos técnicos

El proyecto persigue los siguientes objetivos:

- Desarrollar una API REST siguiendo buenas prácticas.
- Mantener una arquitectura limpia y sencilla.
- Aplicar correctamente Spring Boot y Spring Data JPA.
- Diseñar un modelo de dominio escalable.
- Comprender el funcionamiento interno del framework antes de incorporar herramientas de automatización.

---

# 3. Arquitectura

La aplicación sigue una arquitectura clásica por capas.

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

# 4. Tecnologías utilizadas

- Java 21
- Spring Boot
- Spring Data JPA
- Spring Validation
- H2 Database
- MySQL (futuro entorno de producción)
- Swagger / OpenAPI

---

# 5. Filosofía del proyecto

Antes de incorporar una nueva tecnología o patrón de diseño se valoran los siguientes aspectos:

- ¿Resuelve un problema real?
- ¿Reduce la complejidad?
- ¿Hace el código más mantenible?
- ¿Está justificado para el tamaño del proyecto?

Se evita introducir complejidad innecesaria.

---

# 6. Roadmap

## Sprint 1

- Gestión de Personas
- Swagger
- DTO
- Mapper
- Excepciones personalizadas
- Validaciones

## Sprint 2

- Gestión de Perros
- Relaciones JPA
- CRUD Perro

## Sprint 3

- Federaciones
- Competiciones
- Históricos

## Sprint 4

- Recibos
- Pagos
- Automatizaciones

## Sprint 5

- Seguridad
- Usuarios
- Roles
- JWT

---

# 7. Estado actual

El proyecto se encuentra en desarrollo activo.

Cada Sprint finaliza con:

- Revisión del código.
- Refactorización cuando aporte valor.
- Pruebas funcionales.
- Actualización de Swagger.
- Actualización de la documentación.

---

# 8. Sprint Review

## Sprint 1

### Funcionalidades implementadas

- CRUD completo de Personas.
- Cambio de estado mediante PATCH.
- Búsqueda por nombre y apellidos.
- Validaciones con Bean Validation.
- Swagger / OpenAPI.
- GlobalExceptionHandler.

### Lecciones aprendidas

- Arquitectura por capas.
- DTO y Entity.
- Optional.
- JPQL.
- Bean Validation.
- Responsabilidad de cada capa.