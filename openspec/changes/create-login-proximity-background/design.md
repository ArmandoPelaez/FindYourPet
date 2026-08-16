## Context

`AuthScreen` ya usa un gradiente vertical basado en `MaterialTheme.colorScheme`, pero no tiene un recurso que comunique la relación entre avisos y proximidad. SCRUM-32 solicita un elemento abstracto, local y adaptable; no es un mapa ni debe introducir dependencias externas, permisos o datos de ubicación.

La implementación debe respetar `docs/design-system.md`: Compose con Material 3 estable, colores derivados del tema, dimensiones centralizadas y contenido de autenticación siempre legible.

## Goals / Non-Goals

**Goals:**

- Mostrar detrás o junto al contenido de Login una composición abstracta de líneas, nodos, zonas circulares, conexiones y un marcador principal.
- Generar el gráfico localmente con APIs estables de Compose y adaptarlo al tamaño disponible.
- Usar colores del `MaterialTheme.colorScheme` y opacidades/tokens existentes o aprobados por el Design System.
- Mantener contraste suficiente en Light Theme y Dark Theme.
- Mantener intactos los campos, acciones, estados, callbacks y flujo de autenticación.

**Non-Goals:**

- Mostrar mapas, coordenadas, calles o ubicaciones reales.
- Usar Google Maps, Maps SDK, Places API, red o datos remotos.
- Modificar ViewModels, repositories, Firebase, permisos, navegación o persistencia.
- Crear una nueva identidad visual, imágenes rasterizadas o una dependencia gráfica externa.

## Decisions

### 1. Dibujar el recurso con Compose Canvas

El gráfico se implementará como un composable local basado en `Canvas`, porque permite dibujar líneas, círculos, nodos y conexiones en función del tamaño real sin agregar dependencias ni archivos de imagen. `Canvas` ya se utiliza en componentes del proyecto y es compatible con Material 3 estable.

Alternativa descartada: una imagen rasterizada o SVG externo. Agregaría un asset fijo, dificultaría la adaptación a resoluciones y no aporta valor para una composición geométrica abstracta.

### 2. Ubicarlo como capa visual no interactiva

El recurso se integrará como capa decorativa de la superficie de Login, detrás del contenido o en una región que no capture interacción. La composición de autenticación conservará su orden y podrá desplazarse sin que el gráfico oculte campos, acciones ni mensajes.

Alternativa descartada: insertar el gráfico entre campos y acciones. Alteraría la jerarquía y podría empeorar la experiencia con teclado o ventanas compactas.

### 3. Usar geometría proporcional y colores del tema

Las posiciones se calcularán con proporciones del `Canvas` y límites seguros, sin coordenadas geográficas ni valores dependientes de una resolución concreta. Las líneas, áreas y nodos usarán colores derivados de `MaterialTheme.colorScheme` con opacidades tokenizadas; no se agregarán colores arbitrarios dentro de la pantalla.

Alternativa descartada: valores absolutos por dispositivo o colores fijos. Contradice el Design System y puede degradar el contraste entre temas.

### 4. Separar presentación del flujo de autenticación

La extracción del composable gráfico, si resulta necesaria, quedará en la capa UI (`AuthScreen.kt` o un componente visual reutilizable). No recibirá ViewModels ni datos de usuarios; solo parámetros de tamaño, `Modifier` y colores derivados del tema.

Alternativa descartada: conectar el gráfico con ubicaciones, publicaciones o estados de backend. Eso ampliaría el alcance funcional y violaría la restricción de no mostrar datos geográficos reales.

## Risks / Trade-offs

- [El gráfico puede competir con el formulario] → Usar baja opacidad, ubicación decorativa y revisión manual de contraste en ambos temas.
- [La geometría puede recortarse en ventanas pequeñas] → Dibujar con proporciones del tamaño disponible y validar alturas compactas, teclado visible y distintas resoluciones.
- [El `Canvas` puede volver difícil una prueba visual pixel-perfect] → Cubrir la integración mediante pruebas estáticas/presentación y revisión manual de estados relevantes.
- [La introducción de un token nuevo puede ampliar el Design System] → Reutilizar tokens existentes; si falta una dimensión u opacidad, documentar un token coherente antes de usarlo.

## Migration Plan

No hay migración de datos ni despliegue especial. El rollback consiste en retirar el composable/capa visual y sus pruebas, conservando el flujo de autenticación existente.

## Open Questions

- Confirmar durante la implementación la ubicación exacta que ofrece mejor equilibrio entre presencia visual y legibilidad en teléfonos compactos y pantallas amplias.
