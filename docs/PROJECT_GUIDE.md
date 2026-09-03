# ManagementClub - Project Guide

## 1. Objetivo del proyecto

ManagementClub es una aplicación desarrollada con Java y Spring Boot para la gestión de un club deportivo canino.

El proyecto tiene un doble propósito:

* Resolver una necesidad real de gestión del club.
* Servir como proyecto de aprendizaje para aplicar buenas prácticas de desarrollo backend.

Durante el desarrollo se prioriza la comprensión del código, la mantenibilidad y la simplicidad frente al uso de herramientas que automaticen la implementación.

---

# 2. Objetivos técnicos

El proyecto persigue los siguientes objetivos:

* Desarrollar una API REST siguiendo buenas prácticas.
* Mantener una arquitectura limpia y sencilla.
* Aplicar correctamente Spring Boot y Spring Data JPA.
* Diseñar un modelo de dominio escalable.
* Comprender el funcionamiento interno del framework antes de incorporar herramientas de automatización.

---

# 3. Arquitectura

La aplicación sigue una arquitectura clásica por capas.

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

De forma transversal:

```text
DTO
↕
Mapper
↕
Entity
```

Cada capa tiene una única responsabilidad.

---

# 4. Tecnologías utilizadas

* Java 21
* Spring Boot
* Spring Data JPA
* Spring Validation
* H2 Database
* MySQL (futuro entorno de producción)
* Swagger / OpenAPI

---

# 5. Filosofía del proyecto

Antes de incorporar una nueva tecnología o patrón de diseño se valoran los siguientes aspectos:

* ¿Resuelve un problema real?
* ¿Reduce la complejidad?
* ¿Hace el código más mantenible?
* ¿Está justificado para el tamaño del proyecto?

Se evita introducir complejidad innecesaria.

---

# 6. Roadmap

## Sprint 1 — Personas

* Gestión de Personas
* Entidad `Person`
* CRUD de personas
* DTOs
* Mapper
* Validaciones
* Excepciones personalizadas
* Swagger

**Estado: Completado**

## Sprint 2 — Perros

* Gestión de Perros
* Relación `Person → Dog`
* CRUD de perros
* Validaciones de relación propietario/perro
* DTOs
* Mapper
* Excepciones personalizadas
* Swagger

**Estado: Completado**

## Sprint 3 — Organizaciones y competiciones

* Gestión de Organizaciones
* Gestión de licencias de competición
* Relaciones entre:

    * Organización
    * Persona
    * Perro
* CRUD de licencias de competición
* Validaciones de negocio
* Control de solapamiento de periodos de vigencia
* Modificación de licencias
* Consulta de todas las licencias de un perro
* Consulta de licencias vigentes de un perro
* Histórico funcional de licencias de un perro mediante las propias licencias almacenadas
* DTOs
* Mapper
* Excepciones personalizadas
* Swagger

**Estado: Completado**

> El histórico funcional de licencias no requiere una entidad independiente de histórico. Las propias licencias, junto con sus periodos de vigencia y las consultas correspondientes, permiten conservar y consultar el historial de licencias de cada perro.

## Sprint 4 — Recibos y líneas de recibo

* Gestión de recibos.
* Gestión de líneas de recibo.
* Relación entre personas del club (`Person`) y recibos.
* Relación entre recibos y líneas de recibo.
* Registro de conceptos e importes asociados a una persona.
* Gestión del estado de las líneas de recibo.
* Generación de una propuesta de recibo a partir de las líneas pendientes.
* Inclusión de la cuota correspondiente como línea de recibo al generar el recibo.
* Selección y deselección de líneas durante la preparación del recibo.
* Posibilidad de aplazar líneas pendientes a un recibo posterior.
* Gestión de recibos devueltos.
* Generación de la penalización correspondiente a un recibo devuelto.
* Registro de líneas pendientes para su inclusión en recibos posteriores.
* Cálculo del importe total del recibo.
* Operaciones transaccionales con `@Transactional`.
* Validaciones de negocio.
* DTOs.
* Mapper.
* Excepciones personalizadas.
* Swagger / OpenAPI.

