## Context

`AuthScreen` ya implementa email/password, Google Sign-In, registro, mensajes de error y soporte de tema. Su presentación actual aplica un gradiente de fondo, una `Card` exterior y una superficie de cabecera dentro de esa card. SCRUM-31 requiere eliminar la composición basada en la gran card y convertirla en una pantalla vertical integrada, sin cambiar los contratos ni la lógica de autenticación.

La implementación debe respetar `docs/design-system.md`: Jetpack Compose con Material 3 estable, tokens centralizados, Light/Dark Theme y ningún valor visual nuevo hardcodeado.

## Goals / Non-Goals

**Goals:**

- Reestructurar la jerarquía visual de `AuthScreen` para que el fondo y el viewport sean la superficie principal.
- Mantener una columna vertical clara para identidad/proximidad, mensaje, formulario, autenticación alternativa y cambio a registro.
- Conservar el uso de `AppSpacing`, `AppShapes`, `AppElevation`, `AppFormTypography`, `AppButton` y `MaterialTheme.colorScheme`.
- Mantener el contenido utilizable con teclado visible y en alturas reducidas mediante el manejo de insets y desplazamiento vertical estable.
- Preservar todos los estados existentes: login, signup, Google Sign-In, error, cancelación y configuración faltante.

**Non-Goals:**

- Cambiar Firebase Auth, `PetViewModel`, repositorios, validaciones o contratos de estado.
- Agregar dependencias, imágenes remotas, permisos o nuevas fuentes de datos.
- Rediseñar la identidad visual completa, la navegación autenticada o las pantallas posteriores al login.
- Crear nuevos colores, tipografías o dimensiones si existe un token equivalente.

## Decisions

### 1. Reutilizar `AuthScreen` y mantener el flujo de estado

La modificación se concentrará en la composición de `AuthScreen.kt`. Se conservarán los `remember`, colecciones de estado, callbacks del `PetViewModel`, `CredentialManager` y mensajes actuales. Esto reduce el riesgo de alterar autenticación y mantiene el cambio limitado a presentación.

Alternativa descartada: mover la lógica a nuevos ViewModels o componentes de dominio. No aporta valor para SCRUM-31 y ampliaría el alcance.

### 2. Eliminar la card exterior y usar el viewport como superficie

La `Card` que actualmente envuelve todo el formulario se eliminará. El `Box` seguirá aplicando `fillMaxSize()`, `safeDrawing`, `imePadding()` y el fondo basado en `MaterialTheme.colorScheme`. La composición vertical quedará directamente sobre esa superficie, con el ancho máximo tokenizado que ya exista o con el token actual `AppSpacing.authMaxWidth` cuando corresponda.

Alternativa descartada: conservar la card y modificar solo su elevación o transparencia. No cumple el criterio de eliminar la gran card exterior.

### 3. Mantener la jerarquía funcional en una columna vertical

La pantalla conservará, en orden, la identidad y el área visual de proximidad, el mensaje principal, los campos del formulario, la acción primaria, el separador, Google Sign-In, el cambio entre login/registro y los mensajes de error. La cabecera anidada se simplificará sin eliminar la información que el usuario necesita.

Los campos seguirán usando `FormFieldLabel`, `AppFormTypography.input`, `AppShapes.content` y tokens de espaciado. Las acciones seguirán usando `AppButton` y sus variantes existentes.

Alternativa descartada: crear un sistema de componentes de autenticación nuevo. La pantalla ya tiene componentes reutilizables suficientes para este cambio.

### 4. Preservar legibilidad en ventanas pequeñas

La columna podrá desplazarse verticalmente si el contenido no cabe, manteniendo los insets de sistema y del IME. No se cambiarán los tamaños tipográficos ni se introducirán paddings específicos por dispositivo. En ventanas amplias se conservará el límite de ancho tokenizado para evitar líneas excesivamente largas.

Alternativa descartada: usar tamaños o dimensiones diferentes por dispositivo sin tokens. Contradice el Design System.

### 5. Validar comportamiento visual sin cambiar negocio

La verificación combinará tests existentes, `testDebugUnitTest`, `assembleDebug` y revisión manual de la pantalla en Light/Dark Theme. La revisión manual comprobará login, signup, Google Sign-In, error y teclado; no requiere cambios en Firebase ni en datos persistidos.

## Risks / Trade-offs

- [La eliminación de la card puede afectar contraste o agrupación visual] → Reutilizar el fondo, colores de tema, jerarquía tipográfica y espaciado existentes; revisar Light/Dark Theme manualmente.
- [El contenido puede quedar oculto con teclado o alturas pequeñas] → Mantener `safeDrawing`/`imePadding` y validar desplazamiento vertical en una ventana compacta.
- [La simplificación de la cabecera puede eliminar contexto visual] → Conservar identidad, mensaje y área visual de proximidad existentes; no introducir una identidad nueva.
- [Tokens actuales pueden estar ligados a la card exterior] → Reutilizar los equivalentes existentes y retirar solo los tokens que queden sin consumidores, sin cambios visuales ajenos al issue.

