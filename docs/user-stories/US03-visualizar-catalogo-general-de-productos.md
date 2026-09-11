# US03 — Visualizar catálogo general de productos

**Épica 2:** Catálogo  
**Formato:** Historia de usuario en Markdown para consulta directa en GitHub.

## Historia de usuario

- **Como:** Usuario autenticado (Administrador, Cliente o Auditor).
- **Quiero:** Consumir el endpoint /products mediante el método GET.
- **Para:** Visualizar la lista completa de artículos disponibles en la pantalla de inicio.

## Criterios de aceptación

### Escenario 1: Renderizado correcto del catálogo.

- **Dado que:** El usuario ingresa a la pantalla principal tras iniciar sesión exitosamente
- **Cuando:** La aplicación descarga el arreglo de productos desde la API de forma correcta.
- **Entonces:** Se muestra una lista o cuadrícula donde cada elemento incluye obligatoriamente su imagen, título y precio.

### Escenario 2: Manejo del estado de carga (Loading).

- **Dado:** Que la aplicación lanza la petición de red hacia el servidor de Fake Store API.
- **Cuando:** La descarga de datos está en proceso y la respuesta aún no finaliza.
- **Entonces:** Se muestra un indicador visual de carga (como un spinner circular o un diseño tipo esqueleto) en el centro de la pantalla

### Escenario 3: Manejo de error de conexión.

- **Dado:** El intento de carga inicial del catálogo.
- **Cuando:** Ocurre un error de red (sin internet o caída del servidor).
- **Entonces:** La app detiene el indicador de carga y muestra un mensaje amigable en pantalla con un botón para "Reintentar" la petición.

## Notas adicionales / Reglas de negocio

- Rendimiento en listas: Implementar vistas reciclables (ej. RecyclerView en Android nativo, FlatList en React Native o ListView.builder en Flutter). No deben renderizar todos los elementos de golpe en un simple ScrollView para evitar problemas de memoria.
- Imágenes asíncronas: Las imágenes vienen como URLs (texto) en la respuesta JSON. Tienen que utilizar librerías nativas o de terceros (como Glide, Picasso, SDWebImage o las herramientas propias de su framework) para descargarlas en segundo plano (background thread) y no congelar la interfaz.
- Mapeo del JSON: Crear un modelo de datos (Clase, Struct o Data Class) que empate exactamente con la estructura de la API para facilitar el pase de parámetros a las vistas.

---
*Fuente: documento original `US03 - Visualizar cat#U00e1logo general de productos - copia.docx` del paquete de User Stories.*