**Estado: En desarrollo**

> El sistema no implementará inicialmente una gestión contable de pagos ni una entidad independiente de Pago. El estado de pago se reflejará mediante los estados de Recibo y LíneaRecibo. El medio utilizado para realizar el pago no forma parte del modelo actual.

> La preparación de recibos se realizará a partir de las líneas pendientes. La consulta de preparación no generará ni modificará datos; la cuota se incorporará como LíneaRecibo únicamente cuando el recibo sea generado definitivamente.
## Sprint 5 — Seguridad

* Gestión de usuarios
* Relación entre usuarios y personas, según el modelo definitivo
* Gestión de roles
* Autenticación
* JWT
* Autorización
* Protección de endpoints
* Configuración de Spring Security
* Gestión de errores de autenticación y autorización

**Estado: Pendiente**

## Sprint 6 — Frontend

### Web pública / comercial

* Página principal
* Información del club
* Actividades
* Competiciones
* Información de contacto
* Noticias o comunicaciones, si se consideran necesarias
* Diseño responsive

### Backoffice

* Autenticación
* Dashboard
* Gestión de personas
* Gestión de perros
* Gestión de organizaciones
* Gestión de licencias
* Gestión de recibos
* Gestión de líneas de recibo
* Gestión de usuarios
* Consultas, filtros y navegación

**Estado: Pendiente**

> El frontend tendrá dos ámbitos diferenciados: una web pública orientada a visitantes y una interfaz de gestión privada para usuarios autorizados del sistema.
>
> El objetivo inicial de este sprint será disponer de una web sencilla y publicable para el club.
> 
> La primera versión se desarrollará utilizando una solución sencilla que permita poner en producción la web sin introducir todavía un framework frontend que pueda condicionar el aprendizaje posterior.

## Sprint 7 — Auditoría

* Auditoría transversal de las entidades
* Registro de creación de entidades
* Registro de modificaciones
* Registro de eliminaciones
* Identificación del usuario que realiza la operación
* Fecha y hora de la operación
* Identificación de la entidad afectada
* Identificación del registro afectado
* Registro de valores anteriores y nuevos cuando corresponda
* Consulta del historial de auditoría
* Evaluación y elección de la estrategia técnica de auditoría:

    * Hibernate Envers
    * Sistema de auditoría propio
    * Otra alternativa adecuada

**Estado: Pendiente**

> La auditoría es independiente del histórico funcional de las entidades. Su objetivo es proporcionar trazabilidad sobre las operaciones realizadas por los usuarios y facilitar la investigación de errores o incidencias.


## Fase posterior — Mejoras y nuevas funcionalidades

Una vez completados los sprints principales se realizará una revisión global del proyecto para identificar nuevas necesidades y priorizar futuras funcionalidades.

Posibles líneas de trabajo:

* Automatización de procesos
* Generación automática de recibos
* Emails y notificaciones automáticas
* Avisos de vencimientos
* Estadísticas
* Informes
* Exportación de datos
* Mejoras del dashboard
* Automatizaciones administrativas
* Nuevas funcionalidades derivadas del uso real de la aplicación

**Estado: Por definir**

> Las funcionalidades de esta fase no forman parte de los sprints actuales. Se definirán y priorizarán después de completar el roadmap principal.

---

# 7. Estado actual

El proyecto se encuentra en desarrollo activo.

Cada Sprint finaliza con:

* Revisión del código.
* Refactorización cuando aporte valor.
* Pruebas funcionales.
* Actualización de Swagger.
* Actualización de la documentación.

Actualmente se encuentran completados los Sprints 1, 2 y 3.

El Sprint 4 - Recibos se encuentra actualmente en desarrollo.

---

# 8. Sprint Review

## Sprint 1

### Funcionalidades implementadas

* CRUD completo de Personas.
* Cambio de estado mediante PATCH.
* Búsqueda por nombre y apellidos.
* Validaciones con Bean Validation.
* Swagger / OpenAPI.
* GlobalExceptionHandler.

