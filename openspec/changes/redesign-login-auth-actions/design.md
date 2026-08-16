## Context

`AuthScreen` ya ejecuta autenticación por email/password y Google mediante los callbacks existentes de `PetViewModel`. Actualmente renderiza Entrar como `AppButton` primario, Google como `AppButton` outlined y Crear cuenta como `TextButton`, pero el orden y la comunicación de jerarquía deben quedar explícitos en la composición. El cambio es de UI Compose y estado de presentación; no modifica Firebase, ViewModel, repositorios ni contratos de dominio.

## Goals / Non-Goals

**Goals:**

- Hacer visible una única acción primaria: Entrar.
- Mantener Google como acción secundaria y Crear cuenta como acción terciaria.
- Reutilizar `AppButton`, `AppButtonVariant`, `MaterialTheme` y tokens existentes del Design System.
- Exponer feedback visual mientras cualquier flujo de autenticación está en curso.
- Impedir activaciones repetidas de las acciones durante una operación activa.
- Mantener los errores de autenticación como estados recuperables y accesibles.

**Non-Goals:**

- Cambiar la lógica de Firebase Auth o Credential Manager.
- Cambiar callbacks, navegación, ViewModel, repositorios o modelos de datos.
- Agregar dependencias, permisos, endpoints o persistencia.
- Rediseñar el encabezado, los campos o la identidad visual fuera de las acciones de autenticación.
- Introducir un logo de Google redibujado, genérico, monocromo o recoloreado.
- Cambiar los contratos de autenticación; el asset de marca es un recurso de presentación y no una dependencia del flujo de Credential Manager.

## Decisions

### Jerarquía de componentes

Se conservará `AppButton` como componente común. Entrar usará la variante primaria; Google usará la variante outlined/neutral existente; Crear cuenta permanecerá como `TextButton` terciario. Esto evita valores visuales hardcodeados y mantiene consistencia con el Design System.

### Excepción de branding de Google

La acción de Google debe usar un asset oficial/preaprobado de las Sign in with Google Branding Guidelines vigentes, o un botón renderizado por una solución oficial de Google. El `G` estándar debe conservar sus colores oficiales, proporciones, tamaño y espaciado; no se acepta `Icons.Outlined.AccountCircle`, un icono genérico, una versión monocroma ni un logo redibujado.

El contenedor externo puede integrarse con FindYourPet y sus tokens cuando no contradiga la guía de Google. Para el área de marca prevalecen las reglas de Google, incluyendo el uso del asset aprobado, la preservación de proporciones y los espaciados Android/Web definidos por la guía. Esta es una excepción explícita a la regla local de reutilizar exclusivamente componentes/tokens visuales del Design System.

### Estado de carga y doble submit

El estado de carga observable de `AuthUiState` será la fuente para deshabilitar Entrar, Google y el cambio de modo mientras la autenticación está en curso. El flujo de Google también deberá proteger su coroutine de una segunda activación hasta que termine. El feedback se mostrará con componentes y colores de Material 3 existentes, sin inventar un nuevo contrato de ViewModel.

### Feedback de error

Los mensajes existentes de `localMessage` y `authMessage` se conservarán y se presentarán como estado recuperable debajo de las acciones. Cancelación o fallo de Google no navegará ni cambiará el estado autenticado; solo actualizará el mensaje visible.

### Accesibilidad

Las tres acciones conservarán etiquetas semánticas claras (`Entrar`, `Continuar con Google`, `Crear una cuenta`) y el estado disabled provisto por Material 3. La jerarquía visual no dependerá únicamente del color.

## Risks / Trade-offs

- [Riesgo] El flujo Google se ejecuta en una coroutine separada del estado de Firebase y puede terminar antes de que `AuthUiState` cambie. → Mitigación: usar un estado local de operación Google y liberarlo en `onSuccess`/`onFailure`, combinándolo con `isAuthLoading` para bloquear acciones.
- [Riesgo] Un indicador de carga puede alterar el alto de los botones. → Mitigación: usar el contenido y tokens de `AppButton`, manteniendo dimensiones mínimas existentes.
- [Riesgo] Un asset genérico o alterado puede incumplir la verificación de Google. → Mitigación: incorporar únicamente un asset oficial/preaprobado o un botón renderizado por Google y añadir una prueba que rechace el icono genérico actual.
- [Riesgo] La edición de UI puede afectar Light/Dark Theme. → Mitigación: usar exclusivamente `MaterialTheme.colorScheme`, variantes de `AppButton` y validación manual en ambos temas.

## Migration Plan

No hay migración de datos ni de configuración. El despliegue es un cambio de composición de `AuthScreen`; el rollback consiste en revertir el commit del change. Se deben ejecutar tests unitarios, build debug y revisión manual de los tres estados de acción y de errores antes de integrar.

## Open Questions

- Seleccionar el asset oficial/preaprobado adecuado para el modo visual soportado por la pantalla y conservar su procedencia en el change.
