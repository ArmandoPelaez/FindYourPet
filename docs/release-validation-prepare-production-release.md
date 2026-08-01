# Validacion release: prepare-production-release

Fecha: 2026-07-31

## Decisiones cerradas

- Proyecto Firebase: se usa el unico proyecto Firebase existente (`findyourpet-db301`) para Crashlytics y validacion controlada.
- Datos de prueba historicos: eliminados. La validacion debe usar cuentas/datos QA nuevos, identificables y descartables.
- Primera publicacion: Google Play Internal testing.
- Politica de privacidad: se aloja en Firebase Hosting desde `public/privacy-policy.html`.

## Build

| Item | Estado | Evidencia |
| --- | --- | --- |
| Minificacion release | Configurada | `release.isMinifyEnabled = true` configurado; pendiente artifact firmado |
| Firma release | Bloqueada por secretos faltantes | `.\gradlew.bat :app:validateReleaseSigning` falla claro por `KEYSTORE_PATH file`, `STORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD` |
| Artifact release | Pendiente | Ejecutar `.\gradlew.bat assembleRelease` o `.\gradlew.bat bundleRelease` |
| versionCode/versionName | Pendiente de confirmar en artifact | `versionCode = 1`, `versionName = "1.0"` |

## Tests

| Comando | Estado | Notas |
| --- | --- | --- |
| `.\gradlew.bat testDebugUnitTest` | Pass | `BUILD SUCCESSFUL`, 2026-07-28 |
| `.\gradlew.bat :app:validateReleaseSigning` | Fail esperado | Falla claro por secretos release faltantes y guarda configuration cache |

## Crashlytics

| Item | Estado | Evidencia |
| --- | --- | --- |
| SDK y plugin | Pendiente de build | Configurado cuando existe `app/google-services.json` |
| Mapping release | Pendiente de build autorizado | Crashlytics Gradle plugin queda aplicado y debe procesar mapping de release con artifact firmado |
| Test crash controlado | Pendiente manual | Confirmar en consola Firebase con versionCode/versionName |
| Metadata sensible | En control automatizado | `CrashReporter` redacciona metadata sensible |

## Permisos y privacidad

- Inventario Play: `docs/google-play-permissions.md`.
- Borrador Data safety: `docs/google-play-data-safety-draft.md`.
- Politica Markdown: `docs/privacy-policy.md`.
- Politica publica: `public/privacy-policy.html`.
- Backup/data extraction: `allowBackup=false`, `backup_rules.xml` y `data_extraction_rules.xml` excluyen dominios sensibles.
- Contacto owner/reportero: solo chat interno; sin controles de divulgacion administrada ni telefono/email/direccion compartidos por la app.

## Accesibilidad

Pendiente validacion manual en build firmado:

- Navegacion principal.
- Botones de volver, notificaciones, chats, perfil y envio.
- Acciones de camara, galeria y ubicacion.
- Formularios de publicacion y avistamiento.
- Estados vacios y errores.

## Smoke test release

Pendiente en dispositivo/emulador:

- Autenticacion.
- Feed vacio y feed con datos QA nuevos.
- Crear publicacion.
- Reportar avistamiento.
- Chat interno owner/reportero.
- Ausencia de controles o valores de contacto personal administrados por la app.
- Notificaciones.
- Perfil y cierre de sesion.
- Permisos concedido, denegado, denegado permanente y no disponible.

## Rollback

Si Internal testing muestra crashes o regresiones criticas:

1. Detener promocion a Closed testing.
2. Revisar Crashlytics por versionCode/versionName.
3. Corregir y publicar un build con versionCode superior.
4. Si existe un build anterior validado en Play Console, mantenerlo como referencia de recuperacion.
