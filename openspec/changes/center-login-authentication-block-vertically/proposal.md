## Why

La referencia requiere separar IdentityHeader, Hero y AuthenticationBlock. Solo Hero debe alcanzar la composicion visual inferior sin alterar la posicion actual de IdentityHeader ni el comportamiento de AuthenticationBlock.

## What Changes

- Mantener IdentityHeader (marca/icono + FindYourPet) en su coordenada vertical actual.
- Separar IdentityHeader, Hero y AuthenticationBlock en regiones independientes.
- Mover hacia abajo unicamente Hero (headline + supporting text) hasta la composicion marcada.
- Mantener exactamente la coordenada vertical, distribucion y spacing interno actuales de Iniciar sesion, Email, Contrasena, Entrar, Google y Crear una cuenta.
- Prohibir padding, spacer u offset en un padre compartido cuando desplacen AuthenticationBlock.

## Capabilities

### New Capabilities

- `login-vertical-auth-layout`: posicionamiento independiente de IdentityHeader, Hero y AuthenticationBlock.

### Modified Capabilities

- Ninguna. Los requisitos funcionales de `auth` no cambian; el change modifica unicamente la composicion visual.

## Impact

- Codigo afectado: composicion de `AuthScreen` y pruebas de presentacion.
- No se modifican ViewModel, Firebase Auth, repositorios, navegacion, permisos, contratos de dominio, dependencias ni backend.
- No hay impacto de privacidad, seguridad, datos o permisos.
- Rollback: revertir el commit del change restaura la composicion previa sin cambios funcionales.

## Reference-only text refinement

- Usar la imagen unicamente como referencia para alinear a start y formatear con tokens existentes el headline y supporting text del Hero.
- Alinear a start la etiqueta visible `Iniciar sesión` con los campos.
- No copiar imagenes, fondos, controles ni elementos adicionales de la referencia; no alterar coordenadas ni spacing de los controles.
