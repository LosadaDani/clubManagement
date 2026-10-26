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
noticias, formulario de contacto, backoffice, versión en castellano y página
de socios y sus perros (ver «Pendiente de decidir»).

## Decidido

* Thymeleaf servido desde la API, mismo proceso, sin CORS (ADR-017).
* Fragmentos estándar para cabecera, pie y menú. Sin Layout Dialect.
* Permisos `permitAll` página a página; nunca patrones amplios.
* La portada se abrió con un matcher exacto (`"/"`), en su propia línea de
  `SecurityConfig`, separada de login, Swagger y H2. Los recursos estáticos
  (`/css/**`, `/js/**`, `/img/**`, `/fonts/**`, `/favicon.ico`) se permitirán
  por prefijo cuando existan (#92).
* Con un `Authorization` inválido, caducado o de firma incorrecta,
  `JwtAuthenticationFilter` registra la excepción y trata la petición como
  anónima: una ruta pública responde `200` y una protegida `401`. Verificado
  en #91 (`GET /` con `Bearer abc` devuelve `200`).
* Los Controllers web no aparecen en Swagger (verificado con `GET /`) y no usan
  interfaz `*ControllerDocs`.
* Si existe `templates/index.html`, Spring Boot registra además una *welcome
  page* para `/`. El `@GetMapping("/")` del Controller web tiene prioridad, así
  que el resultado es el mismo; conviene saberlo al depurar.
* Catalán primero. Castellano después, con URLs `/es/...` y `hreflang`.
* Sin fechas de eventos en la web (se quedan obsoletas).
* Contacto como texto y enlaces (`mailto:` y `tel:`), sin formulario.
* Página 404 personalizada para navegadores.
* Tipografías del sistema o alojadas en el propio proyecto, sin cargar fuentes
  desde servidores de terceros (privacidad y velocidad).
* Tipografía elegida: Barlow Condensed para los titulares (peso 700 de partida)
  e Inter 18pt para el texto (400 y 600 de partida). Ambas con licencia OFL.
  Se elige Inter para el texto porque es la única de las probadas en la que la
  «l·l» (col·laboració) se dibuja bien; en Barlow el punto queda pegado. En
  producción: archivos WOFF2 con subconjunto latino en `static/fonts/`, con su
  `OFL.txt`. Pruebas en `docs/design/tipografias.html`.
* Texto y SEO: una intención de búsqueda por página, un solo `<h1>`, `title` y
  `meta description` propios, `alt` en todas las imágenes.
* El texto es provisional y se irá refinando.
* Sin testimonios, cifras ni datos inventados: solo contenido real del club.
* Sin analítica ni cookies de seguimiento en la primera versión (por tanto,
  sin banner de cookies).

## Dirección de diseño

* Tono: mezcla de deportivo (energía del logo) y familiar (cercanía, grupo),
  con un punto de seriedad.
* Estructura visual de partida: cabecera oscura con el azul marino del logo y
  el resto de la página sobre fondo claro. Se revisará al ver la maqueta, porque
  los referentes de abajo son mayoritariamente oscuros.
* Sin React ni framework de frontend: HTML y CSS con Thymeleaf.
* Acento: naranja (opción 1 del muestrario `docs/design/estilos-botones.html`)
  en los botones; se mantiene de momento y es revisable. Variable CSS para
  cambiarlo en un solo sitio. Sobre fondo claro el botón naranja sólido se distingue poco del
  fondo (2,31:1; el texto sí cumple): si molesta, borde más oscuro, sombra suave
  o una banda de color detrás.
* Logo: hoy solo existe una imagen JPEG con fondo azul marino (`#0F1839`). La
  cabecera usa ese mismo color. Para la web se generará una versión recortada y
  optimizada en `static/img/`; el original no va en `static/`. Se buscará una
  versión con fondo transparente (PNG o SVG).
* Fotos: el club tiene bastantes y se pueden pedir o hacer más según haga falta.
  Autoría, permisos y marcas: ver «Fotos: autoría, permisos y marcas».
* Referentes estéticos: naturalgos.net, miperro10.es, y las capturas del
  portfolio de un diseñador de webs de clubes caninos (Agility Pawer, Agility
  Almozara y Mi Perro 10). Solo se toman rasgos de estilo; no se copian
  diseños, formas de marca, imágenes ni código.
* Rasgos que gustan: enlaces a las secciones integrados con las imágenes
  (Pawer); secciones integradas en las fotos, con un toque deportivo pero serio
  (Almozara); sencilla, moderna y deportiva a la vez (Mi Perro 10).
* Maquetas estáticas de la portada en `docs/design/` (v1, v2 y v3, con informes
  en `docs/reviews/maqueta-portada*.md`). Punto de partida para pasar la portada
  a plantillas Thymeleaf. La v2 quitó las formas decorativas de la v1, separó
  el texto de la cabecera de la foto, quitó el subtítulo «Curs d'iniciació a
  l'agility», ajustó el encuadre de la banda nocturna y presentó el año como
  «Des de 1992». La v3 añadió la marca de agua del perro del logo.
* Portada decidida:
  * Cabecera con la foto del atardecer (variante A).
  * Marca de agua del perro del logo al 16 % (variable `--marca-opacitat`), en
    la cabecera y en la banda nocturna.
  * Tesela «Formació» con la foto de cachorros jugando en pista
    (`formacio-cadells`), en lugar del caniche saltando.
  * Teselas «El club» y «Contacte» sin intercambiar. «Contacte» sigue con la
    foto de grupo, provisional hasta repetirla.
  * Alineación del texto de la cabecera con el logo: se deja como está en la
    maqueta.
  * Textos de la maqueta en catalán revisados por Dani, que los da por
    correctos. Siguen siendo provisionales hasta los textos definitivos.
* Marca de agua y contraste: con la marca al 16 %, el texto naranja sobre el
  contorno blanco del perro no cumple 4,5:1 (4,36:1 en la cabecera, 3,04:1 en
  la banda). En la maqueta ningún texto se solapa con la marca, y por eso todo
  cumple. Al pasarlo a plantillas hay que mantener el texto fuera de la marca
  (por ejemplo, con una máscara que deje limpia la zona del texto) y volver a
  medir si cambian la frase, el tamaño de letra o la opacidad. La marca solo
  incluye la mitad delantera del perro, recortada del JPEG del logo.

## Datos del club

* Nombre: Club d'Agility Maresme
* Ubicación: Sot de Can Calopa, s/n, 08310 Argentona (Barcelona)
* Correo: info@agilitymaresme.com
* Teléfono: 600 434 201
* Fundado el 4 de octubre de 1992 (en la web se presenta como «Des de 1992»)
* Formación: curso de iniciación al agility
* Colaboración con FCAG y RSCE

Los datos están confirmados por el club. El código postal es el del municipio
de Argentona (08310, según la web del Ajuntament); no se ha comprobado un
código distinto para el paraje de Sot de Can Calopa.

## Fotos: autoría, permisos y marcas

Regla: que una foto no lleve firma visible no significa que no tenga autor.
Para cada foto que se publique hay que confirmar autor, permiso de uso y, si
procede, crédito. Las firmas de fotógrafo no se recortan sin que el fotógrafo
lo acepte; si no, se acredita. Las comprobaciones de firma se hicieron sobre el
tercio inferior de cada imagen (más un zoom a la del salto), no sobre toda la
imagen.

| Foto (nombre en la maqueta) | Marca o autoría observada | Estado y acción |
|---|---|---|
| `cabecera-atardecer` (caniche en salto, atardecer) | Firma «Lucía Marín» abajo, en el centro, en el original; el EXIF la nombra autora y copyright. La versión de la maqueta ya está recortada por abajo y no la muestra; al optimizar se elimina el EXIF | Es la foto definitiva de la cabecera. Confirmar permiso y que el recorte de la firma esté aceptado; decidir crédito en la página (por ejemplo, en el pie) |
| `formacio-cadells` (cachorros jugando en pista; `formacio-cadells.HEIC`) | Hecha por un socio, que se la ha pasado a Dani. Foto añadida a los assets | Confirmar si aparecen personas o menores. Pasar el HEIC a WebP/JPEG y quitar EXIF y GPS |
| Interior con collie negro y guía (`96CB0B50`, no usada en la maqueta) | Firma «Antonio Luna · Fotografía creativa» abajo, en el centro, grande | No usar sin permiso y crédito, o sin su aceptación a recortarla |
| `agility-collie-azul` (collie y guía en pista azul) | Sin firma de fotógrafo vista | Autoría por confirmar |
| `fuchur-jump-2` (caniche saltando, fondo verde) | Sin firma vista (zoom en la esquina inferior) | Autoría por confirmar |
| `grupo-club` (foto de grupo) | Incluye a una menor | Autorización del padre pendiente; repetir la foto |
| `9EB7979A` (selección, camiseta del presidente) | Sin firma vista | Uso aprobado por el club; autoría por confirmar |
| `club-natural` y `club-artificial` (pista) | Fotos de iPhone, sin firma vista | Autoría por confirmar |
| `caniche-pasarela`, `caniche-salto-valla`, `caniche-rosette`, `_DSC7092`, `IMG-20240630-WA0017`, `1141AAAA` | Sin firma vista en el tercio inferior | Autoría por confirmar. `caniche-rosette` conserva GPS en el original; se elimina al exportar |

Todas las imágenes web se exportan sin EXIF ni GPS.

## Pendiente de decidir

* Barra final de las URLs. Propuesta: mapear `/escola` y `/escola/` al mismo
  método y declarar una sola versión como `canonical`. No se puede validar con
  `/`: se decide en la primera issue con una página distinta de la raíz
  (#94, El club). Si se mapean las dos variantes, el matcher `permitAll`
  también debe cubrir las dos.
* Rutas inexistentes sin token: reciben `401` en JSON, no `404`, porque la
  seguridad actúa antes que Spring MVC. La página 404 personalizada (#98)
  no se vería para un visitante anónimo. Decidir cómo combinarla con
  `anyRequest().authenticated()`.
* Experiencia del backoffice ante un token caducado (ir al login o al inicio).
  El backend ya lo trata como anónimo; con el token en cabecera no afecta a la
  web pública, pero cambiaría si se guardara en cookie.
* Dónde se guarda el token en el backoffice (memoria, localStorage o cookie).
  Si el token se leyera de un sitio donde pueda llegar vacío (por ejemplo una
  cookie), tratar el token vacío como inválido: `JwtAuthenticationFilter` solo
  captura `JwtException` y un token vacío lanza `IllegalArgumentException`.
  Con la cabecera `Authorization` no se llega a ese caso: el servidor recorta
  el espacio final (deducido, no verificado en el código de Tomcat).
* Mecanismo del castellano (plantillas por idioma, o bundles con idioma
  explícito solo para la web). Ver ADR-017. La portada responde hoy con
  `Content-Language: es-ES` (por el idioma fijo de la aplicación) aunque la
  plantilla declara `lang="ca"`; decidir cómo fija el idioma la web sin
  cambiar el de la API.
* Logo con fondo transparente y versión para fondo claro. El logo vectorial
  original (SVG, PDF o AI) se está gestionando con el club. Daría fondo
  transparente, calidad a cualquier tamaño y el perro entero para la marca de
  agua. Sin él, vectorizar el perro por capas de color.
* Selección de fotos para cada página, con la autoría y los permisos de
  «Fotos: autoría, permisos y marcas». Más adelante, fotos de la entrada, de
  iniciación y primeros planos de obstáculos.
* Crédito de fotógrafos en la página (por ejemplo, en el pie): decidir
  formato y confirmar el permiso de cada autor antes de publicar. La foto del
  atardecer ya es la definitiva de la portada y está recortada sin la firma.
  El permiso de Lucía Marín se está gestionando; de momento se sigue adelante
  con esa foto.
* Redacción exacta de la relación con FCAG y RSCE (colaboración, no afiliación,
  salvo que se confirme).
* Página de socios y sus perros (foto y palmarés). Propuesta: fuera de la
  primera versión; cuando se haga, página aparte enlazada desde «El club», no
  una sección dentro de ella. Condiciones: consentimiento de cada socio para
  lo que se publique (nombre, foto, menores), palmarés verificado y sin fechas
  que caduquen, y decidir si los datos van fijos en la plantilla o salen del
  dominio de perros con un campo «publicable» (en ese caso, issue propia en
  un sprint posterior). Para una primera prueba se puede poner en «El club»
  una franja pequeña con 3 o 4 perros.
* Tecnología del backoffice.
* Aviso legal.
* Textos definitivos.

## Para la publicación

* Cerrar o restringir a ADMIN Swagger y la consola H2 en producción.
* Externalizar clave de firma y expiración del JWT (ADR-016).
* Dominio `agilitymaresme.com`: registro DNS apuntando al servidor, servidor
  web o proxy inverso delante de la aplicación (puertos 80 y 443), certificado
  HTTPS, redirección de HTTP a HTTPS, y elegir una sola versión (con o sin
  `www`) y redirigir la otra. Configurar la aplicación para que detrás del
  proxy use el host y el esquema originales (`server.forward-headers-strategy`).
  Sin esto, los enlaces y las redirecciones de la web pueden salir con
  `http` y con el puerto interno.
* Redirecciones `301` de las URLs antiguas (`/horaris/`, `/competicio/`,
  `/video-agility-maresme/`, `/lliga-interna/`) a la página más parecida.
* Alta del sitemap en Google Search Console y ficha de Google Business Profile.
* Revisión del aviso legal.
