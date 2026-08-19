## Context

SCRUM-43 contiene tres regiones visuales con responsabilidades separadas: IdentityHeader (marca/icono + FindYourPet), Hero (headline + supporting text) y AuthenticationBlock. La referencia marca una composicion inferior para Hero. IdentityHeader y AuthenticationBlock deben conservar sus posiciones actuales.

## Goals / Non-Goals

**Goals:**

- Dar a IdentityHeader, Hero y AuthenticationBlock boundaries de layout independientes.
- Mantener IdentityHeader en su coordenada vertical actual.
- Mover unicamente Hero headline/supporting text a la composicion indicada.
- Preservar la coordenada, subtree, spacing, scroll, IME, identidad y comportamiento actuales de AuthenticationBlock.

**Non-Goals:**

- No mover IdentityHeader ni AuthenticationBlock.
- No acoplar las regiones con padding, spacer u offset en un padre compartido.
- No cambiar textos, tipografia, colores, fondo, anchos, controles, logica de autenticacion, navegacion, dependencias ni backend.

## Decisions

### Three direct layout boundaries

IdentityHeader, Hero y AuthenticationBlock seran tres regiones directas distintas. IdentityHeader conserva su coordenada medida, Hero recibe el desplazamiento solicitado y AuthenticationBlock conserva su coordenada medida.

Alternativas rechazadas:

- Padding en padre compartido: desplaza AuthenticationBlock.
- Spacer en padre compartido: acopla las tres regiones y cambia coordenadas.
- Offset en padre compartido: es device-specific y queda fuera del alcance.
- Mover la columna completa header/hero: viola el header fijo y la separacion entre IdentityHeader y Hero.

### Responsive behavior remains at the screen boundary

El `verticalScroll()` y `imePadding()` existentes permanecen responsables de pantallas cortas y teclado abierto. El posicionamiento local no debe eliminar el scroll ni ocultar controles.

### Coordinate-based verification

Los tests estaticos verificaran los tres boundaries, el desplazamiento exclusivo de Hero y la ausencia de desplazamiento compartido. La validacion manual registrara las coordenadas de IdentityHeader, Hero y AuthenticationBlock desde un APK fresco.

## Risks / Trade-offs

- [Risk] Mover Hero independientemente puede provocar solapamiento con IdentityHeader. -> [Mitigation] validar coordenadas de las tres regiones y preservar header y autenticacion.
- [Risk] Un valor arbitrario puede violar Design System. -> [Mitigation] usar tokens existentes y no valores verticales device-specific.
- [Risk] Separar boundaries puede alterar autenticacion. -> [Mitigation] mantener intacto el subtree y ejecutar tests/build.

### Reference-only text alignment

Hero usara `Alignment.Start` y `TextAlign.Start` con tokens existentes (`headlineMedium` y `bodyLarge`) para reflejar la jerarquia tipografica de la referencia sin hardcodear valores. La etiqueta `Iniciar sesión` usara `TextAlign.Start` dentro de su boundary; el contenido de los campos y botones no se modifica.

## Migration Plan

1. Mantener IdentityHeader fijo y separar Hero sin cambiar contenidos.
2. Aplicar posicionamiento responsive local unicamente a Hero.
3. Reconstruir/instalar APK y comparar coordenadas de las tres regiones.
4. Revertir el change si cambian coordenadas o comportamiento de AuthenticationBlock.

## Open Questions

- La composicion marcada de Hero en la referencia es la fuente de verdad; IdentityHeader no debe desplazarse como efecto colateral.

## Verified repair constraint

Hero conserva los tokens tipograficos originales `headlineSmall` y `bodyMedium`. Esta restriccion mantiene la altura natural previa del Hero mientras `Alignment.Start` y `TextAlign.Start` aplican unicamente la alineacion solicitada.
