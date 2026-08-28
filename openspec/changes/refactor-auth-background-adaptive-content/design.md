## Context

`AuthScreen` compone un `Box` con `safeDrawing`, `imePadding`, un gradiente, una imagen a pantalla completa y una capa de overlay. Actualmente usa `imagen_nuevo_fondo_pantalla.png`, cuyo contenido visual incluye logo/identidad, slogan, hero, texto descriptivo y gráficos decorativos. El formulario de email, contraseña, Google y cambio de modo se renderiza en una columna con scroll y el layout `LoginVerticalRegions`, por lo que puede desplazarse sobre el contenido semántico rasterizado cuando baja la altura disponible.

El cambio es de estructura de UI en Jetpack Compose. Debe conservar la identidad visual, el contrato de autenticación, el soporte de temas y los tokens definidos en `docs/design-system.md`. La inspección de recursos confirma que `imagen_fondo_pantalla_login.png` contiene el tratamiento gráfico/mapa decorativo sin el bloque textual del hero; se usará como referencia de fondo decorativo existente, sin generar una nueva imagen ni introducir dependencias.

## Goals / Non-Goals

**Goals:**

- Mantener una capa de fondo estable que contenga únicamente elementos decorativos.
- Renderizar dentro del contenido adaptable el branding, slogan, hero y textos descriptivos que correspondan a la composición actual.
- Calibrar el ritmo vertical del hero con la distribución visual de `imagen_nuevo_fondo_pantalla.png`, usando tokens existentes y sin reintroducir posiciones fijas.
- Componer hero y formulario dentro de una misma superficie desplazable y semánticamente ordenada.
- Mantener Login y Crear cuenta utilizables con teclado abierto, teclado cerrado y distintas alturas.
- Dar prioridad al campo enfocado y a las acciones de autenticación cuando el viewport se reduce.
- Conservar jerarquía, tipografía, colores, formas, espaciado, Light/Dark Theme y accesibilidad existentes mediante tokens.
- Mantener los callbacks, estados y flujos de email/contraseña y Google sin cambios funcionales.

**Non-Goals:**

- No rediseñar la identidad `A CASA`, el logo, el slogan ni los textos aprobados.
- No modificar Firebase Auth, Google Sign-In, `PetViewModel`, navegación, validaciones, persistencia o backend.
- No resolver la superposición mediante offsets, márgenes o paddings compensatorios arbitrarios.
- No agregar tarjetas, divisores, superficies, APIs experimentales, librerías visuales ni recursos remotos.
- No cambiar el comportamiento de autenticación ni el contrato de estados de carga, éxito o error.

## Decisions

1. **Separar el fondo decorativo del contenido semántico.**
   - La imagen de fondo se mantendrá como una capa no interactiva y exclusivamente decorativa, usando el recurso local sin contenido textual incrustado.
   - Logo/branding, slogan, hero y supporting text se renderizarán como composables dentro del árbol de contenido.
   - Alternativa descartada: conservar `imagen_nuevo_fondo_pantalla.png` y ajustar posiciones, porque mantiene la causa raíz y vuelve a producir colisiones al cambiar la altura.

2. **Usar un único flujo desplazable para hero y autenticación.**
   - La columna adaptable contendrá el hero y, después, el bloque de formulario; el scroll existente y `imePadding()` se aplicarán al contenido que necesita moverse.
   - En espacio suficiente se conservará una composición equivalente mediante los tokens de spacing y la jerarquía tipográfica actuales.
   - En espacio reducido se priorizará la continuidad del formulario y la visibilidad del foco, sin depender de una coordenada fija o un modelo de dispositivo.
   - Alternativa descartada: mantener un layout absoluto o dos capas desplazables independientes, porque permite que el formulario invada el hero.

3. **Conservar la distribución visual de la imagen como referencia, no como contenido rasterizado.**
   - El bloque de branding y slogan se mantendrá agrupado; el headline del hero y su texto descriptivo conservarán sus separaciones relativas antes del formulario.
   - La calibración se expresará con `AppSpacing` existente (`compactGap`, `lg`, `xl`) y estilos de `MaterialTheme`, de modo que el scroll siga siendo el mecanismo de adaptación.
   - Alternativa descartada: usar el PNG original como fondo junto con el hero Compose, porque duplicaría logo y textos y volvería a crear contenido no adaptable.

