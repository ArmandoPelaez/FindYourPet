## 1. Preparación y tokens

- [ ] 1.1 Revisar la implementación actual de `BottomPrimaryActionBanner`, `BottomNavigationItemContent` y `BottomNavigationTopDivider` contra la especificación `primary-navigation` del change.
- [ ] 1.2 Definir o ajustar en `DesignTokens.kt` tokens semánticos para el slot común, el círculo visible de `Reportar` (`40.dp`) y su touch target mínimo (`48.dp`), conservando `bannerHeight` (`60.dp`) y el icono (`22.dp`).

## 2. Estructura de navegación

- [ ] 2.1 Reestructurar `BottomNavigationItem` para que los cinco destinos usen el mismo contenedor interactivo y la misma composición vertical, sin clickables anidados.
- [ ] 2.2 Unificar `BottomNavigationItemContent` en un slot de icono compartido, gap y label común; conservar selección, colores semánticos, badges y callbacks existentes.
- [ ] 2.3 Renderizar `Reportar` dentro del slot común con círculo visible de `40.dp`, huella de `22.dp`, colores `primary`/`onPrimary` y área interactiva mínima de `48.dp`.
- [ ] 2.4 Eliminar del elemento `Reportar` el well de `60.dp`, `bottomNavigationActionLift`, offsets verticales, alineación especial del label y cualquier estructura tipo FAB.
- [ ] 2.5 Eliminar el arco central o notch de `BottomNavigationTopDivider` sin alterar la superficie general, altura, forma, elevación o espaciado horizontal de la barra.

## 3. Pruebas automatizadas

- [ ] 3.1 Actualizar `PrimaryNavigationShellStaticTest` o crear una prueba de presentación equivalente que compruebe los cinco labels/destinos, la composición compartida y la ausencia de `bottomNavigationActionLift` en `Reportar`.
- [ ] 3.2 Añadir aserciones para los tokens de `40.dp`, `22.dp`, `48.dp` y `60.dp`, evitando validar literales visuales dentro de los composables.
- [ ] 3.3 Verificar que los callbacks de Inicio, Perfil, Reportar, Actividad y Alertas mantengan sus rutas actuales y que no se modifique la lógica de negocio.

## 4. Validación

- [ ] 4.1 Ejecutar `openspec validate "unify-report-with-bottom-navigation-structure" --strict`.
- [ ] 4.2 Ejecutar `./gradlew.bat testDebugUnitTest --no-daemon --console=plain`.
- [ ] 4.3 Ejecutar `./gradlew.bat assembleDebug --no-daemon --console=plain`.
- [ ] 4.4 Ejecutar `git diff --check` y revisar que el diff solo afecte tokens, navegación inferior, pruebas y artefactos del change.
- [ ] 4.5 Ejecutar `openspec instructions apply --change "unify-report-with-bottom-navigation-structure" --json` y confirmar que no queden tareas pendientes.
- [ ] 4.6 Realizar revisión manual en Light/Dark y, si hay emulador disponible, comprobar barra de `60.dp`, alineación de los cinco iconos/labels, círculo de `40.dp`, touch target mínimo, ausencia de arco y ausencia de recorte en ventana estrecha.
