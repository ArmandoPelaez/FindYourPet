# Guia De Cambios OpenSpec - FindYourPet

Esta guia organiza los cambios propuestos para llevar FindYourPet desde un prototipo/demo local hacia un MVP inicial de produccion. La idea es evitar una mejora gigante y dividir el trabajo en cambios OpenSpec pequenos, revisables y con criterios de aceptacion claros.

## Estructura OpenSpec Recomendada

```text
openspec/
  specs/
  changes/
    nombre-del-cambio/
      proposal.md
      design.md
      tasks.md
      specs/
  config.yaml
```

Cada cambio deberia vivir dentro de `openspec/changes/<nombre-del-cambio>/` y tocar una o varias capabilities.

## Capabilities Sugeridas

- `auth`
- `pet-posts`
- `sightings`
- `private-chat`
- `contact-privacy`
- `notifications`
- `local-storage`
- `media-upload`
- `location`
- `release-readiness`

## Orden Recomendado

1. `stabilize-android-build`
2. `harden-local-privacy`
3. `add-user-authentication`
4. `introduce-production-backend`
5. `redesign-contact-sharing`
6. `add-real-photo-upload`
7. `add-location-capture`
8. `add-push-notifications`
9. `prepare-production-release`

## Etapa 0: Base Del Proyecto

**Objetivo:** dejar el proyecto compilable, limpio y entendible.

**Problemas que resuelve:**

- Tests rotos.
- Falta de Gradle wrapper.
- Codigo de plantilla.
- Nombres inconsistentes.
- Textos con encoding roto.
- Dependencias sin uso.

**Propuestas OpenSpec sugeridas:**

- `stabilize-android-build`
- `clean-demo-code`
- `fix-project-encoding-and-naming`

**Tareas principales:**

- Agregar o regenerar `gradlew`.
- Hacer que `testDebugUnitTest` compile.
- Eliminar tests de plantilla rotos.
- Corregir `GreetingScreenshotTest`.
- Revisar el package name `com.example`.
- Limpiar imports y dependencias no usadas.
- Corregir textos mojibake en etiquetas como "Dueno", "Telefono", "Direccion", etc.

**Criterios de aceptacion:**

- El proyecto compila.
- Los tests pasan.
- No quedan referencias a clases inexistentes.
- El codigo deja claro que es demo y que es funcional.

## Etapa 1: Seguridad Y Privacidad Local

**Objetivo:** evitar que datos personales queden expuestos.

**Problemas que resuelve:**

- Datos sensibles guardados en claro.
- Backup automatico inseguro.
- Promesas de "encriptacion" que hoy no son reales.
- Permisos sensibles innecesarios.

**Propuestas OpenSpec sugeridas:**

- `harden-local-privacy`
- `secure-backup-rules`
- `normalize-permission-usage`

**Tareas principales:**

- Desactivar `allowBackup` o excluir base de datos y archivos sensibles.
- Decidir si se cifran datos locales.
- Quitar permisos no usados como camara/GPS si todavia no hay flujo real.
- No mostrar textos que digan "encriptado" hasta implementarlo.
- Definir una politica clara de que datos se guardan localmente.

**Criterios de aceptacion:**

- Telefono, email, direccion y ubicacion no quedan expuestos innecesariamente.
- Android backup no copia datos sensibles.
- La app no pide permisos que no usa.
- Los mensajes de privacidad reflejan lo que el codigo realmente hace.

## Etapa 2: Autenticacion Y Usuarios Reales

**Objetivo:** reemplazar usuarios hardcodeados por cuentas reales.

**Problemas que resuelve:**

- `user_1`, `owner_1` y reglas falsas de dueno.
- Acciones sensibles controladas solo por el cliente.
- Ausencia de login.

**Propuestas OpenSpec sugeridas:**

- `add-user-authentication`
- `replace-hardcoded-user-profile`
- `define-user-ownership-rules`

**Tareas principales:**

- Elegir proveedor: Firebase Auth, Supabase Auth, backend propio, etc.
- Crear flujo de login/logout.
- Guardar perfil real del usuario.
- Asociar publicaciones al usuario autenticado.
- Eliminar logica como `currentUser.id == "owner_1"`.

