# Guía de Prompts para IA

Este documento recopila los prompts utilizados durante el desarrollo del proyecto.

El objetivo no es generar código automáticamente, sino utilizar la IA como un compañero de revisión que ayude a mejorar el diseño, detectar errores y proponer refactorizaciones justificadas.

## Nota

Estos prompts están diseñados para ser utilizados con asistentes de IA integrados en el IDE.

Actualmente se asume que la IA puede acceder al archivo AI_PROMPTS.md y, desde él, al resto de la documentación del proyecto.

Si durante el uso se comprueba que la IA no es capaz de acceder a estos documentos, los prompts deberán modificarse para incluir explícitamente el contexto necesario.

---

# Contexto común

Antes de ejecutar cualquiera de estos prompts, utiliza como contexto la documentación del proyecto ubicada en `docs/`, especialmente:

- PROJECT_GUIDE.md
- DEVELOPMENT_GUIDE.md
- DOMAIN_RULES.md
- ARCHITECTURE_DECISIONS.md

Si no puedes acceder a alguno de estos documentos, indícalo antes de realizar el análisis.

Durante la revisión debes:

- Adaptar las recomendaciones al tamaño y objetivos del proyecto.
- No proponer automáticamente:
    - Lombok
    - MapStruct
    - Clean Architecture
    - CQRS
    - Microservicios
    - Patrones complejos
- Justificar siempre cualquier propuesta de mejora.

Para cada observación indica:

- Gravedad.
- Motivo.
- Propuesta.
- Si merece la pena implementarla en este proyecto.

Si no detectas ninguna mejora relevante, indícalo explícitamente y explica brevemente por qué consideras que la implementación es correcta.

---

# 1. Revisión de una clase

Objetivo

Realizar una revisión general de una clase siguiendo las normas del proyecto.

Prompt

```text
Utiliza como contexto el archivo AI_PROMPTS.md del proyecto.

Si puedes acceder a él, aplica el contexto común definido en ese documento antes de realizar el análisis.

Si no puedes acceder al archivo, indícalo antes de continuar.

Revisa esta clase.
```

---

# 2. Revisión de una Entity

Objetivo

Validar el modelado JPA.

Prompt

```text
Utiliza como contexto el archivo AI_PROMPTS.md del proyecto.

Si puedes acceder a él, aplica el contexto común definido en ese documento antes de realizar el análisis.

Si no puedes acceder al archivo, indícalo antes de continuar.

Revisa esta Entity JPA.

Comprueba:

- Relaciones.
- Restricciones de persistencia.
- Uso correcto de anotaciones JPA.
- Modelado del dominio.
- Cumplimiento de DOMAIN_RULES.md.

No analices DTO ni Service.
```

---

# 3. Revisión de un Repository

Prompt

```text
Utiliza el contexto común definido en AI_PROMPTS.md y revisa este Repository.

Comprueba:

- Uso correcto de JpaRepository.
- Nombres de los métodos siguiendo las convenciones de Spring Data JPA.
- Consultas @Query únicamente cuando sean necesarias.
- Ausencia de lógica de negocio.
- Consultas redundantes o innecesarias.
- Legibilidad.

No analices Entity, Service ni Controller.

No propongas nuevas tecnologías.

Indica únicamente mejoras que realmente aporten valor para este proyecto.
```

---

# 3. Revisión de un Service

Objetivo

Comprobar que la lógica de negocio está correctamente implementada.

Prompt

```text
Utiliza como contexto el archivo AI_PROMPTS.md del proyecto.

Si puedes acceder a él, aplica el contexto común definido en ese documento antes de realizar el análisis.

Si no puedes acceder al archivo, indícalo antes de continuar.

Revisa únicamente este Service.

Comprueba:

- Responsabilidades.
- Reglas de negocio.
- Uso correcto de Repository.
- Uso correcto del Mapper.
- Validaciones.
- Código duplicado.
- Posibles refactorizaciones.

No propongas cambios de arquitectura.

No propongas mover lógica a otras capas salvo que exista un error claro.
```

---

# 4. Revisión de un Controller

Objetivo

Comprobar que el Controller únicamente coordina la petición HTTP.

Prompt

```text
Utiliza como contexto el archivo AI_PROMPTS.md del proyecto.

Si puedes acceder a él, aplica el contexto común definido en ese documento antes de realizar el análisis.

Si no puedes acceder al archivo, indícalo antes de continuar.

Revisa este Controller.

Comprueba:

- Uso correcto de ResponseEntity.
- Uso de @Valid.
- Códigos HTTP.
- Delegación correcta al Service.
- Ausencia de lógica de negocio.
- Documentación Swagger.

No analices Repository ni Entity.
```

---

# 5. Revisión de un Mapper

Objetivo

Verificar que el Mapper únicamente transforma objetos.

Prompt

