## Why

FindYourPet necesita una primera publicacion controlada que no dependa de configuracion insegura, validaciones manuales dispersas ni errores imposibles de diagnosticar. Este cambio prepara el paso de MVP funcional a release Android verificable, testeado, monitoreable y apto para revision de Google Play.

## What Changes

- Activar minificacion y reglas de shrink/obfuscation para builds `release`, manteniendo los flujos principales funcionales.
- Configurar la firma `release` para usar credenciales externas al repositorio y fallar de forma clara cuando falten secretos.
- Ampliar la cobertura de tests sobre repositorios, ViewModel y flujos criticos de publicacion, avistamiento, chat interno, autenticacion y estados vacios.
- Agregar una revision basica de accesibilidad para pantallas y controles principales.
- Integrar crash reporting para capturar errores de produccion sin exponer datos sensibles.
- Crear la politica de privacidad inicial y alinear sus declaraciones con datos, permisos, ubicacion, fotos, chats y notificaciones realmente implementados.
- Revisar permisos Android y preparar una justificacion observable para Google Play.
- Documentar evidencia de build release, pruebas, permisos, privacidad, accesibilidad, monitoreo y rollback antes de considerar el release listo.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `release-readiness`: agregar requisitos de build release firmado y minificado, cobertura de tests critica, accesibilidad basica, crash reporting, politica de privacidad, evidencia de validacion y estrategia de rollback.
- `device-permissions`: exigir inventario y justificacion de permisos para Google Play, verificando que cada permiso declarado corresponda a un flujo real y validado.
- `contact-privacy`: exigir que la politica de privacidad y el monitoreo describan chat interno como unico contacto mediado por la app y no contradigan las reglas sobre telefono, email, direccion, coordenadas, fotos, mensajes privados y notificaciones.

## Impact

- Codigo Android: `app/build.gradle.kts`, `app/proguard-rules.pro`, `app/src/main/AndroidManifest.xml`, recursos XML de backup/data extraction y codigo tocado por minificacion o crash reporting.
- Tests: suites locales en `app/src/test`, posibles pruebas instrumentadas en `app/src/androidTest` y validaciones manuales documentadas para flujos criticos.
- Documentacion: politica de privacidad, checklist Google Play, evidencia de validacion release y notas de rollback.
- Dependencias/sistemas: posible incorporacion de Firebase Crashlytics o proveedor equivalente, configuracion Gradle asociada y consola de monitoreo no productiva para validacion.
- Seguridad y privacidad: afecta datos personales, fotos, ubicacion, mensajes privados, notificaciones, permisos runtime y reportes de errores; aplican guardrails de no exponer datos sensibles, no pedir permisos sin flujo real, no prometer privacidad no implementada y no cerrar cambios de produccion sin build, tests y validacion documentada.
- Usuarios existentes: no debe cambiar contratos funcionales ni migrar datos de usuario; el riesgo principal es regresion por minificacion o configuracion release, mitigado con tests, build firmado y rollback a la version previa publicada o a un build release anterior validado.