**Criterios de aceptacion:**

- Cada usuario tiene identidad real.
- Solo el dueno puede editar o cerrar su publicacion.
- No hay permisos basados en strings hardcodeados.

## Etapa 3: Backend Y Datos Compartidos

**Objetivo:** que la app deje de ser local/demo y funcione como red real.

Este es el paso mas importante para pasar de demo a producto.

**Problemas que resuelve:**

- Cada usuario ve solo su base local.
- Chats y publicaciones no son compartidos realmente.
- No hay persistencia central.
- No hay reglas de acceso reales.

**Propuestas OpenSpec sugeridas:**

- `introduce-production-backend`
- `sync-pet-posts`
- `sync-sighting-alerts`
- `sync-private-chats`

**Tareas principales:**

- Definir modelo backend para usuarios, mascotas, avistamientos, chats y notificaciones.
- Mover publicaciones desde Room local hacia backend.
- Conservar Room solo como cache local si hace falta.
- Agregar reglas de lectura/escritura.
- Manejar estados de carga, error y sincronizacion.

**Criterios de aceptacion:**

- Una publicacion creada por un usuario puede ser vista por otros.
- Los avistamientos llegan al dueno correcto.
- El chat funciona entre dos usuarios reales.
- No se puede leer o modificar informacion sin permiso.

## Etapa 4: Contacto Privado Y Flujo De Seguridad

**Objetivo:** arreglar la logica de privacidad del contacto.

**Problemas que resuelve:**

- `isContactRevealedToAll` e `isContactSharedByOwner` se contradicen.
- Contacto compartido en una pantalla pero no en otra.
- Riesgo de exponer telefono/email.

**Propuestas OpenSpec sugeridas:**

- `redesign-contact-sharing`
- `add-contact-consent-flow`
- `audit-contact-visibility`

**Decision de diseno necesaria:**

- Opcion A: el contacto solo se comparte dentro de un chat especifico.
- Opcion B: el contacto se revela publicamente en la ficha.

**Recomendacion:** elegir la opcion A, porque reduce exposicion innecesaria de datos personales.

**Tareas principales:**

- Eliminar "revelar publicamente" si no es necesario.
- Guardar consentimiento por chat.
- Mostrar datos solo al usuario autorizado.
- Registrar cuando se compartio o revoco el contacto.
- Evitar mandar telefono/email dentro de notificaciones push.

**Criterios de aceptacion:**

- El contacto solo aparece donde el dueno lo autorizo.
- Revocar contacto lo oculta inmediatamente.
- No hay contradiccion entre ficha, chat y notificaciones.

## Etapa 5: Funciones Reales De Producto

**Objetivo:** reemplazar simulaciones por funciones reales.

**Problemas que resuelve:**

- Fotos preset.
- GPS simulado.
- Alertas "en tiempo real" que no lo son.
- Notificaciones locales en vez de push real.

**Propuestas OpenSpec sugeridas:**

- `add-real-photo-upload`
- `add-location-capture`
- `add-push-notifications`
- `replace-demo-seed-data`

**Tareas principales:**

- Implementar camara/galeria con permisos reales.
- Subir imagenes a storage seguro.
- Capturar ubicacion real con consentimiento.
- Implementar notificaciones push.
- Remover o aislar `seedInitialDataIfNeeded`.
- Agregar validaciones de formulario.

**Criterios de aceptacion:**

- El usuario puede publicar una mascota con foto real.
- Puede enviar ubicacion real en un avistamiento.
- El dueno recibe alerta real.
- La app funciona sin datos demo.

## Etapa 6: Calidad, Release Y Monitoreo

**Objetivo:** preparar una primera publicacion controlada.

**Problemas que resuelve:**

- Release inseguro.
- Poca cobertura de tests.
- Errores dificiles de diagnosticar.
- Falta de checklist Google Play.

**Propuestas OpenSpec sugeridas:**

- `prepare-production-release`
- `add-core-test-coverage`
- `add-crash-and-analytics-monitoring`

