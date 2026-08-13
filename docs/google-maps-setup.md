# Google Maps para FindYourPet

La app usa versiones fijas y compatibles de Maps SDK for Android y Maps Compose 6.12.0. La clave no se guarda en el repositorio.

## Configuracion local

1. Crear `secrets.properties` en la raiz del proyecto.
2. Agregar `MAPS_API_KEY=...` con una clave del proyecto de Google Cloud.
3. Mantener la clave fuera de Git; el archivo esta incluido en `.gitignore`.
4. Para builds sin credenciales se usa `local.defaults.properties` con `DEFAULT_API_KEY`; el mapa queda deshabilitado y la referencia manual sigue disponible.

En Google Cloud se debe habilitar:

- Maps SDK for Android.
- Billing del proyecto.
- Restriccion de aplicacion Android usando `com.findyourpet.app` y los SHA-1 de los builds autorizados.
- Restriccion de API exclusivamente a Maps SDK for Android.
- Cuotas y alertas de consumo para el proyecto.

La app no inicializa clientes de busqueda de direcciones ni usa Autocomplete. El usuario puede elegir un punto tocando el mapa y debe confirmarlo antes de volver al formulario. Si la clave, la red o Google Play Services no estan disponibles, se conserva `Escribir una referencia` como alternativa.

Despues de tocar el mapa, la app intenta obtener una etiqueta legible mediante Android `Geocoder`. La misma resolucion se intenta despues de capturar GPS. Si no hay implementacion, la consulta falla o no devuelve una direccion util, se conserva la etiqueta segura de la fuente y no se abre un fallback manual automatico. Esto no incorpora Places ni un servicio adicional de busqueda.

El manifest declara opcionalmente `org.apache.http.legacy` para compatibilidad con el renderer legacy de Maps que puede entregar Google Play Services en algunos emuladores. Sin esta declaracion, esos dispositivos pueden cerrar el proceso con `NoClassDefFoundError: org/apache/http/ProtocolVersion` al abrir el mapa.

## Validacion manual pendiente

La validacion del mapa requiere un dispositivo o emulador con Google Play Services y una clave restringida real con billing configurado. Sin esos prerequisitos se puede validar el fallback manual, la denegacion de permisos y los estados de formulario, pero no una respuesta real de Google Maps.
