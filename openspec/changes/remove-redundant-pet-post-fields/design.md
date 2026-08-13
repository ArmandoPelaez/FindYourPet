## Context

SCRUM-14 retira dos entradas redundantes del formulario de creación de publicaciones. Actualmente `CreatePetPostScreen` mantiene estado y renderiza `Características` y `Señas particulares`; `PetViewModel.createNewPetPost` los recibe, `PetPostEntity` y `PetPostDocument` los modelan, `RemoteMappers` los serializa y Room los agregó mediante las migraciones 5→6 y 6→7. `Descripción adicional` continúa representada por `features`/`recognitionDetails` y no debe eliminarse.

El cambio cruza UI, ViewModel, contratos de datos, persistencia local/remota y pruebas. Debe preservar la creación autenticada, media, ubicación, validaciones existentes y la compatibilidad con datos históricos.

## Goals / Non-Goals

**Goals:**

- Renderizar solo `Descripción adicional` como campo de reconocimiento en el formulario.
- Eliminar `characteristics` y `particularMarks` de los contratos vigentes de creación, entidad local, documento remoto y mappers.
- Introducir una migración Room versionada que retire ambas columnas sin perder los demás datos de `pet_posts`.
- Mantener la lectura de publicaciones históricas sin fallar cuando Firestore contenga las claves antiguas; los valores legacy se ignorarán.
- Actualizar las pruebas para verificar ausencia de los campos y preservar `features`.
- Mantener tokens del Design System, Material 3 estable y soporte Light/Dark sin rediseño adicional.

**Non-Goals:**

- No eliminar `features`, `breed`, `color`, nombre, especie, ubicación, foto, recompensa ni otros campos no incluidos en SCRUM-14.
- No modificar reglas de autenticación, autorización, Firestore, notificaciones, chat o permisos.
- No ejecutar una limpieza destructiva masiva de claves antiguas en documentos Firestore; las claves legacy existentes quedan ignoradas por el cliente.
- No cambiar el flujo de reporte de avistamientos ni la presentación del home fuera de referencias que fallen por el retiro de los contratos.

## Decisions

### 1. Retirar los campos del formulario y de la firma de creación

Eliminar el estado local, las secciones Compose y los argumentos `characteristics`/`particularMarks` de `CreatePetPostScreen` y `PetViewModel.createNewPetPost`. La construcción del post seguirá enviando `features = recognitionDetails.ifBlank { ... }` con el mismo comportamiento actual.

**Alternativa descartada:** conservar los campos ocultos o enviarlos vacíos. Eso mantendría contratos y lógica que SCRUM-14 solicita retirar y permitiría que futuras escrituras sigan generando atributos obsoletos.

### 2. Retirar ambos atributos de los modelos y mappers vigentes

Eliminar las propiedades de `PetPostEntity` y `PetPostDocument`, y quitar las claves de `RemoteMappers.toDocument` y `toPetPostEntity`. Firestore tolera claves adicionales en documentos existentes; al mapear un documento legacy, las claves no modeladas se ignorarán mientras se conserven todos los campos vigentes.

**Alternativa descartada:** mantener propiedades deprecated con valores vacíos. Evitaría una limpieza real del contrato y conservaría caminos de persistencia no usados.

### 3. Migración Room 7→8 mediante reconstrucción de tabla

Incrementar la versión de `AppDatabase` a 8 y registrar `MIGRATION_7_8`. La migración creará una tabla temporal `pet_posts_new` con el esquema vigente sin `characteristics` ni `particularMarks`, copiará todas las columnas restantes, eliminará la tabla anterior y renombrará la temporal. Se conservarán las migraciones históricas 5→6 y 6→7 para que instalaciones antiguas puedan llegar a la versión 8.

**Alternativa descartada:** editar el esquema sin migración o bajar la versión. Room podría fallar al abrir bases existentes o perder datos; además, las instalaciones ya actualizadas requieren un camino explícito desde la versión 7.

### 4. Actualizar pruebas y compatibilidad legacy

Reemplazar las assertions que exigen ambos campos por assertions de ausencia en UI, firma, modelos y mappers. Agregar cobertura de la migración 7→8 y verificar que `features` permanece intacto. Mantener una prueba de mapeo de documento Firestore legacy con claves antiguas, confirmando que la lectura no falla y conserva los campos vigentes.

### 5. Mantener el alcance visual

No agregar colores, tamaños, paddings, radios ni componentes nuevos. La eliminación de las dos secciones debe dejar que el formulario use los mismos tokens y jerarquía existentes; la validación debe cubrir Light Theme y Dark Theme si el arnés disponible lo permite.

## Risks / Trade-offs

- [Risk] Las instalaciones locales en versión 7 contienen columnas retiradas. → Mitigation: migración 7→8 reconstructiva y prueba de preservación de todas las columnas vigentes.
- [Risk] Documentos Firestore legacy contienen claves que ya no existen en `PetPostDocument`. → Mitigation: deserialización basada en claves conocidas e ignorar extras; no borrar documentos remotamente.
- [Risk] Pruebas o fixtures construyen `PetPostEntity` con los campos eliminados. → Mitigation: localizar todos los usos, actualizar builders y cubrir el contrato simplificado antes de validar.
- [Risk] El espacio vertical del formulario cambia al quitar dos secciones. → Mitigation: revisar el flujo completo y ambos temas, sin compensar con valores hardcodeados ni rediseñar la pantalla.
- [Risk] Otro change paralelo modifica la presentación del home. → Mitigation: mantener los diffs separados y revisar el diff final solo contra este change; no tocar `remove-lost-pet-feed-cards`.

## Migration Plan

1. Implementar el contrato nuevo, la UI, la migración Room y las pruebas en la rama de este change.
2. Ejecutar la prueba de migración, tests unitarios, validación OpenSpec y build debug.
3. Verificar que las nuevas escrituras no incluyan las claves antiguas y que la lectura de documentos legacy siga funcionando.
4. Si la migración falla, detener la entrega y corregirla antes de integrar; rollback mediante la rama anterior o una migración posterior autorizada, sin borrar la base local del usuario.

## Open Questions

- Ninguna para generar los artefactos. La eliminación física de claves antiguas ya almacenadas en Firestore queda fuera de alcance por no estar especificada en SCRUM-14.

