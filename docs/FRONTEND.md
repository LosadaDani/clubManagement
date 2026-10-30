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
* Las páginas y los recursos estáticos tienen cada uno su línea en
  `SecurityConfig`, separadas de login, Swagger y H2: matchers exactos para las
  páginas (hoy `"/"`, `"/el-club"`, `"/agility"` y la variante con barra de las dos últimas) y por prefijo para los recursos (`/css/**`, `/img/**`,
  `/fonts/**` y `/favicon.ico`). No hay `/js/**` porque la web no lleva
  JavaScript (#92).
* Estructura de plantillas (#92): `templates/index.html` y los fragmentos
  `templates/fragments/head.html`, `header.html` y `footer.html`. Los
  fragmentos reciben parámetros (`head(titol, descripcio, ruta)` y `header(activa)`,
  que marca la página actual con `aria-current="page"`). Los enlaces y recursos
  usan `@{...}`. Los `srcset` se construyen con `th:srcset` por concatenación,
  porque la sustitución literal `|...|` no admite `@{...}`.
* Verificado con la aplicación en marcha (#92): la portada responde `200` a
  `GET` y `HEAD` sin token; el HTML servido no contiene atributos `th:` ni
  `xmlns:th`; los 17 recursos se sirven con el `Content-Type` correcto
  (`image/webp`, `font/ttf`, `image/x-icon`, `text/css`); no hay listado de
  directorios; las páginas aún no creadas responden `401`; y `/api/**` sigue
  protegido.
* Barra final de las URLs (#94): para Spring MVC, `/el-club` y `/el-club/` son
  rutas distintas. Cada página acepta las dos: su `@GetMapping` lleva las dos
  rutas (`{"/el-club", "/el-club/"}`) y el matcher `permitAll` de
  `SecurityConfig` también. La versión oficial es la que no lleva barra, y se
  declara con `<link rel="canonical">`. Una redirección `301` de una a otra, si
  se quiere, se hace en el servidor web delantero (ver «Para la publicación»).
  Verificado en #94: `GET` y `HEAD` a las dos variantes dan `200` y sirven
  exactamente el mismo HTML.
* Etiqueta canónica (#94): el fragmento `head` recibe la ruta
  (`head(titol, descripcio, ruta)`) y escribe
  `<link rel="canonical" href="https://agilitymaresme.com" + ruta>`. La URL base
  está en un solo sitio, el propio fragmento. Cada plantilla pasa su ruta sin
  barra final (`'/'` en la portada y `'/el-club'` en El club), también cuando se
  visita la variante con barra.
* Un único Controller web (`PublicWebController`, paquete `controller.web`)
  agrupa las páginas públicas: son vistas sin lógica ni datos, y un Controller
  por página no aportaría nada.
* El club (#94), texto en catalán en `club.html`: fundación en 1992 («un dels
  primers clubs d'agility d'Espanya», dato real aportado por el club), club
  social esportiu, federado en la FCAG (participa en sus competiciones y
  organiza pruebas), colaboración con la RSCE, dos pistas de competición y dos
  recintos cerrados pequeños, palmarés resumido sin nombres ni fechas (con
  permiso de los socios) y dirección. No se incluye lo que solo tenía sentido
  para organizar pruebas (iluminación, cronómetro, aparcamiento y pernocta).
  Fotos: grupo, pista natural y pista artificial.
* Maquetación de El club (#94): el texto va primero. Rejilla común de dos
  columnas iguales desde 900 px: entradilla (P1 y P2) con la foto de grupo a su
  lado (panorámica recortada en píxeles desde 1100 px; por debajo, la
  original), P3 y P4 en dos columnas CSS, las dos pistas una por columna, y
  dirección con botón. Por debajo de 900 px, una sola columna con el mismo
  orden del HTML. Se compararon antes otras disposiciones en maquetas
  (`docs/design/maqueta-club-*.html`, informes en `docs/reviews/`); se
  descartaron las que dejaban huecos grandes o empujaban el texto bajo el
  pliegue (con la foto de grupo a ancho completo, a 1280x800 el primer párrafo
  quedaba fuera de la primera pantalla).
* Texto sin justificar y sin separación silábica (#94): se probó
  `text-align: justify` con `hyphens: auto` desde 1100 px y se descartó. Chrome
  parte palabras de forma distinta según el ancho, parte una palabra entre dos
  columnas («Bri-tish») y, en una prueba aparte, puede partir «col·laborem» por
  el punto volado (`col·-` / `laborem`), que no es la forma normativa. No usar
  `hyphens: auto` en el texto en catalán sin resolver antes ese caso.
* Dirección y mapa (#94): botón «Com arribar» a Google Maps
  (`target="_blank"`, `rel="noopener noreferrer"`, con aviso oculto de pestaña
  nueva), sobre un bloque azul marino que da contraste suficiente al botón
  naranja (7,07:1). No se incrusta ningún mapa: un `iframe` de Google contacta
  con sus servidores al cargar la página y chocaría con la decisión de no usar
  cookies ni terceros.
* Imágenes de El club (#94): pistas con `srcset` de 800 y 1200 px (pantallas
  2x); foto de grupo de 800 y 1477 px (el original no da más, y a 2x se verá
  algo blanda). WebP sin EXIF ni GPS. `loading="lazy"` solo en las imágenes
  que no se ven al cargar.
* CSS: los nombres de clase de la página de El club (`.text`, `.foto`, `.cos`,
  `.pistes`, `.visita`, `.pagina`) son globales en `main.css`. No chocan con
  nada hoy; no reutilizarlos con otro significado en otras páginas.
* Verificado con la aplicación en marcha (#94): las cuatro combinaciones (con y
  sin barra, `GET` y `HEAD`) dan `200`; una sola canónica en cada página; el
  HTML de la portada solo cambia en la canónica; todos los recursos dan `200`;
  sin desbordamiento horizontal de 360 a 1440 px; el primer párrafo se ve sin
  hacer scroll a 1280x800; se cargan las tres fuentes. Solo comprobado en Chrome.
* Agility (#95), texto en catalán en `agility.html`, escrito para quien no
  conoce el agility o lo practica poco. Secciones: qué es (con el vínculo entre
  el gos y su guía como idea central), los obstáculos (16 a 22 según el nivel,
  numerados; salto de barra, doble y de longitud, mur y roda; túneles
  flexibles; eslalon de 12 palos; passarel·la, balancí y empalissada, con
  «A-frame» entre paréntesis), la competición (dos pistas, agility con todos
  los obstáculos y jumping sin los de contacto; gana quien suma menos faltas
  y refusadas y más velocidad entre las dos pistas), para qué perros (cualquier
  raza o cruzado; cuatro alturas, petita, mitjana, intermèdia y gran, con los
  nombres de la RSCE entre paréntesis; competición desde los 18 meses),
  campeonatos (FCAG en liga con Copa Catalunya; RFEC con ligas autonómicas o
  zonales y Campeonato de España; RSCE con niveles y puntos en pruebas
  puntuables) y cómo empezar (curso de iniciación, enlace a Escola). No se
  detallan los niveles de cada entidad. Los datos del texto los ha confirmado
  el club.
* Maquetación de Agility (#95), a partir de tres maquetas comparadas (A: tira
  de las seis fotos en una banda azul; B: fotos repartidas junto al texto; C:
  tira dentro de la sección). Se elige la B. Apertura en dos columnas desde 1100
  px (título y entradilla sobre azul marino a la izquierda, foto hasta el borde
  a la derecha), porque con la foto a ancho completo el primer párrafo quedaba
  fuera de la primera pantalla. Cada sección es una fila de texto (hasta 35rem)
  con una foto de 22rem a la derecha desde 900 px; una columna por debajo. La
  foto va siempre a la derecha para que el orden del HTML sea el visual en todos
  los anchos. «Els obstacles» ocupa todo el ancho en dos columnas CSS, un
  párrafo por columna (`break-inside: avoid`), con las dos fotos debajo, cada
  una bajo su columna. Diferencia máxima entre columnas: una línea.
* Sin cursivas ni pies de foto (#95): el proyecto solo tiene Inter 400 y 600
  rectas y `font-synthesis: none`, por lo que los términos extranjeros van
  rectos. No se añade Inter Italic. Los pies de foto se quitaron; la
  información queda en el `alt`.
* «A-frame» no se parte en el guion a ningún ancho (`span` con
  `white-space: nowrap`, no `&#8209;`, porque no se ha comprobado que Inter
  tenga ese glifo).
* Margen del texto de la apertura sin `100vw` (#95): `100vw` incluye la barra de
  desplazamiento y descuadra el texto unos 7 px respecto al logo con barra
  clásica. Se usa `container-type: inline-size` y la unidad `cqw`. Navegadores
  anteriores a 2022 (Chrome < 105, Safari < 16, Firefox < 110) ignorarían esa
  declaración y el texto quedaría a 16 px del borde, legible pero sin alinear.
* Título de las páginas: «Titular · Club d'Agility Maresme» (como El club).
* Verificado con la aplicación en marcha (#95): `/agility` y `/agility/` dan
  `200` con `GET` y `HEAD` y el mismo HTML; una sola canónica
  (`https://agilitymaresme.com/agility`) en las dos; sin atributos `th:`; texto
  servido idéntico al de la plantilla; las 14 imágenes dan `200` e
  `image/webp`; sin desbordamiento horizontal de 360 a 1440 px; el primer
  párrafo se ve sin scroll a 1280x800; a 2x la apertura elige la versión de
  1600 px; navegación con Tab completa con contorno visible (17,35:1);
  `/escola` responde `401`, que es lo esperado hasta su issue. Solo en Chrome.
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
  * Tesela «Agility» con la foto de un border negro saltando una valla
    (`tesela-agility-border-800.webp`, recortada en píxeles a 4:3), que sustituye
    a la anterior. `tesela-agility-800.webp` queda sin uso en `static/img/`;
    decidir si se borra.
  * Alineación del texto de la cabecera con el logo: se deja como está en la
    maqueta.
  * Textos de la maqueta en catalán revisados por Dani, que los da por
    correctos. Siguen siendo provisionales hasta los textos definitivos.
* Fotos de perros: no repetir un perro dentro de una misma página ni entre
  páginas de contenido. La portada es una excepción aceptada. No se publican
  fotos con terceros reconocibles (por ejemplo, público de fondo) salvo permiso
  o recorte.
* Repeticiones entre la portada y El club (aceptadas hasta tener todas las
  secciones): `club-natural` es también la tesela «El club», la foto de grupo es
  la tesela «Contacte» y la pista artificial es la banda nocturna.
* Página de Agility (#95): apertura con la foto del Shetland corriendo (la
  alternativa descartada para la cabecera de la portada), y seis fotos de
  perros distintos repartidas entre las secciones: eslalon y empalissada en
  «Els obstacles», salto de longitud en «La competició», balancí en «Per a quins
  gossos», passarel·la en «Campionats» y túnel en «Com començar». La foto de la
  passarel·la no ilustra el texto de «Campionats» y se mantiene por equilibrio
  visual. Se descartó la foto de túnel de un perro que ya salía en la
  empalissada.
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
* Federado en la FCAG (participa en sus competiciones y organiza pruebas) y
  colaboración con la RSCE (participa y organiza pruebas de ambas entidades)
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
| `grupo-club` (foto de grupo) | Incluye a una menor | Autorización del padre confirmada. Repetir la foto con más socios y sustituirla |
| `9EB7979A` (selección, camiseta del presidente) | Sin firma vista | Uso aprobado por el club; autoría por confirmar |
| `club-natural` y `club-artificial` (pista) | Hechas por Dani con iPhone; sin firma | Publicables. Se exportan sin EXIF ni GPS |
| Fotos de perros en acción (tesela de Agility de la portada, las de la página de Agility y la del Shetland) | Sin metadatos de autoría (llegaron por mensajería) | Permiso de los autores y de los dueños de los perros confirmado por el club. Sin crédito de momento (ver «Pendiente de decidir»). En las de Agility: perros distintos comprobados por el club; la guía de la apertura es la propietaria del perro; en la del túnel se ve de fondo a personas no reconocibles |
| `caniche-pasarela`, `caniche-salto-valla`, `caniche-rosette`, `_DSC7092`, `IMG-20240630-WA0017`, `1141AAAA` | Sin firma vista en el tercio inferior | Autoría por confirmar. `caniche-rosette` conserva GPS en el original; se elimina al exportar |

Todas las imágenes web se exportan sin EXIF ni GPS.

## Pendiente de decidir

* Rutas inexistentes sin token: reciben `401` en JSON, no `404`, porque la
  seguridad actúa antes que Spring MVC. La página 404 personalizada (#98)
  no se vería para un visitante anónimo. Decidir cómo combinarla con
  `anyRequest().authenticated()`. Los recursos estáticos inexistentes
  (por ejemplo `/css/no-existe.css`) sí responden `404`, pero con el JSON por
  defecto de Spring Boot, no con `ErrorResponseDTO`, y el mensaje expone la
  ruta interna del recurso.
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
  «Fotos: autoría, permisos y marcas». Hechas la portada, El club y Agility;
  faltan Escola y Contacte. Más adelante,
  fotos de la entrada, de iniciación y primeros planos de obstáculos.
* Crédito de fotógrafos en la página (por ejemplo, en el pie): decidir
  formato y confirmar el permiso de cada autor antes de publicar. La foto del
  atardecer ya es la definitiva de la portada y está recortada sin la firma.
  El permiso de Lucía Marín se está gestionando; de momento se sigue adelante
  con esa foto. De momento las páginas se publican sin crédito.
* Portada, a revisar en #100: a 360 y 768 px una imagen no termina de cargar
  (`currentSrc` vacío). Es de antes de #94; probablemente sea la marca de agua
  de la banda, oculta a propósito con un `<source>` vacío, pero no está
  comprobado.
* Agility (#95): la foto de la empalissada solo tiene versión de 800 px (el
  original mide 1080x720) y en pantallas 2x se verá algo blanda. Sustituirla por
  una de mayor resolución cuando haya una.
* `/escola` responde `401` en JSON hasta que exista su página (#96), y desde
  Agility y el menú un visitante que pulse «Coneix l'escola» o «Formació» lo
  vería. Priorizar #96 si tarda.
* El hover del botón «Com arribar» (contraste 9,56:1 según la maqueta) no se
  ha vuelto a medir en la página real. La página de El club solo se ha
  probado en Chrome (no en Firefox ni Safari).
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
* Perfiles de configuración: hoy todo está en un único `application.properties`.
  Separar desarrollo y producción (por ejemplo `application-dev.properties` y
  `application-prod.properties`). Lo que cambia entre ellos: `spring.thymeleaf.cache`
  (`false` solo en desarrollo; `true` en producción), consola H2, `show-sql` y
  `format_sql`, `ddl-auto` (`update` no es adecuado en producción) y la base de
  datos (H2 en memoria a MySQL).
* Maven wrapper: `./mvnw` no funciona (falta
  `.mvn/wrapper/maven-wrapper.properties`). Regenerarlo o quitarlo antes de
  cualquier despliegue o integración continua; sin él, solo se puede compilar
  con un Maven instalado o desde el IDE.
* Etiqueta canónica: si cambia el dominio, actualizar `https://agilitymaresme.com`
  en `fragments/head.html` (es el único sitio).
* Barra final: la aplicación sirve las dos variantes. Si se quiere, redirigir
  `/x/` a `/x` con un `301` en el servidor web delantero.
* Fuentes: convertir los TTF de `static/fonts/` a WOFF2 con subconjunto latino
  (requiere `fonttools` y `brotli`). Hoy suponen el 36 % del peso de `static/`.
* Caché de los estáticos: en desarrollo salen con `Cache-Control: no-store`
  (por devtools, deducido). Al separar perfiles, fijar `spring.web.resources.cache.*`
  (caché larga, con versión en el nombre de los archivos) y comprobar las
  cabeceras sin devtools, que no se han verificado.
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
 