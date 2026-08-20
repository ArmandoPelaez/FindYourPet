# Orchestration State: define-exclusive-auth-strategy

state: INTEGRATED
phase: INTEGRATED
issue: SCRUM-45
change: define-exclusive-auth-strategy
base_branch: main
base_commit: 8ba64eba72f9bd46eca7132adf7516bc266c3a21
remote_base_commit: 8ba64eba72f9bd46eca7132adf7516bc266c3a21
branch: ops/define-exclusive-auth-strategy
branch_head_after_creation: 8ba64eba72f9bd46eca7132adf7516bc266c3a21
delegation_status: COMPLETED
handoff_mode: SUBAGENT
agent_id: 01a01bf0-14dc-7d02-a61f-ae63ce47a1a7
agent_role: findyourpet-implementer
delegation_error:
integration_status: MERGED
integrated_commit: 0dfbac6abfc6d4936e97e737d084a5e42d74e2c6
integration_evidence: PR #61 merged into main; main and origin/main synchronized at 0dfbac6abfc6d4936e97e737d084a5e42d74e2c6.

## Preflight y sincronización

- `git status --short --branch`: `## main...origin/main` antes de crear la rama.
- `git status --porcelain=v1`: vacío.
- `git switch main`: correcto.
- `git fetch origin --prune`: correcto.
- `git pull --ff-only origin main`: correcto; `main` avanzó hasta `8ba64eba72f9bd46eca7132adf7516bc266c3a21`.
- `git rev-parse main`: `8ba64eba72f9bd46eca7132adf7516bc266c3a21`.
- `git rev-parse origin/main`: `8ba64eba72f9bd46eca7132adf7516bc266c3a21`.
- Ramas no fusionadas revisadas; existe trabajo paralelo documentado y autorizado por el usuario.
- La rama de trabajo fue creada desde `main` sincronizada.

## Autorización de trabajo paralelo

- El usuario autorizó explícitamente continuar SCRUM-45 en paralelo el 2026-08-19.
- Change activo preexistente relevante: `adjust-login-width-and-action-hierarchy` / SCRUM-42, `PASSED_PENDING_INTEGRATION`.

## Jira Scrum normalizado

- Issue: `SCRUM-45` — `Definir estrategia de autenticación exclusiva por método`.
- Tipo: Task.
- Estado Jira: To Do.
- Prioridad: Medium.
- Fecha límite: `2026-08-19`.
- Dependencias, subtareas, enlaces y adjuntos: no declarados.
- Objetivo: definir el comportamiento de autenticación cuando el correo ya existe con Email/Password o Google Sign-In.
- Regla central: cada cuenta conserva el único método con el que fue creada originalmente.
- Fuera de alcance: account linking, migración de cuentas, agregar un segundo proveedor, administración de métodos desde Perfil/Seguridad y creación de cuentas duplicadas.

## Contraste técnico inicial

- `openspec/specs/auth/spec.md` ya documenta Email/Password, Google Sign-In, sesión y logout, pero no fija la estrategia de proveedor exclusivo ni los conflictos entre métodos.
- `FirebaseAuthRepository.kt` actualmente captura `FirebaseAuthUserCollisionException`, conserva una credencial Google pendiente y ejecuta `linkWithCredential` después del login con contraseña; esto contradice el alcance explícito de SCRUM-45, que prohíbe account linking.
- La documentación actual consultada de Firebase indica que `account-exists-with-different-credential` representa una colisión entre proveedores y que `fetchSignInMethodsForEmail` puede devolver una lista vacía cuando Email Enumeration Protection está habilitada; la estrategia debe evitar depender de esa consulta como única detección.
- La propuesta deberá preservar el UID y los datos existentes, rechazar el método alternativo con un mensaje funcional claro y mantener la sesión sin exponer errores técnicos de Firebase.

## OpenSpec

- Change creado con schema `spec-driven`.
- Artefactos completos: `proposal.md`, `design.md`, `specs/auth/spec.md`, `tasks.md`.
- `openspec status --change "define-exclusive-auth-strategy"`: 4/4 artefactos completos.
- `openspec validate "define-exclusive-auth-strategy" --strict`: válido.
- El delta modifica la capability existente `auth` y prohíbe explícitamente `linkWithCredential`, credenciales pendientes, conversión de proveedores y cuentas duplicadas.
- El diseño deja como riesgo operativo verificar la configuración Firebase de una cuenta por email y el comportamiento real de Email Enumeration Protection.

## Handoff pendiente

- Implementar únicamente el change OpenSpec `define-exclusive-auth-strategy` para SCRUM-45.
- No implementar trabajo de SCRUM-42 ni modificar sus ramas/artefactos.
- La implementación debe retirar el linking existente antes de ejecutar la verificación final.

## Delegación

- `delegation_status: SPAWNED`.
- `handoff_mode: SUBAGENT`.
- `agent_id: 01a01bf0-14dc-7d02-a61f-ae63ce47a1a7` (`Singer`).
- `agent_role: findyourpet-implementer`.
- `delegation_error:` vacío.

## Reporte del implementador

- Estado reportado: `READY_FOR_VERIFICATION`.
- Progreso: `11/15` tareas.
- Implementado: resultados de dominio para conflictos, eliminación de credenciales pendientes y `linkWithCredential`, detección segura con fallback, preservación de UID/sesión, mensajes funcionales y pruebas de exclusividad/conflictos.
- Pendientes: tareas manuales `3.1–3.4`, que requieren cuentas Firebase reales, dispositivo/emulador y confirmación de Email Enumeration Protection.
- Evidencia reportada: `openspec validate --strict`, `testDebugUnitTest`, `assembleDebug` y `git diff --check` correctos.
- Riesgo reportado: `fetchSignInMethodsForEmail` está deprecated; se mantiene conforme al diseño y se usa fallback genérico ante respuesta vacía/inconcluyente.

