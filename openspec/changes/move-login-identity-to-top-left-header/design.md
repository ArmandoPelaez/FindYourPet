## Context

Después de SCRUM-39, `AuthScreen.kt` ya renderiza el Login sobre un fondo continuo, pero todavía comienza con un `Box` circular de `AppSpacing.avatarLarge` y `Icons.Outlined.AccountCircle`, seguido de la identidad centrada. SCRUM-40 requiere cambiar únicamente esa composición de marca.

El repositorio contiene `app/src/main/res/drawable/ic_launcher_foreground.xml`, un recurso vectorial existente y transparente asociado a la identidad del launcher. También existe `drawable-nodpi/ic_launcher_new.png`, pero su fondo es blanco opaco; no debe colocarse como una imagen rectangular sobre el fondo del Login. La implementación debe reutilizar el recurso transparente existente y tokens del Design System.

## Goals / Non-Goals

**Goals:**

- Presentar una fila horizontal `[logo] FindYourPet` alineada al inicio del contenido en la parte superior del Login.
- Reducir el protagonismo de la identidad mediante el tamaño `AppSpacing.headerLogo` y una tipografía existente de menor jerarquía que el headline.
- Mantener el headline y supporting text centrados y visualmente dominantes.
- Conservar fondo continuo, scroll/IME, Light/Dark, responsive, accesibilidad y todo el formulario.

**Non-Goals:**

- No editar, recrear ni recolorear el logo.
- No utilizar el PNG opaco como tarjeta, superficie o recorte visual.
- No cambiar headline, supporting text, textos funcionales, campos, botones, autenticación, ViewModel, navegación o asset de fondo.
- No agregar nuevos tokens, shapes, elevaciones, opacidades, dependencias o componentes interactivos.

## Decisions

### 1. Usar el recurso vectorial transparente existente

La firma usará `painterResource(R.drawable.ic_launcher_foreground)` en lugar de `Icons.Outlined.AccountCircle` y del PNG opaco. Esto permite conservar un recurso de marca existente sin introducir un nuevo asset ni un rectángulo blanco sobre el fondo.

Alternativas consideradas:

- Usar `ic_launcher_new.png`: descartado porque su fondo es opaco y rompería la integración con el fondo continuo.
- Crear o editar un PNG transparente: descartado porque SCRUM-40 exige conservar el logo oficial y no rediseñarlo.
- Mantener `AccountCircle`: descartado porque es el avatar circular genérico que Jira solicita eliminar.

### 2. Separar la firma de la jerarquía contextual

La identidad se renderizará en un `Row` no interactivo, `fillMaxWidth()` y alineado al inicio, antes del bloque centrado de headline/supporting text. El Row reutilizará `AppSpacing.headerLogo` para el icono y un gap tokenizado existente para la separación con `FindYourPet`.

Alternativa considerada:

- Aplicar alineación izquierda a todo el hero: descartado porque cambiaría la jerarquía ya aprobada y la legibilidad del headline.

### 3. Mantener semántica no interactiva y orden de foco

El logo será decorativo (`contentDescription = null`) y el nombre será texto visible no clickeable. El Row no tendrá `clickable`, `focusable` ni acciones, por lo que Email, Contraseña y los botones conservarán su orden de foco y comportamiento.

## Risks / Trade-offs

- [El vectorial de launcher puede tener una proporción distinta a la esperada en el encabezado] → limitarlo con `AppSpacing.headerLogo` y `ContentScale.Fit`, y revisar visualmente tamaños pequeño/mediano.
- [La identidad puede competir todavía con el headline] → usar estilo `labelLarge`/equivalente existente y mantener el headline en `headlineSmall`.
- [El encabezado puede consumir más espacio vertical en pantallas pequeñas] → conservar la columna desplazable y evitar offsets o dimensiones específicas por dispositivo.
- [El logo puede perder contraste en un tema] → validar Light/Dark con colores del asset existente y sin inventar tintes.

## Migration Plan

1. Reemplazar el bloque circular centrado en `AuthScreen.kt` por la firma horizontal y ajustar aserciones estáticas de presentación.
2. Ejecutar validación OpenSpec, tests unitarios y build debug.
3. Validar visualmente posición, jerarquía, temas y tamaños de pantalla; verificar que el formulario conserva interacción.
4. Rollback: restaurar el bloque visual previo; no existen migraciones ni datos persistentes.

## Open Questions

- Ninguna para iniciar. Si el recurso vectorial existente no resulta legible sobre el fondo en un tema, detenerse y reportar el conflicto antes de crear un asset alternativo.
