# ManagementClub

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F)
![JPA](https://img.shields.io/badge/JPA-Hibernate-blue)
![OpenAPI](https://img.shields.io/badge/OpenAPI-Swagger-85EA2D)
![Status](https://img.shields.io/badge/Status-In_Development-yellow)
![Sprint](https://img.shields.io/badge/Sprint-2-blue)


ManagementClub es una aplicación desarrollada con **Java** y **Spring Boot** para la gestión de un club deportivo canino.

Este proyecto nace con un doble objetivo:

- Desarrollar una aplicación real para gestionar el club.
- Aprender y aplicar buenas prácticas de desarrollo backend con Spring Boot.

---

## Estado del proyecto

🚧 En desarrollo

Actualmente se ha completado el Sprint 1.

### Funcionalidades implementadas

- Gestión completa de Personas (CRUD)
- Cambio de estado mediante PATCH
- Búsqueda por nombre y apellidos
- Validación de datos con Bean Validation
- Manejo global de excepciones
- Documentación OpenAPI / Swagger
- Arquitectura por capas

---

## Tecnologías

- Java 21
- Spring Boot
- Spring Data JPA
- Spring Validation
- H2 Database
- Swagger / OpenAPI

---

## Arquitectura

El proyecto sigue una arquitectura clásica basada en capas:

```
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

Además utiliza:

- DTO
- Mapper
- Entity
- Excepciones personalizadas

---

## Ejecución

Clonar el repositorio:

```bash
git clone ...
```

Ejecutar la aplicación desde IntelliJ o mediante:

```bash
./mvnw spring-boot:run
```

Swagger estará disponible en:

```
http://localhost:8080/swagger-ui/index.html
```

---

## Documentación

La documentación técnica del proyecto puede consultarse en:

```
docs/PROJECT_GUIDE.md
```

---

## Roadmap

- ✅ Sprint 1 - Gestión de Personas
- 🚧 Sprint 2 - Gestión de Perros
- ⏳ Sprint 3 - Federaciones
- ⏳ Sprint 4 - Recibos
- ⏳ Sprint 5 - Seguridad (JWT)

---

## Autor

Proyecto desarrollado por Dani Losada como parte de su formación en Java y Spring Boot.