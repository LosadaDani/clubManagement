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

Actualmente se encuentran completados los Sprints 1, 2 y 3.

El **Sprint 4 — Recibos y líneas de recibo** se encuentra actualmente en desarrollo.

### Funcionalidades implementadas

- Gestión completa de Personas.
- Cambio de estado de Personas mediante PATCH.
- Búsqueda de Personas por nombre y apellidos.
- Gestión completa de Perros.
- Relación entre Personas y Perros.
- Gestión de Organizaciones.
- Gestión de Licencias de competición.
- Validaciones de negocio.
- Control de solapamiento de periodos de vigencia de licencias.
- Histórico funcional de licencias.
- Validación de datos mediante Bean Validation.
- Manejo global de excepciones.
- Documentación OpenAPI / Swagger.
- Arquitectura por capas.
- DTOs Request / Response / Summary.
- Mappers manuales.

---

## Tecnologías

- Java 21
- Spring Boot
- Spring Data JPA
- Spring Validation
- H2 Database
- MySQL (futuro entorno de producción)
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

El objetivo es mantener una arquitectura sencilla, comprensible y adecuada al tamaño y necesidades del proyecto.

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

La documentación del proyecto se encuentra en la carpeta **docs**.

- PROJECT_GUIDE.md -> Define los objetivos del proyecto, su arquitectura, el roadmap y el alcance de cada Sprint.
- DEVELOPMENT_GUIDE.md -> Define las reglas funcionales y de negocio que debe cumplir la aplicación.
- DOMAIN_RULES.md -> Índice de las reglas de negocio por entidad, separadas en DOMAIN_PERSONAS.md, DOMAIN_PERROS.md, DOMAIN_ORGANIZACIONES.md y DOMAIN_RECIBOS.md.
- ARCHITECTURE_DECISIONS.md -> Define las convenciones, el flujo de desarrollo y los criterios utilizados para implementar nuevas funcionalidades.

---

## Roadmap

- ✅ Sprint 1 - Gestión de Personas
- ✅ Sprint 2 - Gestión de Perros
- ✅ Sprint 3 - Organizaciones y competiciones
- 🚧 Sprint 4 - Recibos
- ⏳ Sprint 5 - Seguridad, usuarios, roles (JWT)
- ⏳ Sprint 6 - Frontend público + backoffice
- ⏳ Sprint 7 - Auditoria global
- ⏳ Siguientes - Automatizaciones y nuevas mejoras

---

## Objetivo futuro

Una vez completado el backend y la primera versión del frontend, el proyecto podrá evolucionar incorporando nuevas funcionalidades según las necesidades reales del club.

Entre ellas:

- Automatización de procesos administrativos.
- Emails y notificaciones.
- Avisos de vencimientos.
- Estadísticas e informes.
- Exportación de datos.
- Mejoras del dashboard.

La primera versión del frontend priorizará la simplicidad y la posibilidad de publicar la web rápidamente. Posteriormente se evaluará su evolución o migración a un framework frontend como React o Angular.

---

## Autor

Proyecto desarrollado por Dani Losada como parte de su formación en Java y Spring Boot.