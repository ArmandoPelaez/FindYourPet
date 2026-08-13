## Why

La pantalla de reporte de mascotas solicita información redundante: `Características` y `Señas particulares` se superponen con la descripción adicional usada para reconocer a la mascota. SCRUM-14 busca simplificar el flujo y reducir datos que el usuario debe completar, manteniendo una única descripción de reconocimiento.

## What Changes

- Retirar `Características` y `Señas particulares` de la UI de creación/reporte de mascotas perdidas.
- Eliminar su transporte por el flujo de creación y sus contratos de dominio, Room y Firestore cuando ya no sean necesarios.
- Mantener `Descripción adicional`/`features` como el campo de reconocimiento vigente.
- Actualizar pruebas, mappers y validaciones afectadas para reflejar el contrato simplificado.
- **BREAKING**: las nuevas publicaciones dejarán de producir o persistir los atributos `characteristics` y `particularMarks`; los datos históricos deberán conservar compatibilidad de lectura o migrarse según la decisión técnica documentada.

## Capabilities

### New Capabilities

Ninguna.

### Modified Capabilities

- `pet-posts`: simplificar el formulario de creación y retirar los atributos redundantes del contrato de publicación sin afectar los campos requeridos ni `features`.

## Impact

- UI Compose: `CreatePetPostScreen` y pruebas de presentación.
- Dominio y datos: `PetViewModel`, `PetPostEntity`, `PetPostDocument`, mappers y esquema/migraciones Room.
- Tests: pruebas estáticas, de mappers y de compatibilidad de datos históricos.
- No se agregan dependencias, permisos, cambios de autenticación ni cambios de backend fuera de los campos retirados.
- Rollback: restaurar la UI y los contratos mediante una nueva migración compatible; no se debe borrar información histórica sin una estrategia explícita.