```text
Utiliza como contexto el archivo AI_PROMPTS.md del proyecto.

Si puedes acceder a él, aplica el contexto común definido en ese documento antes de realizar el análisis.

Si no puedes acceder al archivo, indícalo antes de continuar.

Revisa este Mapper.

Comprueba:

- Conversión correcta entre DTO y Entity.
- Ausencia de lógica de negocio.
- No realiza consultas a repositorios.
- Legibilidad.
- Posibles mejoras.

No propongas MapStruct.
```

---

# 6. Buscar oportunidades de refactorización

Objetivo

Detectar código repetido.

Prompt

```text
Utiliza como contexto el archivo AI_PROMPTS.md del proyecto.

Si puedes acceder a él, aplica el contexto común definido en ese documento antes de realizar el análisis.

Si no puedes acceder al archivo, indícalo antes de continuar.

Analiza esta clase buscando únicamente código duplicado.

No cambies su funcionalidad.

No propongas nuevas tecnologías.

Busca únicamente métodos privados que puedan extraerse para mejorar la reutilización y la legibilidad.

Justifica cada propuesta.
```

---

# 7. Revisión de una Pull Request

Objetivo

Simular una revisión de código entre compañeros.

Prompt

```text
Utiliza como contexto el archivo AI_PROMPTS.md del proyecto.

Si puedes acceder a él, aplica el contexto común definido en ese documento antes de realizar el análisis.

Si no puedes acceder al archivo, indícalo antes de continuar.

Actúa como si fueras un compañero realizando una Pull Request Review.

No propongas nuevas tecnologías.

Revisa únicamente:

- Calidad del código.
- Legibilidad.
- Arquitectura.
- Responsabilidad de las clases.
- Posibles bugs.
- Código duplicado.
- Refactorizaciones justificadas.

No reescribas el código.

Indica únicamente observaciones relevantes.
```

---

# 8. Validación contra la documentación

Objetivo

Comprobar que una implementación respeta todas las decisiones del proyecto.

Prompt

```text
Utiliza como contexto el archivo AI_PROMPTS.md del proyecto.

Si puedes acceder a él, aplica el contexto común definido en ese documento antes de realizar el análisis.

Si no puedes acceder al archivo, indícalo antes de continuar.

Verifica únicamente que la implementación respeta las decisiones del proyecto:

No analices estilo.

No analices rendimiento.
```

---

# 9. Revisión final de una Issue

Objetivo

Revisar una funcionalidad completa antes de cerrar la Issue.

Prompt

```text
Utiliza como contexto el archivo AI_PROMPTS.md del proyecto.

Si puedes acceder a él, aplica el contexto común definido en ese documento antes de realizar el análisis.

Si no puedes acceder al archivo, indícalo antes de continuar.

Verifica únicamente que la implementación respeta las decisiones del proyecto, anotaciones de cada capa y revisa toda la implementación de la Issue de .

Analiza:

- Entity
- Repository
- DTO
- Mapper
- Service
- Controller
- Swagger

Analiza las anotaciones:

- Entity → @Entity, @Table, relaciones JPA, etc.
- Repository → extensión correcta de JpaRepository.
- Service → @Service.
- Controller → @RestController, @RequestMapping, mappings HTTP, @Valid, @RequestBody, @PathVariable, etc.
- Swagger → @Tag, @Operation, @ApiResponses.
- DTO → validaciones Bean Validation.
- Mapper → @Component.

Comprueba que la implementación cumple la documentación del proyecto y las reglas de negocio.

Indica únicamente mejoras que realmente aporten valor para este proyecto.
```

---

# Explicación de una propuesta

Objetivo

Comprender una recomendación realizada por la IA antes de implementarla.

Prompt

```text
Utiliza como contexto el archivo AI_PROMPTS.md del proyecto.

Si puedes acceder a él, aplica el contexto común definido en ese documento antes de realizar el análisis.

Si no puedes acceder al archivo, indícalo antes de continuar.

Explica esta propuesta como si estuvieras enseñando a un desarrollador junior.

No escribas código inicialmente.

Explica:

- Qué problema intenta resolver.
- Por qué es una mejora.
- Qué ventajas aporta.
- Qué inconvenientes tiene.
- Si merece la pena aplicarla en este proyecto.
```

---

# Comparar dos implementaciones

Objetivo

Elegir la mejor solución cuando existen dos implementaciones distintas.

Prompt

```text
Utiliza como contexto el archivo AI_PROMPTS.md del proyecto. 

Si puedes acceder a él, aplica el contexto común definido en ese documento antes de realizar el análisis.

Si no puedes acceder al archivo, indícalo antes de continuar.

Compara estas dos implementaciones.

No indiques únicamente cuál prefieres.

Analiza:

- Legibilidad.
- Mantenibilidad.
- Buenas prácticas.
- Escalabilidad.
- Responsabilidad de cada clase.

Finalmente indica cuál utilizarías en este proyecto y justifica la decisión.
```

--

# Filosofía de uso

La IA no sustituye el criterio del desarrollador.

Toda recomendación debe ser entendida y evaluada antes de implementarse.

La prioridad del proyecto es aprender, mantener un código limpio y comprender el funcionamiento de Spring Boot antes de introducir herramientas que automaticen el desarrollo.