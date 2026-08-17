## Context

`AuthScreen.kt` ya dispone de una columna con `AppSpacing.authMaxWidth`, paddings tokenizados, `AppButton` y variantes diferenciadas para las acciones. SCRUM-42 requiere confirmar y ajustar esa composición para que el contenido no se expanda innecesariamente en pantallas anchas, mantenga una referencia horizontal común y haga evidente la prioridad de `Entrar`.

La pantalla de referencia aportada por el usuario se utilizará únicamente como guía de distribución: hero más arriba y compacto, ritmo vertical más corto entre hero, título, campos y acciones, controles agrupados en el centro y menor espacio inferior. No se copiarán controles ni textos que no formen parte del Login actual.

El cambio es exclusivamente de presentación en Jetpack Compose. Debe respetar `docs/design-system.md`, no introducir valores visuales arbitrarios, mantener el fondo y hero existentes y conservar todos los contratos funcionales de autenticación.

## Goals / Non-Goals

**Goals:**

- Contener headline, supporting text, formulario y acciones en una única columna visual coherente.
- Mantener márgenes laterales tokenizados y evitar expansión indefinida en pantallas anchas.
- Garantizar tres niveles perceptibles: `Entrar` primario, Google secundario y Crear una cuenta terciario.
- Ajustar el ritmo vertical para que el hero no consuma espacio innecesario y el formulario/acciones queden agrupados visualmente.
- Conservar el asset oficial y el branding de Google.
- Mantener responsive, accesibilidad, foco, teclado, `verticalScroll`, `imePadding()`, Light/Dark Theme y callbacks.

**Non-Goals:**

- No modificar lógica de autenticación, ViewModel, Firebase, navegación o backend.
- No cambiar textos del hero, identidad, fondo o posición de la marca.
- No copiar de la referencia `Recordarme`, `¿Olvidaste tu contraseña?` ni textos informativos adicionales.
- No crear colores, anchos, `maxWidth`, paddings, spacing, shapes, elevaciones o estilos de botones nuevos.
- No agregar métodos de autenticación ni alterar el comportamiento funcional de Google Sign-In.

## Decisions

1. **Una sola columna de contenido para todo el Login.**
   - El límite horizontal se aplicará en el contenedor común que engloba hero, campos y acciones, y sus hijos conservarán `fillMaxWidth()` dentro de ese límite.
   - Se reutilizarán tokens existentes de `AppSpacing` para el límite y los márgenes; se elegirá el token existente que produzca la composición contenida requerida sin inventar un valor.
   - Alternativa descartada: asignar anchos distintos a campos y botones, porque contradice la alineación común solicitada.

2. **Conservar los componentes de acción existentes y sus roles.**
   - `Entrar` permanecerá en `AppButtonVariant.Primary` o en el estilo primario equivalente que ya provee `AppButton`.
   - Google permanecerá en una variante secundaria compatible con el Design System y conservará `google_sign_in_g_standard_color`.
   - Crear una cuenta permanecerá como `TextButton` o equivalente terciario, sin superficie primaria.
   - Alternativa descartada: modificar el asset oficial de Google o convertir las tres acciones en botones primarios.

3. **Concentrar el énfasis visual en Entrar sin tocar colores directamente.**
   - La jerarquía se logrará mediante las variantes existentes, orden, ancho común y espaciado tokenizado.
   - Los colores se obtendrán de `MaterialTheme.colorScheme` y componentes del Design System.
   - Alternativa descartada: usar `Color(...)`, opacidades o colores locales para debilitar acciones secundarias.

4. **Preservar la interacción y la adaptabilidad.**
   - Se conservarán `verticalScroll`, `imePadding`, touch targets, labels, semantics, orden de foco, callbacks y estados de carga.
   - La columna podrá ocupar el ancho disponible en teléfonos pequeños dentro de márgenes tokenizados, pero mantendrá un máximo en pantallas anchas.
   - Alternativa descartada: fijar un ancho absoluto que pueda causar overflow o volver inaccesibles los campos con el teclado abierto.

5. **Usar la referencia para ritmo, no para ampliar el producto.**
   - El ajuste vertical se realizará con el orden actual de componentes y tokens de spacing existentes: hero compacto, separación controlada, campos y acciones agrupados, y espacio inferior no sobredimensionado.
   - No se agregarán componentes funcionales, textos, toggles ni enlaces presentes únicamente en la imagen.
   - Alternativa descartada: reproducir la pantalla de referencia elemento por elemento, porque SCRUM-42 solo solicita composición visual y jerarquía de las acciones existentes.

## Risks / Trade-offs

- [Risk] Un límite demasiado estrecho puede causar wrapping o reducir la legibilidad del hero. → Mitigation: reutilizar tokens existentes y validar en teléfono pequeño y ancho.
- [Risk] Google podría conservar demasiado peso visual por su branding y variante secundaria. → Mitigation: mantener el asset oficial y revisar el contenedor/variante existente frente a Entrar.
- [Risk] Cambios de layout pueden afectar el acceso a acciones con teclado abierto. → Mitigation: conservar scroll/IME y ejecutar validación manual cuando haya dispositivo.
- [Risk] Reducir demasiado el ritmo vertical puede mezclar hero y formulario o generar colisiones en pantallas pequeñas. → Mitigation: reducir solo separaciones tokenizadas, conservar `verticalScroll`/`imePadding()` y validar el flujo con teclado.
- [Risk] Tests estáticos pueden depender de markers o estructura previa. → Mitigation: actualizar solo las aserciones de presentación necesarias y conservar contratos de autenticación.

## Migration Plan

1. Ajustar la composición común de `AuthScreen.kt` y las variantes visuales solo si el análisis confirma que son necesarias.
2. Actualizar o agregar pruebas de presentación para ancho común, límites tokenizados y jerarquía de acciones.
3. Ejecutar `openspec validate --strict`, `testDebugUnitTest` y `assembleDebug`.
4. Validar visualmente resoluciones pequeñas y anchas, Light/Dark y teclado abierto; probar las tres acciones.
5. Rollback: revertir `AuthScreen.kt` y sus pruebas; no requiere migraciones ni cambios remotos.

## Open Questions

- Confirmar durante implementación cuál token existente de `AppSpacing` expresa mejor el ancho contenido del Login; no se autoriza agregar un nuevo valor sin documentar una necesidad real.
- Confirmar con revisión visual si las variantes actuales ya satisfacen la jerarquía o si Google requiere una variante secundaria existente distinta, sin modificar su branding.
- Confirmar con revisión visual que el nuevo ritmo se perciba más compacto sin agregar elementos de la referencia ni eliminar información funcional existente.
