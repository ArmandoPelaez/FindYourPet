## Context

`CreatePetPostScreen` actualmente muestra un unico campo de texto para `lastSeenLocation`. El proyecto ya tiene `DeviceLocationProvider`, permisos de ubicacion, `LocationSource`, coordenadas y persistencia de publicaciones. La nueva experiencia agregara un selector basado en Google Maps SDK for Android/Maps Compose, GPS iniciado por el usuario y referencia manual. Places y la busqueda de direcciones quedan fuera del alcance por decision del producto.

## Goals / Non-Goals

**Goals:**

- Reemplazar el campo de ubicacion por una entrada guiada con tres opciones: ubicacion actual, mapa y referencia manual.
- Permitir seleccionar coordenadas tocando el mapa y confirmar la seleccion antes de publicar.
- Reutilizar `DeviceLocationProvider` para la ubicacion actual y distinguir `DEVICE_GPS` de `MANUAL_COARSE`.
- Persistir el texto visible, latitud, longitud y fuente mediante la ruta existente de creacion de publicaciones.
- Intentar obtener una referencia legible con Android `Geocoder` despues de un toque en el mapa y tambien despues de capturar GPS. Si el servicio no esta disponible, falla o no devuelve resultados, conservar la etiqueta segura de la fuente y no abrir fallback manual automatico.
- Permitir que las reglas de Firestore guarden solo coordenadas numericas validas de la mascota perdida, sin aceptar coordenadas del propietario ni datos de contacto.
- Mantener Material 3 estable, tokens del Design System, Light/Dark Theme y estados de permiso, carga, vacio y error.
- Proteger la API key de Maps mediante `secrets.properties`/Secrets Gradle Plugin.

**Non-Goals:**

- No incorporar Places SDK, Autocomplete, geocodificacion ni busqueda de direcciones.
- No exponer coordenadas de personas: las reglas mantienen bloqueados los campos de contacto y `ownerLatitude`/`ownerLongitude`, y la UI publica no renderiza latitud/longitud precisas.
- No agregar geolocalizacion en segundo plano, tracking continuo, rutas, favoritos ni edicion de publicaciones existentes.
- No agregar el icono o etiqueta decorativa de “Ultima ubicacion” de la imagen de referencia.

## Decisions

### 1. Usar Google Maps Compose para el selector de mapa

Se usara Maps Compose sobre Maps SDK for Android y se mostrara un mapa dentro de un flujo Compose de seleccion. Un toque en el mapa movera un unico marcador y actualizara una seleccion pendiente; un boton de confirmacion devolvera `LatLng` y un texto seguro al formulario. El selector no persistira hasta que el usuario confirme y publique.

Alternativas descartadas: WebView o mapa externo, porque rompen la integracion Compose y dificultan devolver una seleccion consistente.

### 2. Mantener GPS y referencia manual como fuentes independientes

`Usar mi ubicacion actual` reutilizara el launcher de permisos y `DeviceLocationProvider`. `Elegir en el mapa` almacenara coordenadas confirmadas como `MANUAL_COARSE`, mientras `Escribir una referencia` limpiara coordenadas y conservara `MANUAL_COARSE`. Ninguna seleccion de mapa se presentara como captura GPS.

### 3. Mantener una seleccion normalizada y el contrato existente

La pantalla mantendra una seleccion con texto visible, latitud, longitud y `LocationSource`. El formulario continuara llamando a `createNewPetPost`; los mappers existentes seguiran escribiendo `lastSeenLocation`, coordenadas y `locationSource`.

### 4. Configurar credenciales de Maps de forma segura

La API key se cargara desde `secrets.properties` ignorado por Git y se inyectara en el manifest mediante `com.google.android.geo.API_KEY`. Se documentara activar Maps SDK for Android, restringir la key por aplicacion Android y API, y configurar las cuotas o condiciones de uso del proyecto. No se incluira ninguna key real en el repositorio.

### 5. Degradacion segura

Si la key, el mapa o la red no estan disponibles, la pantalla mostrara un error recuperable y mantendra `Escribir una referencia` como alternativa. Si se deniega ubicacion, no se capturaran coordenadas y el usuario podra usar mapa o referencia manual. La publicacion no se realizara con una ubicacion vacia.

### 6. Referencia automatica despues del toque

El selector intentara una geocodificacion inversa con Android `Geocoder` usando la API asyncrona disponible desde API 33 y una ejecucion fuera del hilo principal en versiones anteriores. El mismo helper se ejecutara para coordenadas GPS. El resultado se usara solo como etiqueta legible; las coordenadas seleccionadas seguiran siendo la fuente de ubicacion. Si no hay implementacion, la respuesta esta vacia o falla la consulta, se conservara la etiqueta segura existente (`Punto seleccionado en el mapa` o `Ubicacion actual capturada`) sin abrir fallback manual automatico.

## Risks / Trade-offs

- **[Costo o limite de Maps]** → Restringir la API key, configurar cuotas y registrar el requisito de Google Cloud antes de la validacion manual.
- **[API key expuesta en un APK]** → Restringir la key por package/SHA-1 y API; nunca versionar secretos.
- **[El punto de mapa no tiene direccion legible]** → Usar el texto seguro `Punto seleccionado en el mapa` y permitir que la persona elija una referencia manual si necesita mayor detalle.
- **[Permiso de ubicacion denegado]** → No bloquear el flujo completo; ofrecer mapa y referencia manual.
- **[Mapa no disponible]** → Mostrar error recuperable y conservar la referencia manual.

## Migration Plan

1. Eliminar dependencias, inicializacion y codigo de Places.
2. Mantener Maps SDK/Maps Compose, configuracion segura de API key y selector de mapa.
3. Integrar GPS, mapa y referencia manual en el formulario.
4. Actualizar pruebas, documentacion y validacion OpenSpec.

El rollback elimina el selector nuevo y las dependencias Maps; los campos persistidos existentes no requieren migracion destructiva.

## Open Questions

- La API key restringida y el proyecto de Google Cloud deben estar disponibles antes de validar el mapa en un dispositivo o emulador.
