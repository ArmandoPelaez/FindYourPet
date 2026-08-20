## Why

SCRUM-45 necesita fijar un contrato único para cuentas creadas con Email/Password o Google Sign-In. El repositorio actual conserva una credencial Google pendiente y puede vincularla después del login con contraseña, lo que contradice la decisión funcional de mantener un solo proveedor por cuenta y debe resolverse antes de ampliar el flujo de autenticación.

## What Changes

- **BREAKING**: establecer que cada cuenta FindYourPet conserva exclusivamente el proveedor con el que fue creada originalmente.
- Definir el comportamiento para cuentas existentes Email/Password, cuentas existentes Google y usuarios nuevos de ambos métodos.
- Definir la detección y comunicación de conflictos entre proveedores sin depender exclusivamente de consultas susceptibles a Email Enumeration Protection.
- Rechazar el método alternativo con un mensaje funcional recuperable, sin mostrar errores técnicos de Firebase.
- Prohibir `account linking`, conversión de proveedores, creación de cuentas duplicadas y modificación del UID o datos existentes.
- Alinear la implementación y sus pruebas con el contrato; el rollback debe restaurar la última versión validada del flujo sin migrar ni modificar cuentas existentes.

## Capabilities

### New Capabilities

<!-- No se introduce una capability nueva; se endurece el contrato de autenticación existente. -->

### Modified Capabilities

- `auth`: agregar exclusividad de proveedor, reglas de colisión, mensajes funcionales y preservación de identidad/UID.

## Impact

- Afecta `FirebaseAuthRepository`, el mapeo de errores de autenticación, los estados/mensajes de la pantalla de Login y las pruebas de contrato.
- Firebase Authentication debe permanecer configurado con una cuenta por dirección de email; no se agregan proveedores ni dependencias nuevas.
- Los usuarios existentes conservan su UID, proveedor original y datos asociados; los intentos con otro método se rechazan de forma recuperable.
- Impacto de seguridad: evita vinculación involuntaria, duplicación de identidad y exposición directa de errores técnicos; se aplican los guardrails de autenticación, autorización y datos sensibles.