## Sprint 2

### Funcionalidades implementadas

* CRUD completo de Perros.
* Relación entre Persona y Perro mediante JPA.
* Consulta de perros asociados a una persona.
* Validaciones de existencia del propietario.
* Validaciones mediante Bean Validation.
* DTOs de Request y Response.
* Mapper para conversión entre DTO y Entity.
* Excepciones personalizadas.
* GlobalExceptionHandler.
* Documentación de endpoints mediante Swagger / OpenAPI.

### Reglas de negocio

* Un perro debe estar asociado a una persona existente.
* Una persona puede tener varios perros.
* La relación propietario-perro se valida desde la capa de Service.

## Sprint 3

### Funcionalidades implementadas

* CRUD de Organizaciones.
* CRUD de Licencias de competición.
* Relación entre Organización, Persona y Perro.
* Validación de existencia de las entidades relacionadas.
* Validación de que el perro pertenece a la persona indicada.
* Validación de periodos de vigencia.
* Control de solapamiento de licencias.
* Actualización de licencias mediante PATCH.
* Exclusión de la licencia actual en la comprobación de solapamientos durante una actualización.
* Consulta de todas las licencias de un perro.
* Consulta de licencias vigentes de un perro.
* Histórico funcional de licencias mediante el conjunto de licencias asociadas al perro.
* DTOs de Request y Response.
* Mapper para conversión entre DTO y Entity.
* Excepciones personalizadas.
* GlobalExceptionHandler.
* Documentación de endpoints mediante Swagger / OpenAPI.

### Reglas de negocio

* Una licencia debe estar asociada a una organización, una persona y un perro existentes.
* El perro debe pertenecer a la persona indicada en la licencia.
* La fecha de inicio debe ser anterior a la fecha de fin.
* No pueden existir licencias incompatibles por solapamiento de periodos según las reglas definidas para el dominio.
* Al actualizar una licencia, la comprobación de solapamiento excluye la propia licencia modificada.
* Las licencias existentes se conservan para poder consultar el histórico de licencias de un perro.

### Decisiones de diseño

El histórico funcional de las licencias no requiere una entidad independiente. Cada licencia representa un periodo de vigencia y permanece almacenada una vez finalizado dicho periodo, permitiendo consultar tanto todas las licencias de un perro como sus licencias vigentes.

La auditoría de modificaciones de las entidades se ha separado del histórico funcional y se implementará posteriormente como un sistema transversal dentro del Sprint 6.

## Sprint 4

### Objetivo

Implementar la gestión de recibos del club, permitiendo acumular durante el mes líneas pendientes de cobro y preparar posteriormente los recibos que serán emitidos.

### Alcance

* Gestión de LíneaRecibo.
* Gestión de Recibo.
* Asociación de líneas a personas.
* Asociación de recibos a personas.
* Preparación de recibos mediante consulta de líneas pendientes.
* Selección de líneas que se incluirán en cada recibo.
* Incorporación de la cuota correspondiente.
* Generación definitiva del recibo y cálculo de su importe total.
* Gestión de estados de recibos y líneas de recibo.
* Gestión de recibos devueltos mediante la generación de una nueva línea pendiente.
* Operaciones transaccionales para las operaciones que impliquen múltiples modificaciones.

### Reglas de negocio

Las reglas funcionales de Recibos y LíneaRecibo se encuentran definidas en `docs/DOMAIN_RECIBOS.md`.

### Decisiones de diseño

La relación entre Recibo y LíneaRecibo se implementará mediante una referencia desde LíneaRecibo hacia Recibo, sin utilizar una entidad intermedia.

La preparación del recibo no modificará datos. Las líneas seleccionadas se asociarán al recibo únicamente durante su generación definitiva.

Las operaciones que impliquen múltiples modificaciones dependientes se ejecutarán dentro de una transacción mediante `@Transactional`.

### Estado

En desarrollo.