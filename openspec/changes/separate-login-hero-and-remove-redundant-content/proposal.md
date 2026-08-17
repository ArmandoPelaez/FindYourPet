## Why

La pantalla de Login mezcla el hero contextual con el encabezado y la explicación del formulario, por lo que el propósito del producto y las instrucciones de autenticación se perciben como un único bloque. SCRUM-41 requiere recuperar una jerarquía visual clara y eliminar el texto redundante del formulario sin alterar la autenticación existente.

## What Changes

- Separar semántica y visualmente el hero del formulario mediante la composición vertical y tokens de spacing existentes.
- Mantener la identidad FindYourPet, el headline aprobado y el supporting text del hero agrupados.
- Asociar `Iniciar sesión` directamente con Email, Contraseña y las acciones de autenticación.
- Eliminar del modo Login el subtítulo redundante `Accedé para seguir avisos y actualizar tus publicaciones.` y no reemplazarlo por otro texto equivalente.
- Mantener intactos los comportamientos de Email, Contraseña, Entrar, Continuar con Google y Crear una cuenta.
- Verificar la composición en pantallas pequeñas, con teclado abierto y en Light/Dark Theme.
- No agregar cards, divisores, superficies ni valores visuales arbitrarios.

## Capabilities

### New Capabilities

- `login-presentation`: Define la jerarquía visual y semántica entre el hero contextual y el formulario de autenticación.

### Modified Capabilities

- Ninguna. El contrato funcional de autenticación no cambia; la nueva capacidad documenta comportamiento de presentación específico del Login.

## Impact

- Código afectado: composición de `AuthScreen.kt` y pruebas estáticas o visuales de presentación que deban actualizarse.
- No se modifican ViewModels, repositorios, Firebase, navegación, contratos de autenticación, permisos ni dependencias.
- No hay impacto sobre datos, privacidad o seguridad.
- Usuarios existentes conservan los mismos campos, acciones, navegación y flujo de autenticación; solo cambia la jerarquía visual y se elimina texto redundante.
- Rollback: restaurar la composición anterior de `AuthScreen.kt` y sus pruebas, sin migraciones ni cambios persistentes.
- Guardrails aplicables: usar Jetpack Compose y Material 3 estable, tokens del Design System, soporte Light/Dark y ningún valor visual hardcodeado.
