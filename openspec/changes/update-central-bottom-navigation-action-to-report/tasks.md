## 1. Actualizar la acción central

- [x] 1.1 Reemplazar en `BottomPrimaryActionBanner` el icono central `Icons.Filled.Add` por `Icons.Filled.Pets`, mantener `isCreateAction` y cambiar la etiqueta visible a `Reportar`.
- [x] 1.2 Actualizar el content description de la acción central para que la accesibilidad la anuncie como `Reportar`, sin cambiar el callback ni el destino `ROUTE_CREATE`.
- [x] 1.3 Confirmar que `Publicar ficha` continúa siendo el CTA independiente dentro del formulario y que no se modifica su callback, validación ni publicación.

## 2. Pruebas de presentación y navegación

- [x] 2.1 Actualizar las pruebas estáticas de `BottomPrimaryActionBanner` para verificar el orden `Inicio`, `Perfil`, `Reportar`, `Mensajes`, `Alertas`, el icono de mascota y la ausencia de la etiqueta `Publicar` en la navegación.
- [x] 2.2 Actualizar o agregar pruebas Compose para verificar `Reportar`, su content description, estados selected/unselected/disabled aplicables y la persistencia del CTA `Publicar ficha`.
- [x] 2.3 Agregar pruebas de navegación que recorran Reportar → Inicio, Perfil, Mensajes y Alertas, verificando que cada destino siga siendo independiente y que ningún callback redirija siempre al formulario.
- [x] 2.4 Verificar la representación en Light Theme y Dark Theme, incluyendo legibilidad, alineación, jerarquía circular y touch target sin valores visuales hardcodeados.

## 3. Validación

- [x] 3.1 Ejecutar `openspec validate "update-central-bottom-navigation-action-to-report" --strict`.
- [x] 3.2 Ejecutar `./gradlew.bat testDebugUnitTest` y corregir solo fallos dentro del alcance de este change.
- [x] 3.3 Ejecutar `./gradlew.bat assembleDebug`.
- [x] 3.4 Ejecutar la verificación manual del flujo autenticado: Inicio, Perfil, Reportar, Mensajes y Alertas, más `Publicar ficha` dentro del formulario.
