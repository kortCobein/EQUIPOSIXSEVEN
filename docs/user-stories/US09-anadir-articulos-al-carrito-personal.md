# US09 — Añadir artículos al carrito personal

**Épica 4:** Compras  
**Formato:** Historia de usuario en Markdown para consulta directa en GitHub.

## Historia de usuario

- **Como:** Usuario con rol de Cliente.
- **Quiero:** Seleccionar la cantidad de un producto y presionar "Agregar al carrito" consumiendo el endpoint /carts mediante el método POST
- **Para:** Acumular los artículos que deseo adquirir antes de proceder a una simulación de pago.

## Criterios de aceptación

### Escenario 1: Agregado exitoso al carrito.

- **Dado que:** El Cliente se encuentra en la vista de detalle de un producto.
- **Cuando:** Selecciona una cantidad mayor a cero y presiona "Agregar al carrito".
- **Entonces:** La aplicación realiza la petición POST, guarda el artículo en la memoria local y muestra un mensaje confirmando que el producto fue añadido.

### Escenario 2: Producto previamente existente en el carrito.

- **Dado:** Que el Cliente ya tiene un producto específico guardado en su carrito actual.
- **Cuando:** Vuelve a presionar "Agregar al carrito" en el mismo artículo desde el catálogo
- **Entonces:** El sistema no crea un registro duplicado, sino que suma la nueva cantidad al registro existente

### Escenario 3: Restricción de interfaz por rol

- **Dado:** Un usuario autenticado con el perfil de Auditor.
- **Cuando:** Navega por la lista de productos o entra al detalle de un artículo.
- **Entonces:** La aplicación oculta completamente el botón de "Agregar al carrito", manteniendo su experiencia como solo lectura.

## Notas adicionales / Reglas de negocio

- Gestión de estado local: Aunque se consuma la Fake Store API (POST /carts) para practicar la petición de red, la API no guardará el carrito en el servidor. Los alumnos deberán implementar un manejador de estado (State Management, SQLite, o SharedPreferences) para mantener los artículos del carrito vivos en el dispositivo mientras la sesión del Cliente esté activa.
- El Administrador sí podría tener acceso a esta función si el modelo de negocio lo requiere, pero el caso de uso principal pertenece al Cliente.

---
*Fuente: documento original `US09 - A#U00f1adir art#U00edculos al carrito personal.docx` del paquete de User Stories.*
