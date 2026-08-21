## Context

`CreatePetPostScreen` ya contiene el flujo completo de creación, validación y publicación. Actualmente presenta el título `Publicar mascota perdida` y un `AppButton` con `Publicar ficha` y `Icons.Filled.Publish`. SCRUM-50 solicita mejorar únicamente el lenguaje visible y la metáfora del CTA. El cambio debe respetar Compose + Material 3 estable, los tokens existentes y los temas Light/Dark.

## Goals / Non-Goals

**Goals:**

- Reemplazar el título por `Crea un aviso para ayudar a encontrarla`.
- Dar al nuevo título el mismo peso tipográfico que el texto de la superficie de carga de foto, sin introducir tamaños arbitrarios.
- Reemplazar la etiqueta y content description del CTA por `Publicar aviso`.
- Usar `Icons.Filled.Send` como representación Material estable del avión de papel.
- Mantener el mismo `AppButton`, estado disabled, indicador de progreso, callback `submitPost()` y validaciones.
- Actualizar las pruebas que verifican los contratos visibles del formulario.

**Non-Goals:**

- No modificar `PetViewModel`, repositorios, Firebase, modelos, navegación, ubicación, permisos ni persistencia.
- No cambiar el orden de campos, espaciado, colores, formas, dimensiones o comportamiento de desplazamiento.
- No introducir dependencias ni nuevos tokens visuales.

## Decisions

1. **Mantener el punto de edición en `CreatePetPostScreen`.** El título y el CTA son propiedad de esta pantalla; cambiar únicamente sus parámetros evita alterar la lógica de publicación.
   - Alternativa descartada: mover textos o estado a ViewModel, porque ampliaría el alcance y mezclaría presentación con lógica.

2. **Reutilizar tokens y componentes existentes.** El título usará el estilo tipográfico ya usado por `Toca para agregar una foto`/el componente de carga, y el botón seguirá siendo `AppButton` con sus tokens internos.
   - Alternativa descartada: declarar `fontSize`, `fontWeight`, `dp` o colores en la pantalla, porque contradice `docs/design-system.md`.

3. **Usar `Icons.Filled.Send`.** Es el ícono Material disponible que comunica un avión de papel y no requiere una librería adicional.
   - Alternativa descartada: crear un vector propio o incorporar otra dependencia para un ícono equivalente.

4. **Conservar el contrato de publicación.** Solo se cambiarán cadenas, content description e ícono; `onClick`, `enabled`, `isSubmitting`, `CircularProgressIndicator` y `submitPost()` permanecen sin cambios.
   - Alternativa descartada: renombrar símbolos de dominio o modificar callbacks, porque no aporta valor visual y aumenta el riesgo funcional.

## Risks / Trade-offs

- [Risk] Las pruebas estáticas o Compose existentes esperan los textos anteriores → Mitigation: actualizar únicamente las aserciones afectadas y agregar cobertura para el nuevo título, CTA, content description e ícono.
- [Risk] El texto largo puede perder legibilidad en pantallas compactas → Mitigation: conservar el ancho, padding y comportamiento de layout actuales; verificar visualmente en Light/Dark y en los tamaños de teléfono soportados.
- [Risk] `Icons.Filled.Send` no estuviera disponible en la versión actual de Material Icons → Mitigation: comprobar compilación; si la dependencia actual no lo expone, usar otro ícono Material estable ya disponible sin introducir dependencia nueva.

## Migration Plan

No hay migración de datos ni compatibilidad de backend. Implementar el cambio de presentación, actualizar pruebas y ejecutar validación OpenSpec, tests unitarios y `assembleDebug`. El rollback consiste en revertir el diff de la pantalla y de sus pruebas.

## Open Questions

Ninguna: Jira define los textos, el icono y el alcance exclusivamente visual.
