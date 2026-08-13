## Why

La pantalla de creación de publicaciones todavía no ofrece un campo independiente para registrar señas particulares —por ejemplo, collar, manchas o cicatrices— que ayuden a reconocer una mascota perdida. SCRUM-11 solicita agregar ese dato junto al flujo actual de creación y conservarlo como parte de la publicación.

## What Changes

- Agregar en `CreatePetPostScreen` un textbox independiente inmediatamente después de `Características` y antes de `Detalles adicionales`.
- Mostrar la etiqueta `Señas particulares` como campo opcional, sin indicador `*` ni icono dentro del campo.
- Transportar el valor por el flujo de creación y persistirlo como atributo independiente `particularMarks` en Room y Firestore.
- Mantener `Características` y `Detalles adicionales` como entradas independientes, sin alterar el mapeo existente de `characteristics` y `features`.
- Mantener la identidad visual, los tokens del Design System, Material 3 estable, Light Theme y Dark Theme.
- No modificar otros formularios, navegación, permisos ni lógica de negocio no relacionada.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `pet-posts`: agregar al contrato del formulario de creación el campo opcional de señas particulares y su mapeo independiente hacia la publicación persistida.

## Impact

- Código afectado: `CreatePetPostScreen.kt`, `PetViewModel`, `PetPostEntity`, `PetPostDocument`, mappers Room/Firestore, `AppDatabase` y pruebas del formulario/mappers.
- Room requerirá una migración aditiva para instalaciones existentes; los registros anteriores deberán mapear el nuevo atributo como cadena vacía.
- Firestore recibirá y devolverá `particularMarks` como campo independiente; no se agrega una colección ni una dependencia.
- No hay nuevo acceso a datos sensibles, permisos ni exposición de contacto: las señas forman parte de la publicación existente y quedan sujetas a sus reglas actuales.
- Los usuarios existentes conservarán sus publicaciones; el nuevo atributo será vacío en datos legacy y opcional en nuevas publicaciones.
- Rollback: revertir la UI, el mapeo y la migración compatible; no se eliminan los campos históricos existentes.
- Guardrails aplicables: respetar `docs/design-system.md`, no introducir valores visuales hardcodeados, soportar ambos temas y evitar cambios fuera del flujo de publicaciones.
