## Context

`AuthScreen` ya usa `OutlinedTextField`, `AppFormTypography.input`, `FormFieldLabel`, `AppShapes.content` y el estado de autenticación del `PetViewModel`. Los campos Email y Contraseña todavía no exponen placeholder, visibilidad de contraseña, acciones de teclado, autofill ni errores asociados al campo. El cambio es de UI e interacción local; Firebase Auth, callbacks y contratos del ViewModel deben permanecer sin cambios.

## Goals / Non-Goals

**Goals:**

- Integrar Email y Contraseña visualmente con el formulario existente sin competir con el CTA.
- Hacer visibles los estados focused, error y disabled usando componentes Material 3 y tokens existentes.
- Agregar placeholder, mostrar/ocultar contraseña, teclado contextual y submit desde IME.
- Mantener el comportamiento actual de Login y Registro, incluido el callback final al ViewModel.

**Non-Goals:**

- No cambiar Firebase Auth, repositories, `PetViewModel`, validaciones de dominio ni contratos de datos.
- No agregar dependencias, permisos, persistencia ni APIs alpha, beta o experimentales.
- No rediseñar el encabezado, botones, Google Sign-In ni la navegación fuera de los campos.

## Decisions

1. **Conservar `OutlinedTextField` y la estructura de `AuthScreen`.**
   - Se mantienen iconos, shape y layout actuales, ajustando solo la presentación y estados de Email/Contraseña.
   - Alternativa descartada: reemplazar por un componente nuevo, porque duplicaría comportamiento y aumentaría la superficie visual sin aprobación de tokens.

2. **Usar los tokens de formulario existentes.**
   - Labels usarán `FormFieldLabel`; texto ingresado usará `AppFormTypography.input`; placeholders usarán `FormFieldPlaceholder`/`AppFormTypography.placeholder` cuando corresponda.
   - Colores y estados usarán `MaterialTheme.colorScheme` y la configuración estable de Material 3; shapes y espaciado seguirán `AppShapes.content` y `AppSpacing`.
   - No se declararán `TextStyle`, `sp`, `dp`, `Color(...)` ni radios dentro de la pantalla.

3. **Mantener el valor de contraseña y controlar solo su transformación visual.**
   - Un estado local `passwordVisible` alternará entre texto normal y `PasswordVisualTransformation`.
   - El trailing icon será accesible y cambiará su descripción entre mostrar y ocultar, sin modificar el valor ni el callback de autenticación.
   - Alternativa descartada: mover la contraseña a un estado externo o al ViewModel, porque no es necesario para este alcance y aumentaría el riesgo de exponer datos sensibles.

4. **Separar presentación de error de la autenticación.**
   - Los estados `isError` y supporting/error text se alimentarán de validaciones locales de entrada y de los errores ya disponibles, sin cambiar el contrato del ViewModel.
   - Email mostrará errores asociados a entrada vacía/formato inválido y Contraseña a entrada vacía o inválida según las validaciones existentes; el formulario no llamará al ViewModel cuando una validación local impida el submit.
   - Los errores remotos que no puedan atribuirse de forma segura a un campo conservarán el mecanismo global existente.
   - Alternativa descartada: modificar Firebase o `PetViewModel` para devolver un nuevo modelo de errores, fuera de alcance.

5. **Configurar teclado y foco con APIs estables del proyecto.**
   - Email usará teclado de correo y acción `Next`; al avanzar moverá el foco a Contraseña.
   - Contraseña usará acción `Done`; al completar ejecutará la misma acción que el CTA `Entrar`/`Crear cuenta`, respetando las validaciones locales.
   - Se mantendrá el scroll y `imePadding` existentes para que el teclado no oculte los campos.

6. **Mantener accesibilidad y diferenciación visual de estados.**
   - Los iconos de campo conservarán contenido descriptivo apropiado y el control de visibilidad tendrá una acción anunciable.
   - Focused, error y disabled se distinguirán mediante los estados Material 3 y colorScheme, manteniendo contraste en Light/Dark.

## Risks / Trade-offs

- [La validación local puede duplicar mensajes remotos] → Reutilizar el estado y mensajes existentes, validar solo condiciones observables de entrada y dejar los errores no atribuibles en el mensaje global.
- [La acción IME puede ejecutarse mientras el teclado cubre el CTA] → Compartir la misma función de submit del botón y conservar `imePadding`/scroll.
- [Mostrar contraseña reduce temporalmente la privacidad visual] → Mantenerlo como acción explícita del usuario, con icono/semántica claros y sin persistir el estado fuera de la pantalla.

## Migration Plan

No hay migración de datos ni cambios de API. Implementar en `AuthScreen`, actualizar pruebas estáticas/unitarias de presentación e interacción, ejecutar OpenSpec, tests y `assembleDebug`, y revisar manualmente Login/Registro en Light/Dark, teclado, errores y password visibility. El autofill queda fuera de este change y se retomará mediante una actualización separada de Compose/API. El rollback consiste en revertir la composición y estados locales de los dos campos.

## Open Questions

No hay preguntas bloqueantes. Los textos exactos de placeholders y mensajes deben seguir el idioma y convenciones existentes de `AuthScreen`; el criterio funcional es que sean claros, asociados al campo y consistentes con el Design System. Autofill está diferido por decisión registrada en Jira.
