## Context

FindYourPet ya tiene un proyecto Android Kotlin/Compose con Room, Firebase Auth/Firestore, permisos reales de camara/galeria/ubicacion en etapas previas y un conjunto creciente de tests locales. El release actual todavia no esta listo para publicacion controlada porque `release` no minifica, la firma depende de secretos externos sin una validacion de preparacion completa, no hay crash reporting integrado, la cobertura de flujos criticos es parcial y faltan evidencias de privacidad, permisos, accesibilidad y checklist Google Play.

Este cambio cruza cliente Android, configuracion Gradle, documentacion de release y monitoreo. No debe ampliar el acceso a datos sensibles ni introducir permisos nuevos salvo que exista un flujo de usuario real, validado y documentado.

## Goals / Non-Goals

**Goals:**

- Generar un build `release` firmado, minificado y reproducible para validacion controlada.
- Mantener los flujos principales funcionando bajo minificacion y reglas de ProGuard/R8.
- Agregar crash reporting para errores de produccion con trazas utiles y sin datos sensibles.
- Aumentar cobertura de tests de repositorio, ViewModel y flujos criticos antes de publicar.
- Documentar politica de privacidad, permisos Google Play, accesibilidad basica, evidencia de validacion y rollback.

**Non-Goals:**

- Publicar automaticamente en Google Play o configurar Play Console desde codigo.
- Reemplazar el proveedor backend, migrar datos productivos o cambiar reglas de negocio de publicaciones, chats o avistamientos.
- Agregar nuevos permisos Android no vinculados a flujos ya implementados.
- Prometer cifrado adicional, anonimato o privacidad no implementada tecnicamente.

## Decisions

### Android client

- Activar `isMinifyEnabled = true` en `release` y conservar `proguard-android-optimize.txt` con reglas locales en `app/proguard-rules.pro`.
  - Rationale: valida el binario real que se publicaria, reduce tamano y detecta problemas de reflection/serialization antes de usuarios reales.
  - Alternatives considered: dejar minificacion desactivada para simplificar el primer release. Se descarta porque oculta errores especificos de release y contradice el objetivo de publicacion controlada.
- Mantener la firma release basada en variables de entorno y documentar un preflight que falle con un mensaje accionable si falta keystore, alias o passwords.
  - Rationale: evita secretos en el repositorio y permite repetir el build en maquinas autorizadas.
  - Alternatives considered: usar el debug keystore para generar un APK instalable. Se descarta porque no representa un release ni sirve para una ruta segura de Play.
- Ejecutar pruebas locales y validaciones manuales sobre el build release/minificado para feed, detalle, crear publicacion, reportar avistamiento, chat/contacto, autenticacion, permisos, estados vacios y notificaciones.
  - Rationale: los bugs mas caros de release suelen aparecer por configuracion, shrinker o flujos no cubiertos.
- Publicar primero por Google Play Internal testing, con testers internos/controlados, antes de ampliar a Closed testing.
  - Rationale: valida instalacion desde Play, firma, permisos, Crashlytics, politica de privacidad y flujos principales sin exponer la app ampliamente.
  - Alternatives considered: Closed testing inicial o produccion abierta. Se posponen hasta que Internal testing no tenga blockers criticos.

### Backend and monitoring

- Usar el unico proyecto Firebase existente como proyecto definitivo para Crashlytics de produccion controlada.
  - Rationale: se integra con Firebase existente, soporta plugin Gradle, versiones de app y trazas de release; la documentacion actual contempla plugin `com.google.firebase.crashlytics`, dependencia Android y control de collection via `FirebaseCrashlytics`.
  - Alternatives considered: crear un proyecto staging separado o usar un proveedor externo independiente. Se pospone porque hoy solo existe un proyecto Firebase y agregar otra consola, credenciales y SDK no aporta al primer release controlado.
- Mantener Firebase limpio de datos historicos de prueba antes de validar release y crear solo cuentas/datos QA nuevos, identificables y descartables.
  - Rationale: evita mezclar contenido viejo de pruebas con datos reales o con evidencia de release.
  - Alternatives considered: conservar los datos de prueba anteriores para acelerar QA. Se descarta porque pueden confundir la validacion de estados vacios, privacidad, permisos y monitoreo.
- No registrar telefono, email, direccion, coordenadas exactas, URLs privadas de fotos, mensajes privados completos ni notas de avistamientos como custom keys, logs o mensajes de excepcion.
  - Rationale: el monitoreo debe diagnosticar errores sin convertirse en una nueva superficie de fuga de datos.
- Confirmar que versionCode/versionName quedan visibles en reportes para correlacionar crashes con builds controlados.

### Local storage and privacy documentation

- Mantener backup/data extraction excluyendo bases y archivos sensibles ya definidos por la etapa de privacidad.
  - Rationale: el release no debe relajar protecciones locales.
- Crear una politica de privacidad inicial en documentacion del repo, preparada para usarse en ficha Play, que describa datos recolectados, finalidad, permisos, almacenamiento, terceros, retencion y contacto.
  - Rationale: Google Play y usuarios necesitan una declaracion consistente con lo implementado.
- Alojar publicamente la politica de privacidad en Firebase Hosting del mismo proyecto Firebase, usando una URL accesible sin login antes de cargar la ficha de Google Play.
  - Rationale: aprovecha el proyecto Firebase existente, evita sumar otro proveedor y entrega una URL estable para Play Console y usuarios.
  - Alternatives considered: GitHub Pages, Google Docs publicado, Notion publico o web externa. Se posponen porque Firebase Hosting mantiene la politica junto al stack ya elegido y reduce pasos operativos para el primer release.
- Preparar un inventario de permisos desde `AndroidManifest.xml` y mapear cada permiso a flujo, pantalla, justificacion Play, datos afectados y evidencia de denegacion/grant.

## Risks / Trade-offs

- Minificacion rompe mappers, Room, Firebase o modelos remotos -> Mitigacion: ejecutar tests unitarios, build release y smoke test manual del binario minificado; agregar reglas ProGuard puntuales solo con evidencia.
- Crash reporting captura datos sensibles por logs o excepciones -> Mitigacion: prohibir datos sensibles en custom keys/logs, revisar mensajes de error y validar la politica de privacidad.
- Firma release falla en otra maquina -> Mitigacion: documentar variables requeridas, preflight y ubicacion esperada del keystore fuera del repo.
- Checklist Play queda incompleto por permisos o politica -> Mitigacion: inventario manifest, matriz de justificacion y evidencia manual por permiso.
- Mas tests aumentan tiempo de build -> Mitigacion: priorizar tests locales deterministicos y dejar instrumentados para validacion de permisos/UI cuando sea necesario.

## Migration Plan

1. Preparar configuracion Gradle release, ProGuard/R8 y firma sin modificar datos de usuario.
2. Integrar Crashlytics en el proyecto Firebase existente, usando cuentas/datos QA nuevos y controlados, y confirmar que reportes se agrupan por version.
3. Agregar tests y ajustar reglas de minificacion hasta que debug tests y release build pasen.
4. Ejecutar validacion manual de flujos criticos y permisos sobre un build release firmado.
5. Completar politica de privacidad, publicarla en Firebase Hosting, preparar checklist Play, evidencia de accesibilidad y plan de rollback.
6. Subir el build a Google Play Internal testing para testers internos/controlados.
7. Si Internal testing no tiene blockers criticos, preparar una etapa posterior de Closed testing; si aparece una regresion critica, revertir al ultimo build validado o publicar una version correctiva con versionCode superior y Crashlytics habilitado para diagnostico.

## Open Questions

None.
