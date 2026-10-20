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
* Los recursos estáticos se permiten por prefijo.
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
  método y declarar una sola versión como `canonical`. A validar en la issue 2.
* Comportamiento ante un token caducado o inválido en una ruta pública.
  Verificar el comportamiento actual y documentarlo. No afecta a la web pública
  mientras el token vaya en cabecera, pero sí si se guardara en cookie.
* Dónde se guarda el token en el backoffice (memoria, localStorage o cookie).
* Mecanismo del castellano (plantillas por idioma, o bundles con idioma
  explícito solo para la web). Ver ADR-017.
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