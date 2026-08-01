# Inventario de permisos para Google Play

Ultima actualizacion: 2026-07-31

Este inventario debe mantenerse alineado con `app/src/main/AndroidManifest.xml`, la politica de privacidad y la declaracion Data safety de Google Play.

En este documento, "permiso" refiere solo a permisos Android/runtime y controles de autorizacion backend. FindYourPet no administra permisos de divulgacion de telefono, email o direccion entre usuarios.

| Permiso | Flujo visible | Disparador del usuario | Datos accedidos | Justificacion Play | Evidencia requerida |
| --- | --- | --- | --- | --- | --- |
| `android.permission.INTERNET` | Inicio de sesion, publicaciones, avistamientos, chats, notificaciones, carga de fotos y Crashlytics | La app se abre o el usuario ejecuta flujos conectados | Trafico de red de Firebase, Cloudinary y servicios asociados | Necesario para operar backend, autenticacion, imagenes y diagnostico | Build release instalado y flujos conectados validados |
| `android.permission.CAMERA` | Tomar foto de mascota o avistamiento | Boton de camara en crear publicacion o reportar avistamiento | Imagen capturada por el usuario | Permite adjuntar evidencia visual creada por el usuario | Estados concedido, denegado, denegado permanente y no disponible |
| `android.permission.ACCESS_COARSE_LOCATION` | Usar ubicacion actual como referencia aproximada | Accion "usar mi ubicacion" en publicacion o avistamiento | Ubicacion aproximada del dispositivo | Ayuda a ubicar una zona de busqueda sin escritura manual | Estados concedido, denegado, denegado permanente y no disponible |
| `android.permission.ACCESS_FINE_LOCATION` | Usar ubicacion actual con mayor precision cuando el usuario lo autoriza | Accion "usar mi ubicacion" en publicacion o avistamiento | Coordenadas del dispositivo | Mejora la referencia de un avistamiento o publicacion cuando el usuario acepta | Estados concedido, denegado, denegado permanente y no disponible |

## Bloqueo de release

- Cualquier permiso nuevo debe tener un flujo real implementado, justificacion de Play, texto de razonamiento visible cuando aplique y evidencia de validacion.
- Si un permiso declarado no tiene flujo real o evidencia, el release queda bloqueado hasta removerlo o completar su implementacion.
- La politica de privacidad y Data safety deben declarar las mismas categorias de datos que este inventario.

## Pendiente antes de carga Play

- Completar evidencia manual en un build firmado de Internal testing.
- Confirmar que la URL publica de Firebase Hosting para la politica de privacidad este accesible sin login.
- Completar la declaracion Data safety en Play Console con las categorias de cuenta, contacto, fotos, ubicacion, mensajes, contenido de usuario y diagnostico tecnico.
