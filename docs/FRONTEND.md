# Frontend — Decisiones y pendientes

> Decisión técnica en `ARCHITECTURE_DECISIONS.md` (ADR-017). Este documento
> recoge las decisiones de producto y diseño, y lo que queda por decidir. Se
> actualiza al cerrar cada issue del Sprint 6.

## Alcance de la primera versión (web pública)

Cinco páginas, solo en catalán, sin endpoints públicos nuevos de API:

| Página              | URL            | Nota                                    |
|---------------------|----------------|-----------------------------------------|
| Inicio              | `/`            | Mismo URL que la web actual             |
| El club             | `/el-club`     | Nueva                                   |
| Qué es el Agility   | `/agility`     | Nueva                                   |
| Formación           | `/escola`      | Reutiliza la URL de la web actual       |
| Contacto            | `/contactans`  | Reutiliza la URL de la web actual       |

Fuera de la primera versión: Horarios, Vídeos, Lliga interna, Competición,
noticias, formulario de contacto, backoffice y versión en castellano.

## Decidido

* Thymeleaf servido desde la API, mismo proceso, sin CORS (ADR-017).
* Fragmentos estándar para cabecera, pie y menú. Sin Layout Dialect.
* Permisos `permitAll` página a página; nunca patrones amplios.
* La portada se abrió con un matcher exacto (`"/"`), en su propia línea de`SecurityConfig`, separada de login, Swagger y H2. Los recursos estáticos (`/css/**`, `/js/**`, `/img/**`, `/favicon.ico`) se permitirán por prefijo cuando existan (#92).
* Con un `Authorization` inválido, caducado o de firma incorrecta,`JwtAuthenticationFilter` registra la excepción y trata la petición como anónima: una ruta pública responde `200` y una protegida `401`. Verificado en la #91 (`GET /` con `Bearer abc` devuelve `200`).
* Los Controllers web no aparecen en Swagger (verificado con `GET /`) y no usan interfaz `*ControllerDocs`.
* Si existe `templates/index.html`, Spring Boot registra además una *welcome page* para `/`. El `@GetMapping("/")` del Controller web tiene prioridad, así que el resultado es el mismo; conviene saberlo al depurar.
* Catalán primero. Castellano después, con URLs `/es/...` y `hreflang`.
* Sin fechas de eventos en la web (se quedan obsoletas).
* Contacto como texto y enlaces (`mailto:` y `tel:`), sin formulario.
* Página 404 personalizada para navegadores.
* Tipografías del sistema o alojadas en el propio proyecto, sin cargar fuentes
  desde servidores de terceros (privacidad y velocidad).
* Texto y SEO: una intención de búsqueda por página, un solo `<h1>`, `title` y
  `meta description` propios, `alt` en todas las imágenes.
* El texto es provisional y se irá refinando.

## Datos del club (provisionales)

* Nombre: Club d'Agility Maresme
* Ubicación: Sot de Can Calopa, s/n, Argentona (Barcelona)
* Correo: info@agilitymaresme.com
* Teléfono: 600 434 201
* Fundado el 4 de octubre de 1992
* Formación: curso de iniciación al agility
* Colaboración con FCAG y RSCE

Pendiente de confirmar: código postal, si se publica la dirección completa y el
móvil, y la redacción exacta de la relación con FCAG y RSCE (colaboración, no
afiliación, salvo que se confirme).

## Pendiente de decidir

* Barra final de las URLs. Propuesta: mapear `/escola` y `/escola/` al mismo
  método y declarar una sola versión como `canonical`. No se puede validar con `/`: se decide en la primera issue con una página distinta de la raíz (#94, El club). Si se mapean las dos variantes, el matcher `permitAll`también debe cubrir las dos.
* Rutas inexistentes sin token: reciben `401` en JSON, no `404`, porque la seguridad actúa antes que Spring MVC. La página 404 personalizada (#98) no se vería para un visitante anónimo. Decidir cómo combinarla con`anyRequest().authenticated()`.
* Experiencia del backoffice ante un token caducado (ir al login o al inicio). El backend ya lo trata como anónimo; con el token en cabecera no afecta a la web pública, pero cambiaría si se guardara en cookie.
* Dónde se guarda el token en el backoffice (memoria, localStorage o cookie). Si el token se leyera de un sitio donde pueda llegar vacío (por ejemplo una cookie), tratar el token vacío como inválido: `JwtAuthenticationFilter` solo captura `JwtException` y un token vacío lanza `IllegalArgumentException`. Con la cabecera `Authorization` no se llega a ese caso: el servidor recorta el espacio final (deducido, no verificado en el código de Tomcat).
* Mecanismo del castellano (plantillas por idioma, o bundles con idioma
  explícito solo para la web). Ver ADR-017. La portada responde hoy con `Content-Language: es-ES` (por el idioma fijo de la aplicación) aunque la plantilla declara `lang="ca"`; decidir cómo fija el idioma la web sin cambiar el de la API.
* Tecnología del backoffice.
* Aviso legal.
* Logo, fotos y textos definitivos.

## Para la publicación (pasar al chat de despliegue)

* Cerrar o restringir a ADMIN Swagger y la consola H2 en producción.
* Externalizar clave de firma y expiración del JWT (ADR-016).
* Redirecciones `301` de las URLs antiguas (`/horaris/`, `/competicio/`,
  `/video-agility-maresme/`, `/lliga-interna/`) a la página más parecida.
* Alta del sitemap en Google Search Console y ficha de Google Business Profile.
* Revisión del aviso legal.