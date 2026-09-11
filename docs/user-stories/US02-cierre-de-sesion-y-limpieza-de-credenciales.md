# US02 — Cierre de sesión y limpieza de credenciales

**Épica 1:** Autenticación  
**Formato:** Historia de usuario en Markdown para consulta directa en GitHub.

## Historia de usuario

- **Como:** Usuario con sesión activa (Administrador, Cliente o Auditor).
- **Quiero:** Presionar un botón para cerrar mi sesión desde el menú de navegación o perfil.
- **Para:** Proteger mi cuenta, eliminar mis credenciales del dispositivo y permitir que otro usuario ingrese.

## Criterios de aceptación

### Escenario 1: Cierre de sesión exitoso.

- **Dado que:** El usuario tiene una sesión válida y se encuentra en la pantalla principal.
- **Cuando:** Presiona la opción de "Cerrar sesión".
- **Entonces:** La aplicación elimina el token de acceso del almacenamiento local y redirige inmediatamente a la pantalla de Login.

### Escenario 2: Bloqueo de retroceso a vistas protegidas.

- **Dado:** Que el usuario cerró su sesión y se encuentra en la vista de Login.
- **Cuando:** Presiona el botón físico o gestual de "Atrás" en su teléfono.
- **Entonces:** La aplicación cierra el aplicativo o se mantiene en el Login, impidiendo regresar al catálogo sin autenticarse nuevamente.

### Escenario 3: Limpieza profunda de memoria.

- **Dado:** El proceso de finalización de sesión
- **Cuando:** La aplicación ejecuta el cierre.
- **Entonces:** Todo dato sensible guardado en memoria, como el identificador del usuario, el rol asignado (Admin/Cliente/Auditor) y el carrito local, se borra por completo.

## Notas adicionales / Reglas de negocio

- Limpieza de memoria persistente: Tienen que borrar el token y el rol directamente del almacenamiento del dispositivo (SharedPreferences en Android, UserDefaults en iOS, o equivalentes en Flutter/React Native), no solo limpiar las variables temporales del código.
- Destrucción del historial de pantallas: Al enviar al usuario de vuelta al Login, deben investigar cómo "matar" las pantallas anteriores. Si el usuario presiona "Atrás" en su celular y ve el catálogo de nuevo, la historia de usuario se considera fallida.
- Reinicio de estados: Si están usando gestores de estado globales para el carrito de compras, asegúrense de resetearlos a cero para que el siguiente usuario no vea los productos del usuario anterior.

---
*Fuente: documento original `US02 - Cierre de sesi#U00f3n y limpieza de credenciales.docx` del paquete de User Stories.*
