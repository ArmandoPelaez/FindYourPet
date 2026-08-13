## Why

La ubicacion de una mascota perdida es uno de los datos mas importantes de la publicacion, pero el formulario actual depende de un unico campo de texto libre y no ofrece una seleccion guiada. SCRUM-13 busca reducir errores de ingreso y permitir que el usuario indique la ultima ubicacion vista mediante alternativas claras, preservando el dato ya mapeado y persistido.

## What Changes

- Reemplazar el ingreso aislado de ubicacion libre por una entrada guiada con el placeholder `Seleccionar ubicacion`.
- Ofrecer las opciones aprobadas: usar la ubicacion actual, elegir en el mapa o escribir una referencia.
- Intentar obtener automaticamente una referencia legible con Android `Geocoder` despues de seleccionar un punto en el mapa o capturar GPS. Si no hay resultado, conservar una etiqueta segura y no abrir fallback manual automatico.
- Mostrar la ubicacion seleccionada con nombre de zona/ciudad y referencia cuando esos datos esten disponibles.
- Usar la etiqueta `¿Donde fue vista por ultima vez?`, respetando los componentes y tokens existentes del Design System.
- Mantener el mapeo, la fuente de ubicacion, las coordenadas y la persistencia existentes; no exponer coordenadas precisas en la presentacion publica.
- Mantener el alcance limitado a la pantalla de publicacion, sus estados de ubicacion y las pruebas necesarias.
- No agregar la etiqueta ni el icono decorativo de “Ultima ubicacion” mostrados en la referencia.

## Capabilities

### New Capabilities

<!-- No se introduce una capability independiente; el cambio modifica contratos existentes. -->

### Modified Capabilities

- `pet-posts`: la creacion de publicaciones debe aceptar una ubicacion elegida mediante el nuevo flujo y conservar el contrato de persistencia existente.
- `location`: el flujo de publicacion debe distinguir la ubicacion actual de una ubicacion manual, permitir mapa y referencia manual, y mantener las reglas de proteccion de ubicacion precisa.

## Impact

- Afecta la pantalla Compose `CreatePetPostScreen` y los componentes de entrada de formulario relacionados.
- Puede afectar los estados de ubicacion y la integracion existente con `DeviceLocationProvider`, `LocationSource` y la ruta de creacion de publicaciones, sin cambiar contratos de repositorio salvo necesidad demostrada.
- Requiere actualizar pruebas de presentacion, validacion y mapeo/persistencia que cubran las nuevas fuentes de ubicacion.
- La captura de ubicacion actual requiere consentimiento y permisos existentes; el mapa usara Google Maps SDK for Android/Maps Compose con una API key restringida. No se incorpora Places ni busqueda de direcciones.
- El cambio tiene impacto de privacidad porque puede manejar coordenadas GPS: se conservara la distincion entre `DEVICE_GPS` y `MANUAL_COARSE`, evitando mostrar coordenadas precisas a otros usuarios.
- Rollback: revertir la pantalla y los estados nuevos conserva el modelo y los campos persistidos existentes, por lo que no requiere migracion destructiva.