## Verificación del orquestador

- `openspec instructions apply --change "define-exclusive-auth-strategy" --json`: 11/15 tareas; quedan `3.1–3.4` por validación manual.
- `openspec validate "define-exclusive-auth-strategy" --strict`: correcto.
- `./gradlew.bat testDebugUnitTest --no-daemon --console=plain`: `BUILD SUCCESSFUL`.
- `./gradlew.bat assembleDebug --no-daemon --console=plain`: correcto, APK generado en `app/build/outputs/apk/debug/app-debug.apk`.
- `git diff --check`: correcto; solo advertencias normales de conversión LF/CRLF.
- Revisión de alcance: limitada a `AuthModels.kt`, `FirebaseAuthRepository.kt`, `AuthProviderPolicy.kt`, `PetViewModel.kt`, `AuthScreen.kt`, pruebas de autenticación y artefactos OpenSpec/orquestación.
- Revisión de seguridad funcional: no quedan referencias productivas a `linkWithCredential`, `pendingGoogleLink`, `PendingGoogleLink`, `AccountLinkRequiredException` ni propagación de `error.message` en el flujo de autenticación.
- `adb`: no disponible (`adb: NOT_AVAILABLE`).
- Firebase CLI: no disponible (`firebase-cli: NOT_AVAILABLE`).

## Bloqueo

- Las tareas manuales `3.1–3.4` requieren dos cuentas Firebase reales, un dispositivo/emulador Android y acceso para confirmar la configuración “One account per email” y Email Enumeration Protection.
- No se puede verificar en este entorno que los conflictos reales preserven UID/datos, que no creen duplicados y que el fallback de enumeración coincida con la configuración Firebase desplegada.
- Estado final del change: `BLOCKED`.
- No se creó commit, merge, push ni PR automáticamente.

## Reanudación por decisión funcional

- El usuario confirmó que Email Enumeration Protection permanece activa.
- Nuevo mensaje fallback aprobado: `No pudimos iniciar sesión. Si creaste tu cuenta con Google, seleccioná 'Continuar con Google'.`
- El cambio queda limitado a ese mensaje, su especificación y cobertura; no se desactiva la protección ni se modifica la política de proveedor único.

## Reporte de reanudación

- Estado reportado: `READY_FOR_VERIFICATION`.
- Fallback actualizado exactamente al texto aprobado.
- Actualizados `AuthModels.kt`, `AuthFailureMessageTest.kt`, `openspec/.../specs/auth/spec.md` y `design.md`.
- Email Enumeration Protection permanece activa; no se agregaron linking, duplicados ni cambios de UID.
- Evidencia reportada: OpenSpec estricto, tests, assemble y diff check correctos.

## Verificación posterior del mensaje aprobado

- `android run --device=emulator-5554 --apks=app\\build\\outputs\\apk\\debug\\app-debug.apk`: correcto.
- Prueba con credenciales ficticias `test@example.com`/contraseña ficticia: el Login mostró exactamente `No pudimos iniciar sesión. Si creaste tu cuenta con Google, seleccioná 'Continuar con Google'.`
- `android layout --device=emulator-5554 --pretty`: confirmó el texto accesible en pantalla.
- Captura: `.codex/orchestration/scrum-45-updated-message.png`.
- La validación no usó ni expuso credenciales reales.
- Las tareas manuales reales `3.1–3.4` permanecen pendientes; el change continúa `BLOCKED` hasta verificar cuentas Firebase y configuración de proveedor.

## Nueva aclaración funcional

- El usuario solicitó limpiar automáticamente Email y Contraseña después de un intento Email/Password fallido que muestra el fallback orientado a Google.
- El comportamiento debe limitarse al modo Login (`isSignUp == false`), preservar el mensaje visible y no modificar la configuración de Firebase ni la política de proveedor único.

## Reporte de implementación de limpieza de campos

- Estado reportado: `READY_FOR_VERIFICATION`.
- Progreso: `12/16` tareas; se agregó y completó la tarea `2.4`.
- En Login, un fallo Email/Password limpia Email y Contraseña y conserva visible el fallback orientado a Google.
- Sign-up, Google Sign-In, linking, proveedor único y Firebase no fueron modificados.
- Archivos actualizados: `AuthScreen.kt`, `AuthFailureMessageTest.kt`, `AuthScreenPresentationStaticTest.kt`, `specs/auth/spec.md`, `design.md` y `tasks.md`.
- Evidencia reportada: OpenSpec estricto, tests focalizados, `testDebugUnitTest`, `assembleDebug` y `git diff --check` correctos.

## Verificación manual de limpieza de campos

- `android run --device=emulator-5554 --apks=app\\build\\outputs\\apk\\debug\\app-debug.apk`: correcto.
- Se cerró la sesión persistida del emulador y se probó Login con `test@example.com` y contraseña ficticia.
- Resultado: Email y Contraseña quedaron vacíos después del fallo; el mensaje permaneció visible.
- `android layout --device=emulator-5554 --pretty`: confirmó ausencia de valores en ambos campos y presencia del mensaje aprobado.
- Captura: `.codex/orchestration/scrum-45-cleared-fields.png`.
- No se utilizaron credenciales reales.
- Las validaciones reales `3.1–3.4` continúan pendientes; el change permanece `BLOCKED`.

## Integración

- Commit, merge y OR realizados.
- PR #61 mergeado en `main`.
- Commit integrado: `0dfbac6abfc6d4936e97e737d084a5e42d74e2c6`.
- Estado de bitácora: `INTEGRATED`.
