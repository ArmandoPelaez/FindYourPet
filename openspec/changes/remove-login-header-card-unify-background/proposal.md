## Why

La card superior del Login separa visualmente el hero del formulario y hace que la pantalla se perciba como bloques independientes. SCRUM-39 requiere recuperar una composición continua sobre el fondo aprobado, conservando el contenido y el comportamiento de autenticación existentes.

## What Changes

- Eliminar la `Surface`/card contenedora que agrupa la identidad, headline, supporting text y encabezado del formulario.
- Renderizar esos elementos directamente sobre el fondo continuo ya existente.
- Mantener la jerarquía entre hero y formulario mediante spacing y tipografía tokenizados, sin agregar otra superficie equivalente.
- Conservar campos, botones, navegación, accesibilidad, responsive, temas Light/Dark y lógica de autenticación sin cambios funcionales.
- Validar visualmente la pantalla con teclado abierto y ejecutar las validaciones de build solicitadas por Jira.

## Capabilities

### New Capabilities

- `login-continuous-background`: Define la composición visual continua del Login sin una card superior que encierre el hero.

### Modified Capabilities

- Ninguna. Los requisitos funcionales de `auth` no cambian; este change modifica únicamente la estructura visual del Login.

## Impact

- Código afectado: `AuthScreen.kt` y, si fuera necesario, únicamente tokens o pruebas UI relacionadas con la composición existente.
- No se modifican APIs, dependencias, Firebase, ViewModels, repositories, textos, asset de fondo ni lógica de autenticación.
- No hay impacto de privacidad, seguridad, datos o permisos.
- El rollback consiste en restaurar la superficie del hero en la rama del change, sin migraciones ni cambios persistentes.
- Aplica el guardrail de no introducir valores visuales hardcodeados y respetar `docs/design-system.md`.