**Tareas principales:**

- Activar minificacion en release.
- Configurar firma release correctamente.
- Agregar tests de repositorio, ViewModel y flujos criticos.
- Revisar accesibilidad basica.
- Agregar crash reporting.
- Crear politica de privacidad.
- Revisar permisos para Google Play.

**Criterios de aceptacion:**

- Build release generado.
- Flujos principales probados.
- Errores monitoreables.
- Permisos justificados.
- Politica de privacidad lista.

## Reglas Para Propuestas OpenSpec

### `proposal.md`

- Explicar que problema de produccion resuelve.
- Indicar si el cambio afecta privacidad, seguridad, datos o permisos.
- Incluir impacto sobre usuarios existentes.
- Incluir estrategia de rollback cuando aplique.

### `design.md`

- Separar decisiones de cliente Android, backend y almacenamiento local.
- Describir reglas de autorizacion para acciones sensibles.
- Evitar prometer cifrado o privacidad si no esta implementado tecnicamente.
- Incluir manejo de errores, estados vacios y permisos runtime.

### Specs

- Usar lenguaje observable por el usuario o el sistema.
- Incluir escenarios `Given/When/Then`.
- Definir claramente quien puede leer, crear, modificar o borrar datos.
- Tratar ubicacion, contacto y mensajes como datos sensibles.

### `tasks.md`

- Separar tareas de implementacion, pruebas y validacion manual.
- Incluir actualizacion o creacion de tests.
- Incluir verificacion de build antes de cerrar el cambio.
- Marcar limpieza de codigo demo cuando corresponda.

## Buenas Practicas Para `config.yaml`

Segun la documentacion de OpenSpec, `openspec/config.yaml` conviene usarlo para dos cosas:

- `context`: informacion global que debe influir en todos los cambios.
- `rules`: reglas por tipo de artefacto, como `proposal`, `design`, `specs` y `tasks`.

En este proyecto, eso significa:

- Los goals generales deben ir en `context`, porque describen hacia donde debe evolucionar FindYourPet.
- Las barreras o guardrails deben ir en `context` cuando son restricciones globales del producto.
- La estructura OpenSpec esperada, las capabilities y el orden recomendado de cambios pueden ir en `context`, porque orientan todos los cambios futuros.
- Las reglas para propuestas deben ir en `rules.proposal`, `rules.design`, `rules.specs` y `rules.tasks`.
- Los arneses de validacion deben aparecer como reglas de `tasks`, porque cada cambio debe declarar como se prueba y valida.

Conviene evitar claves inventadas de primer nivel como `goals:`, `guardrails:` o `validation_harnesses:` si no se confirma que la version de OpenSpec las interpreta. Para mantener compatibilidad, es mejor poner esas secciones dentro del bloque `context`.

## Base Sugerida Para `openspec/config.yaml`

