## 1. Baseline y componentes compartidos

- [x] 1.1 Revisar `CreatePetPostScreen.kt`, `SightingAlertScreen.kt`, componentes Compose existentes y pruebas asociadas para identificar patrones visuales duplicados y conservar sus callbacks/estados actuales.
- [x] 1.2 Definir o extraer composables stateless compartidos para superficie multimedia, encabezado de sección y/o agrupación de campos usando exclusivamente tokens del Design System.
- [x] 1.3 Verificar que la extracción no modifique `PetViewModel`, repositorios, modelos, permisos, navegación ni contratos de media o ubicación.

## 2. Alinear la pantalla de publicación

- [x] 2.1 Mantener la jerarquía de `CreatePetPostScreen` como referencia y aplicar el patrón compartido sin alterar sus campos requeridos, validación de foto real, ubicación manual ni creación de publicaciones.
- [x] 2.2 Verificar que la pantalla conserve scroll, `imePadding`, `safeDrawing`, alturas, estados de carga/error y compatibilidad Light Theme/Dark Theme.
- [x] 2.3 Actualizar o agregar pruebas de presentación para foto, secciones, campos requeridos, acción de publicar y estabilidad responsive.

## 3. Alinear la pantalla de alerta

- [x] 3.1 Aplicar a `SightingAlertScreen` la jerarquía, espaciado, formas, superficies y feedback visual compartidos con la pantalla de publicación.
- [x] 3.2 Preservar el layout adaptativo, la foto opcional, ubicación manual/GPS, permisos existentes, notas, elegibilidad del reportante, idempotencia y barra de envío inferior.
- [x] 3.3 Mantener la semántica de alerta de la acción primaria, incluidos estados disabled, loading, success y error, sin modificar el fan-out backend.
- [x] 3.4 Actualizar o agregar pruebas Compose/estáticas de la alerta para verificar controles visibles, tags/layouts adaptativos y estados de envío.

## 4. Validación visual y de regresión

- [x] 4.1 Ejecutar `openspec validate "unify-lost-pet-post-and-sighting-alert-forms" --strict` y corregir cualquier incumplimiento de artefactos.
- [x] 4.2 Ejecutar `./gradlew.bat testDebugUnitTest` y confirmar que pasan las pruebas existentes y nuevas.
- [x] 4.3 Ejecutar `./gradlew.bat assembleDebug` y confirmar que la aplicación compila.
- [x] 4.4 Revisar manualmente ambos flujos en Light Theme y Dark Theme, con teclado, foto ausente/presente, error y tamaños compacto/expandido; registrar evidencia.
- [x] 4.5 Revisar el diff final contra este change, ejecutar `git diff --check` y confirmar que no hubo cambios en lógica de negocio, permisos, backend o persistencia.
