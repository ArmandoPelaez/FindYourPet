## Context

`CreatePetPostScreen` actualmente muestra el nombre de la mascota solo como placeholder y mantiene deshabilitado el botón si el nombre está vacío. La validación de dominio ya rechaza nombres vacíos, pero devuelve un texto diferente al solicitado por SCRUM-9. El cambio es local al formulario de publicación de mascotas perdidas y debe conservar el diseño existente documentado en `docs/design-system.md`.

## Goals / Non-Goals

**Goals:**

- Hacer visible el propósito del campo mediante una etiqueta `Nombre` y un indicador `*` de obligatoriedad.
- Mostrar `Campo obligatorio` al intentar publicar sin nombre.
- Mantener el contrato actual de `createNewPetPost`, la validación de producción y el flujo válido de publicación.
- Usar Material 3 estable, `MaterialTheme` y tokens existentes para color, tipografía y espaciado.
- Mantener la presentación correcta en Light Theme y Dark Theme.

**Non-Goals:**

- No cambiar `SightingAlertScreen`, backend, Firebase, Room, repositorios, permisos ni navegación.
- No agregar componentes visuales nuevos ni colores hardcodeados.
- No modificar reglas de negocio más allá de hacer observable la validación ya requerida para este campo.

## Decisions

1. **Modificar únicamente el campo de nombre del create-post.**
   - El campo existente en `CreatePetPostScreen` es el único que captura el nombre de una mascota perdida; el formulario de avistamiento recibe ese valor desde la publicación.
   - Alternativa descartada: agregar un nombre al formulario de avistamiento, porque duplicaría un dato de la publicación y ampliaría el alcance de SCRUM-9.

2. **Usar `label` para `Nombre` y un asterisco separado con el token primario existente.**
   - La etiqueta permanece visible cuando el campo tiene foco o contenido. El asterisco usa `MaterialTheme.colorScheme.primary`, que representa el coral/naranja de marca sin introducir un color nuevo.
   - Alternativa descartada: hardcodear un naranja o cambiar la paleta del tema.

3. **Conservar la validación de dominio y agregar una guarda visible en el submit.**
   - La pantalla debe poder procesar el intento de publicación cuando faltan datos textuales suficientes para mostrar `Campo obligatorio`; la guarda de nombre debe ejecutarse antes de llamar al ViewModel y detener el write.
   - La validación existente en `RealProductValidators.validatePost` se mantiene como defensa de dominio y compatibilidad para otros llamadores.
   - Alternativa descartada: depender solo del botón deshabilitado o solo del mensaje `Indica el nombre de la mascota.`, porque no cumple el criterio textual de Jira.

4. **Representar el estado de error con los mecanismos actuales del formulario.**
   - Reutilizar `formMessage` y el color de error del `MaterialTheme`; no crear un componente nuevo. El texto se limpia cuando el usuario corrige el nombre o cuando se inicia una nueva acción válida.

## Risks / Trade-offs

- [El botón puede quedar habilitado con el nombre vacío para permitir el intento requerido] → La guarda local no llama al ViewModel ni inicia un write; el mensaje identifica el campo y la cobertura verifica que no se publique.
- [El asterisco puede variar de contraste entre temas] → Usar el token primario del esquema Material y validar Light/Dark en tests/capturas existentes.
- [El mensaje de dominio y el mensaje visual podrían divergir] → Mantener la guarda de pantalla alineada con `Campo obligatorio` y conservar tests de `validatePost` para el contrato de dominio.

## Migration Plan

No hay migración de datos ni despliegue de backend. Implementar en la rama del change, ejecutar tests de validación/presentación y build debug. El rollback es revertir los cambios del formulario y sus pruebas.

## Open Questions

Ninguna para iniciar la implementación. El uso del token primario resuelve la referencia de Jira al color naranja sin modificar el Design System.