```yaml
schema: spec-driven

context: |
  Proyecto: FindYourPet / Mascotas Perdidas
  Plataforma: Android nativo con Kotlin, Jetpack Compose, Room y Gradle Kotlin DSL.
  Estado actual: prototipo/demo con datos locales, usuario hardcodeado, fotos preset y GPS simulado.
  Objetivo: evolucionar a MVP inicial de produccion con autenticacion real, backend, privacidad verificable, datos compartidos, chat real y release Android seguro.

  Prioridades:
  1. Seguridad y privacidad de datos personales.
  2. Autenticacion y autorizacion real.
  3. Backend para publicaciones, avistamientos, chats y notificaciones.
  4. Reemplazo de simulaciones por camara, galeria, GPS y push reales.
  5. Calidad de build, tests, release y cumplimiento Google Play.

  Estructura OpenSpec recomendada:
  openspec/
    specs/
    changes/
      nombre-del-cambio/
        proposal.md
        design.md
        tasks.md
        specs/
    config.yaml

  Cada cambio debe vivir dentro de openspec/changes/<nombre-del-cambio>/ y tocar una o varias capabilities.

  Capabilities sugeridas:
  - auth
  - pet-posts
  - sightings
  - private-chat
  - contact-privacy
  - notifications
  - local-storage
  - media-upload
  - location
  - release-readiness

  Orden recomendado para crear cambios:
  1. stabilize-android-build
  2. harden-local-privacy
  3. add-user-authentication
  4. introduce-production-backend
  5. redesign-contact-sharing
  6. add-real-photo-upload
  7. add-location-capture
  8. add-push-notifications
  9. prepare-production-release

  Goals generales:
  - Convertir la app de demo local en un MVP usable por usuarios reales.
  - Proteger datos personales antes de agregar mas superficie de producto.
  - Reemplazar identidades hardcodeadas por autenticacion real.
  - Centralizar datos compartidos en un backend con reglas de acceso.
  - Mantener Room solo como almacenamiento local/cache cuando sea apropiado.
  - Sustituir simulaciones por camara, galeria, ubicacion y push reales.
  - Llegar a un release Android verificable, testeado y monitoreable.

  Datos sensibles:
  - Nombre del dueno
  - Telefono
  - Email
  - Direccion
  - Coordenadas GPS
  - Fotos
  - Mensajes privados
  - Historial de avistamientos

  Barreras / guardrails:
  - No exponer telefono, email, direccion o coordenadas sin consentimiento explicito.
  - No basar permisos de dueno en strings hardcodeados como user_1 u owner_1.
  - No prometer cifrado, privacidad o tiempo real si el codigo no lo implementa.
  - No pedir permisos Android que no tengan un flujo real y justificable.
  - No enviar datos sensibles completos en notificaciones push.
  - No permitir que Android backup copie bases de datos o archivos sensibles.
  - No introducir backend sin reglas de lectura/escritura por usuario.
  - No cerrar cambios de produccion sin build, tests o validacion manual documentada.

  Arneses de validacion esperados:
  - Build Android debug antes de cerrar cambios de codigo.
  - Tests unitarios cuando se toque repositorio, ViewModel, validaciones o reglas de negocio.
  - Validacion manual de flujos criticos cuando se toque UI o permisos runtime.
  - Revision de manifest, backup rules y permisos cuando se toque privacidad.
  - Prueba de reglas de autorizacion cuando se toque auth, backend, chat o contacto.

rules:
  proposal:
    - Explicar que problema de produccion resuelve.
    - Indicar si el cambio afecta privacidad, seguridad, datos o permisos.
    - Incluir impacto sobre usuarios existentes.
    - Incluir estrategia de rollback cuando aplique.
    - Vincular el cambio con uno o mas goals generales.
    - Declarar que guardrails aplican.

  design:
    - Separar decisiones de cliente Android, backend y almacenamiento local.
    - Describir reglas de autorizacion para acciones sensibles.
    - Evitar prometer cifrado o privacidad si no esta implementado tecnicamente.
    - Incluir manejo de errores, estados vacios y permisos runtime.
    - Explicar como se protegen datos sensibles en reposo, transito y UI.
    - Documentar migracion o compatibilidad cuando se cambie persistencia local.

  specs:
    - Usar lenguaje observable por el usuario o el sistema.
    - Incluir escenarios Given/When/Then.
    - Definir claramente quien puede leer, crear, modificar o borrar datos.
    - Tratar ubicacion, contacto y mensajes como datos sensibles.
    - Cubrir escenarios de denegacion, revocacion y errores, no solo el camino feliz.

  tasks:
    - Separar tareas de implementacion, pruebas y validacion manual.
    - Incluir actualizacion o creacion de tests.
    - Incluir verificacion de build antes de cerrar el cambio.
    - Marcar limpieza de codigo demo cuando corresponda.
    - Nombrar comandos de validacion cuando existan.
    - Incluir checklist manual para permisos, privacidad y flujos de usuario afectados.
```

## Checklist Antes De Crear Un Cambio

- El cambio tiene un nombre concreto y accionable.
- El alcance puede cerrarse en una revision razonable.
- La capability afectada esta identificada.
- Hay criterios de aceptacion observables.
- Las tareas incluyen pruebas o validacion manual.
- Si toca datos personales, permisos, ubicacion, contacto o mensajes, se documenta el impacto de privacidad.