4. **Mantener componentes y contratos existentes.**
   - Se conservarán `OutlinedTextField`, `AppButton`, `TextButton`, `FormFieldLabel`, `FormFieldPlaceholder`, `AppFormTypography`, `AppShapes`, `AppSpacing` y los estados/callbacks actuales.
   - La extracción de hero y formulario será de presentación; no moverá reglas de autenticación al composable ni cambiará `PetViewModel`.
   - Los mensajes de validación, error, carga y éxito seguirán siendo parte del contenido desplazable y accesible.
   - Alternativa descartada: crear un nuevo estado de dominio o cambiar el ViewModel para resolver un problema exclusivamente de layout.

5. **Tratar el teclado como una reducción del viewport.**
   - `safeDrawing`, `imePadding()` y `verticalScroll` seguirán formando parte del flujo de layout.
   - La solución debe permitir que el elemento enfocado y las acciones necesarias sean alcanzables sin ocultar el contenido por una capa fija.
   - No se introducirán APIs experimentales ni una implementación específica para un tamaño de pantalla.

6. **Conservar el tratamiento visual en ambos temas.**
   - Los colores, contraste, tipografía y transparencias se resolverán con `MaterialTheme` y los tokens existentes.
   - El fondo decorativo no tendrá contenido semántico ni `contentDescription`; los elementos equivalentes del hero sí formarán parte del árbol accesible con etiquetas apropiadas.
   - Alternativa descartada: añadir colores o tamaños propios para reconstruir el arte de la imagen, porque rompería la fuente de verdad visual y el Design System.

## Risks / Trade-offs

- [Risk] Reubicar el hero puede alterar la composición visual aprobada en pantallas altas. → Mitigation: conservar orden, jerarquía y tokens actuales, y validar con teclado cerrado antes de validar el modo compacto.
- [Risk] El contenido semántico puede quedar demasiado alto o bajo en un viewport pequeño. → Mitigation: mantener un único scroll, `imePadding()`, foco accesible y pruebas con alturas representativas, sin coordenadas fijas.
- [Risk] La imagen decorativa existente puede no cubrir todas las variantes de composición. → Mitigation: mantener `ContentScale` y overlay actuales, comprobar la lectura en Light/Dark y no incorporar otro recurso sin evidencia.
- [Risk] El refactor puede alterar callbacks o estados de autenticación al mover bloques. → Mitigation: conservar estado local, callbacks del ViewModel, semantics, orden de foco y aserciones de presentación existentes.
- [Risk] Cambios paralelos de login pueden tocar las mismas líneas. → Mitigation: documentar la dependencia con SCRUM-42 y `add-login-background-image`, implementar solo el alcance de SCRUM-51 y revisar el diff contra este change.

## Migration Plan

1. Revisar `AuthScreen.kt`, recursos de fondo, tokens y pruebas estáticas existentes.
2. Extraer/ubicar el hero como contenido Compose y dejar el fondo local solo con gráficos decorativos.
3. Reorganizar la columna desplazable para que hero, formulario, errores y acciones compartan el flujo adaptable.
4. Actualizar o agregar pruebas de estructura, orden, ausencia de contenido semántico en el background y comportamiento IME/scroll.
5. Ejecutar `openspec validate --strict`, `testDebugUnitTest` y `assembleDebug`; realizar revisión manual en Login/Crear cuenta, alturas bajas, teclado abierto, Light y Dark.
6. Rollback: revertir la composición, el recurso seleccionado y las pruebas del change; no requiere migración de datos ni cambios remotos.

## Open Questions

- Confirmar durante la implementación cuál recurso de branding existente debe renderizarse como parte del hero, sin reutilizar una imagen que vuelva a incrustar textos no adaptables.
- Confirmar mediante revisión manual que el tratamiento de `imagen_fondo_pantalla_login.png` conserva la apariencia aprobada después de retirar el fondo con contenido.
- La prueba con dispositivo/emulador real queda condicionada a la disponibilidad del entorno; las pruebas estáticas y de build deben ejecutarse siempre.
