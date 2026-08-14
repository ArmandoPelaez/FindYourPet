## Context

La navegación autenticada ya se concentra en `BottomPrimaryActionBanner` dentro de `CommonComponents.kt`. La acción central usa la variante circular existente, `Icons.Filled.Add`, la etiqueta `Publicar` y el destino `onCreatePostClick`; el formulario debe mostrar `Publicar ficha` como CTA propio. El cambio es visual y semántico, pero debe conservar el contrato de navegación y el tratamiento especial del elemento central.

## Goals / Non-Goals

**Goals:**

- Mostrar `Reportar` con `Icons.Filled.Pets`, que ya está disponible en el sistema de iconos usado por la app.
- Mantener el slot central, la jerarquía circular, la elevación, las dimensiones y los tokens de estados existentes.
- Mantener el destino de creación/reporte y los cuatro destinos secundarios sin cambios.
- Mantener `Publicar ficha` como CTA contextual del formulario, sin trasladarlo a la navegación.
- Cubrir la representación, accesibilidad, estados selected/unselected y los flujos de navegación con pruebas.

**Non-Goals:**

- No modificar navegación, ViewModels, validaciones, persistencia, backend ni lógica de publicación.
- No rediseñar la Bottom Navigation ni cambiar el orden, posición o comportamiento de los demás destinos.
- No agregar dependencias ni nuevos tokens visuales.

## Decisions

### Reutilizar el componente y los tokens existentes

La modificación se limita a la presentación de la acción central, al CTA del formulario y a las expectativas de sus pruebas. Se conserva `isCreateAction = true`, el slot central, `AppSpacing.bottomNavigationCreateActionSize`, `AppSpacing.bottomNavigationCreateIconSize`, `AppElevation.bottomNavigation` y los colores derivados del tema.

Alternativa descartada: crear una variante de navegación para `Reportar`; duplicaría el tratamiento visual y aumentaría el riesgo de divergencia entre Light/Dark y estados de interacción.

### Reutilizar `Icons.Filled.Pets`

`Icons.Filled.Pets` ya se utiliza en componentes de la app y satisface la semántica de huella/mascota sin incorporar recursos o dependencias nuevas. La etiqueta y el content description se alinearán con `Reportar` para que TalkBack no anuncie la acción como creación genérica.

Alternativa descartada: dibujar un icono personalizado o usar un asset nuevo; no es necesario y podría incumplir la consistencia del Design System.

### Conservar el destino existente y separar el CTA del formulario

El callback `onCreatePostClick` y el destino `ROUTE_CREATE` permanecen iguales. La acción central siempre se presenta como `Reportar`; no recibe ni reemplaza su contenido por `Publicar ficha`. El formulario renderiza `Publicar ficha` con la misma validación y callback de publicación. Las pruebas deben demostrar que Reportar sigue abriendo el formulario, que el CTA permanece dentro de él y que pulsar Inicio, Perfil, Mensajes o Alertas no altera sus destinos.

## Risks / Trade-offs

- [Riesgo] El label `Reportar` puede requerir más ancho que `Publicar` → mantener `maxLines`, overflow y tamaños tokenizados existentes; verificar teléfonos pequeños y tablets.
- [Riesgo] La acción contextual del formulario podría confundirse con la acción central → conservar el label `Publicar ficha` y su callback separado, y probar que ambos aparecen en responsabilidades distintas.
- [Riesgo] Cambiar solo el texto visible y no la semántica accesible → actualizar también `contentDescription` y cubrirlo con pruebas de Compose/estáticas.

## Migration Plan

No hay migración de datos ni de rutas. Cambiar la presentación en la rama del change, ejecutar validación OpenSpec, tests unitarios y build debug. El rollback consiste en restaurar `Icons.Filled.Add`, `Publicar` y el content description anterior, conservando el mismo callback.

## Open Questions

No quedan preguntas de alcance: Jira permite usar `pets` o equivalente y el repositorio ya dispone de `Icons.Filled.Pets`.
