# Orchestration: login-with-google-account

## Estado actual

BLOCKED

## Fase

RECEIVE_AND_NORMALIZE_JIRA_SCRUM

## Issue Jira

- Clave: `SCRUM-44`
- Título: `Login with Google account`
- Tipo: Feature
- Estado Jira: To Do
- Prioridad: Medium
- Fecha límite: `2026-08-19`
- URL: https://pelaezarmando.atlassian.net/browse/SCRUM-44
- Dependencias, enlaces, adjuntos y comentarios: no informados

## Autorización de trabajo paralelo

- Autorizada explícitamente por el usuario durante esta ejecución.
- Changes activos existentes: `adjust-login-width-and-action-hierarchy` y `center-login-authentication-block-vertically`, ambos pendientes de integración.

## Scrum recibido

Jira solicita implementar el login con una cuenta de Google desde la pantalla de autenticación usando Firebase Authentication. Incluye:

- Inicio del flujo al presionar `Ingresar con Google`.
- Selección de una cuenta disponible mediante Google.
- Autenticación Firebase exitosa y acceso a la pantalla principal.
- Creación automática del usuario en el primer ingreso y reutilización del usuario existente.
- Cancelación y errores recuperables sin cerrar la aplicación.
- Persistencia de sesión, logout y nuevo login.
- Verificación manual en cuenta nueva/existente, cancelación, error, reapertura, logout y Light/Dark Theme.
- Fuera de alcance: rediseño general del login, cambios en email/password y otros proveedores OAuth.

## Preflight y sincronización

- `git status --short --branch` => `## main...origin/main`.
- `git status --porcelain=v1` => vacío.
- `git switch main` => correcto.
- `git fetch origin --prune` => correcto.
- `git pull --ff-only origin main` => `Already up to date.`
- `git rev-parse main` => `668ab3b79e865a900e9d5e1deffdbc5ad3fb2a4f`.
- `git rev-parse origin/main` => `668ab3b79e865a900e9d5e1deffdbc5ad3fb2a4f`.
- No se creó rama `ops/login-with-google-account`.

## Evidencia de contradicción de alcance

El Scrum afirma que la funcionalidad todavía no está implementada, pero `main` ya contiene:

- `FirebaseAuthRepository.signInWithGoogleIdToken`, usando `GoogleAuthProvider` y `FirebaseAuth.signInWithCredential`.
- `AuthScreen` con `CredentialManager`, `GetGoogleIdOption`, `GoogleIdTokenCredential`, `firebase_web_client_id` y llamada a `viewModel.signInWithGoogleIdToken`.
- Manejo explícito de cancelación, parsing inválido, error recuperable, loading y bloqueo de envíos concurrentes.
- `AuthRepository`, `PetViewModel` y `AuthUiState` integrados con la sesión actual.
- Tests estáticos que cubren callbacks, Google action, cancelación, errores, estados de loading y recuperación.
- Especificación vigente `openspec/specs/auth/spec.md` con el requisito de Google Sign-In.

El historial confirma que el flujo fue incorporado en el commit `b9ab943972d3d5442fdbc417a3abe2c7ee09bfb5` (`Implementacion de autenticacion con usuarios realesusando Firebase Auth y Firestore`, 2026-07-26), incluyendo la implementación de Firebase Auth, Credential Manager y Google Sign-In.

## Decisión y bloqueo

No se puede generar un change de implementación sin duplicar funcionalidad existente ni inventar una brecha no indicada por Jira. Se requiere confirmar una de estas decisiones:

1. SCRUM-44 está desactualizado y debe cerrarse/registrarse como ya implementado.
2. SCRUM-44 requiere una corrección concreta sobre el flujo existente; debe indicarse el escenario que falla.
3. SCRUM-44 debe convertirse en una validación manual/configuración externa; debe confirmarse el entorno Firebase, credenciales y dispositivo/emulador disponibles.

## Estado de integración

- `integration_status: PENDING`
- `integrated_commit:`
- `integration_evidence:`

