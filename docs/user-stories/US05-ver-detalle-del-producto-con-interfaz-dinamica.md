# US05 — Ver detalle del producto con interfaz dinámica

**Épica 2:** Catálogo  
**Formato:** Historia de usuario en Markdown para consulta directa en GitHub.

## Historia de usuario

- **Como:** Usuario con rol de Administrador, Cliente o Auditor.
- **Quiero:** Seleccionar un producto del catálogo para consumir el endpoint /products/{id}
- **Para:** Visualizar la información completa del artículo y acceder a las opciones de gestión únicamente si cuento con los permisos necesarios.

## Criterios de aceptación

### Escenario 1: Visualización estándar para perfiles de consulta.

- **Dado que:** Un usuario con rol de Cliente o Auditor ingresa al detalle de un artículo específico.
- **Cuando:** La pantalla termina de cargar los datos del producto.
- **Entonces:** Se muestra la imagen, título, precio, descripción completa y categoría, sin mostrar controles de edición o eliminación.

### Escenario 2: Interfaz de gestión habilitada.

- **Dado:** Que un usuario con rol de Administrador accede a la vista de detalle.
- **Cuando:** Selecciona una categoría específica.
- **Entonces:** El sistema despliega botones adicionales y funcionales para "Editar" y "Eliminar" el artículo

### Escenario 3: Manejo de error de consulta.

- **Dado:** El intento de acceder a la vista detallada de un producto por cualquier perfil.
- **Cuando:** La solicitud a la API falla o indica que el producto no existe.
- **Entonces:** La aplicación muestra una alerta de "Producto no disponible" y retorna automáticamente a la pantalla del catálogo general.

## Notas adicionales / Reglas de negocio

- La lectura del perfil (Administrador, Cliente, Auditor) debe realizarse leyendo estrictamente la variable de sesión almacenada de forma local, no delegando la validación a la Fake Store API.
- Por seguridad y rendimiento de la vista, los componentes de edición/eliminación no deben instanciarse ocultos en la interfaz; deben excluirse de la jerarquía visual si el rol no es el adecuado.

---
*Fuente: documento original `US05 - Ver detalle del producto con interfaz din#U00e1mica.docx` del paquete de User Stories.*
