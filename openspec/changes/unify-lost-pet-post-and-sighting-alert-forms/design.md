## Context

`CreatePetPostScreen` ya representa la referencia visual del flujo de publicación: TopAppBar, superficie de foto, secciones de formulario, campos Material 3 y acción primaria usando tokens de `AppSpacing`, `AppShapes` y `MaterialTheme`. `SightingAlertScreen` tiene capacidades equivalentes, pero agrega layout adaptativo, ubicación actual, foto opcional y una barra de envío inferior; su jerarquía visual y algunos componentes no siguen exactamente el mismo patrón.

El cambio es exclusivamente de presentación. Debe preservar validación, selección/captura de medios, ubicación manual o GPS, elegibilidad del reportante, envío de alertas y los contratos existentes. El Design System exige Material 3 estable, tokens centralizados, Light/Dark Theme y ausencia de valores visuales arbitrarios.

## Goals / Non-Goals

**Goals:**

- Usar la pantalla de publicación como referencia de jerarquía, secciones, superficies, espaciado y acciones.
- Compartir composables de presentación cuando sus responsabilidades sean realmente comunes, especialmente para carga de foto, encabezados de sección y agrupación de campos.
- Mantener el layout adaptativo de la alerta y asegurar que el patrón visual sea estable en tamaños de teléfono soportados.
- Mantener estados de carga, error, éxito, formulario vacío y permisos visibles y coherentes.
- Cubrir la convergencia con pruebas Compose, estáticas o de screenshot existentes y nuevas cuando aporten una comprobación observable.

**Non-Goals:**

- No cambiar `PetViewModel`, repositorios, Firebase, Room, modelos, mappers o reglas de dominio.
- No cambiar permisos, fuentes de ubicación, contratos de media, navegación ni textos funcionales salvo ajustes necesarios de presentación.
- No introducir una nueva librería visual, API experimental, migración de datos ni backend.
- No convertir la alerta en una copia idéntica de la publicación: sus campos de ubicación, foto opcional y acción de envío conservan su semántica.

## Decisions

### 1. Adoptar tokens y componentes existentes como contrato visual

Ambas pantallas usarán `MaterialTheme`, `AppSpacing`, `AppShapes`, `AppOpacity`, `AppElevation` y los componentes `AppButton` existentes. Los valores que no tengan token equivalente se incorporarán al Design System solo si son necesarios y se derivan de la identidad actual.

Alternativa descartada: ajustar cada pantalla con `dp`, `sp`, colores o radios locales. Mantendría diferencias ocultas y violaría las reglas del Design System.

### 2. Extraer solo patrones de presentación verdaderamente compartidos

Se evaluarán composables reutilizables para la superficie de carga de foto, títulos de sección y disposición común de campos. Cada pantalla conservará callbacks y estado propios; la reutilización no moverá validación ni efectos secundarios a los componentes visuales.

Alternativa descartada: crear un formulario genérico con un modelo de estado común. Aumentaría el acoplamiento entre publicación y alerta y podría alterar sus diferencias funcionales.

### 3. Mantener la adaptación de la alerta y alinear sus puntos visuales

`SightingAlertAdaptiveContent` conservará sus breakpoints, límites de ancho y desplazamiento. La unificación se aplicará en los elementos que componen el contenido: superficie multimedia, encabezados, campos, feedback y acción primaria. La barra inferior seguirá siendo necesaria para que el envío permanezca accesible durante el scroll.

Alternativa descartada: reemplazar ambos layouts por una columna fija. Reduciría la adaptación existente y podría introducir clipping u ocultamiento del CTA en ventanas distintas.

### 4. Preservar semántica y estados de cada flujo

La publicación continuará exigiendo foto, nombre y ubicación manual según su contrato. La alerta conservará foto opcional, ubicación manual/GPS, notas, elegibilidad de reportante y envío seguro. Los cambios de apariencia no deben crear ni mutar datos antes de que las validaciones existentes pasen.

Alternativa descartada: unificar los campos funcionales para que ambas pantallas tengan exactamente el mismo formulario. El SCRUM solicita lenguaje visual común, no igualdad de dominio.

### 5. Verificar con pruebas orientadas a presentación

Se actualizarán las pruebas Compose, estáticas y de screenshot existentes donde ya cubran estas pantallas. Se agregarán assertions de tags, componentes visibles, tokens o layout solo cuando no exista cobertura equivalente. La validación final incluirá tests unitarios y assemble debug, además de revisión manual de Light/Dark Theme y tamaños compactos/expandidos.

## Risks / Trade-offs

- [Riesgo] Extraer un componente compartido puede mezclar callbacks o estados de cámara/galería → Mantener componentes stateless y pasar callbacks explícitos; cada pantalla conserva sus launchers y estado.
- [Riesgo] Ajustar alturas o espaciado puede afectar scroll y teclado → Usar tokens existentes, conservar `imePadding`/`safeDrawing` y verificar viewport compacto y alto.
- [Riesgo] La acción de alerta puede perder su semántica de peligro al alinearse → Mantener la variante `Danger` y el texto/acción de envío propios de la alerta.
- [Riesgo] Cambios de colores o superficies pueden degradar Dark Theme → Usar exclusivamente `MaterialTheme.colorScheme` y revisar ambos temas en pruebas o inspección manual.
- [Riesgo] La unificación visual puede inducir cambios de negocio accidentales → Revisar diff contra el alcance y bloquear modificaciones a ViewModel, repositorio, permisos y modelos.

## Migration Plan

1. Implementar la convergencia en la rama del change, manteniendo los contratos actuales.
2. Ejecutar pruebas de presentación, `testDebugUnitTest` y `assembleDebug`; completar validación manual de ambos flujos en Light/Dark Theme.
3. Si la validación falla, revertir los cambios de UI del change; no requiere migración de datos ni rollback de backend.

## Open Questions

- ¿La superficie de foto compartida debe admitir exactamente las mismas etiquetas en ambos flujos o solo la misma estructura visual? Se resolverá durante la comparación de accesibilidad y tests existentes, manteniendo textos funcionales distintos cuando corresponda.
