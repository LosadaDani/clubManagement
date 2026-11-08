# ManagementClub

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5-6DB33F)
![JPA](https://img.shields.io/badge/JPA-Hibernate-blue)
![OpenAPI](https://img.shields.io/badge/OpenAPI-Swagger-85EA2D)
![Status](https://img.shields.io/badge/Status-In_Development-yellow)
![Sprint](https://img.shields.io/badge/Sprint-7-blue)


ManagementClub es una aplicación desarrollada con **Java** y **Spring Boot** para la gestión de un club deportivo canino.

Este proyecto nace con un doble objetivo:

- Desarrollar una aplicación real para gestionar el club.
- Aprender y aplicar buenas prácticas de desarrollo backend con Spring Boot.
---

## Estado del proyecto

🚧 En desarrollo

Actualmente se encuentran completados los Sprints 1, 2, 3, 4, 5 y 6.

El siguiente es el **Sprint 7 — Tests automáticos**.

### Funcionalidades implementadas

- Gestión completa de Personas.
- Cambio de estado de Personas mediante PATCH.
- Búsqueda de Personas por nombre y apellidos.
- Gestión completa de Perros.
- Relación entre Personas y Perros.
- Gestión de Organizaciones.
- Gestión de Licencias de competición.
- Control de solapamiento de periodos de vigencia de licencias.
- Histórico funcional de licencias.
- Recibos y líneas de recibo.
- Autenticación con JWT y control de acceso por roles (ADMIN, ENTRENADOR y USER).
- Web pública en catalán (inicio, el club, agility, escola y contacte), servida con Thymeleaf desde la propia aplicación.
- Validación de datos mediante Bean Validation.
- Manejo global de excepciones.
- Documentación OpenAPI / Swagger.
- Arquitectura por capas.
- DTOs Request / Response / Summary.
- Mappers manuales.
### La web publicada

La web que se ve en https://agilitymaresme.com es un proyecto aparte
(`agilitymaresme-web`): HTML estático subido a un hosting compartido que no
ejecuta Java. La web pública de este proyecto sigue desarrollándose por separado.
Un cambio en las plantillas de aquí no llega a la web publicada de forma
automática.
 
---

## Tecnologías

- Java 21
- Spring Boot 3.5
- Spring Data JPA
- Spring Security con JWT
- Spring Validation
- Thymeleaf
- H2 Database
- MySQL o PostgreSQL (futuro entorno de producción, a decidir en el Sprint 8)
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

La aplicación necesita la variable de entorno `JWT_SECRET` (clave con la que se firman los tokens).

Ejecutar la aplicación desde IntelliJ o mediante:

```bash
mvn spring-boot:run
```

Swagger estará disponible en:

```
http://localhost:8080/swagger-ui/index.html
```
 
---

## Documentación

La documentación del proyecto se encuentra en la carpeta **docs**.

- PROJECT_OVERVIEW.md -> Objetivos del proyecto, arquitectura, roadmap y alcance de cada Sprint.
- DEVELOPMENT_GUIDE.md -> Arquitectura por capas, convenciones del proyecto y flujo de desarrollo de cada funcionalidad.
- ARCHITECTURE_DECISIONS.md -> Decisiones de arquitectura (ADR) y su motivo.
- DOMAIN_PERSONAS.md, DOMAIN_PERROS.md, DOMAIN_ORGANIZACIONES.md y DOMAIN_RECIBOS.md -> Reglas de negocio por entidad.
- DOMAIN_SEGURIDAD.md -> Autenticación, roles y rutas públicas.
- FRONTEND.md -> Decisiones, alcance y diseño de la web pública (Sprint 6).
- BACKLOG.md -> Funcionalidades pendientes e ideas.
---

## Roadmap

- ✅ Sprint 1 - Gestión de Personas
- ✅ Sprint 2 - Gestión de Perros
- ✅ Sprint 3 - Organizaciones y competiciones
- ✅ Sprint 4 - Recibos
- ✅ Sprint 5 - Seguridad, usuarios, roles (JWT)
- ✅ Sprint 6 - Web pública
- ⏳ Sprint 7 - Tests automáticos
- ⏳ Sprint 8 - Base de datos de producción y perfiles
- ⏳ Sprint 9 - Backoffice
- ⏳ Sprint 10 - Dockerización y despliegue
- ⏳ Sprint 11 - Auditoria global
- ⏳ Sprint 12 - Mejora del frontend (React o Angular, por valorar)
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