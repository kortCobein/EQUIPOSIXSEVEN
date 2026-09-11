# US08 — Eliminar producto del sistema

**Épica 3:** Inventario  
**Formato:** Historia de usuario en Markdown para consulta directa en GitHub.

## Historia de usuario

- **Como:** Usuario con rol de Administrador.
- **Quiero:** Consumir el endpoint /products/{id} mediante el método HTTP DELETE.
- **Para:** Remover de forma segura un artículo que ya no deba existir en el catálogo del sistema.

## Criterios de aceptación

### Escenario 1: Eliminación confirmada y exitosa.

- **Dado que:** El Administrador presiona el botón de eliminar y confirma la acción en el cuadro de diálogo.
- **Cuando:** La aplicación consume el método DELETE y recibe una respuesta positiva de la API.
- **Entonces:** La app muestra un mensaje flotante (Toast/Snackbar) de éxito y redirige al usuario de vuelta al catálogo general.

### Escenario 2: Cancelación del borrado.

- **Dado:** Que el Administrador presiona el botón "Eliminar" en la vista de detalle del producto.
- **Cuando:** El sistema muestra un cuadro de diálogo preguntando "¿Estás seguro de eliminar este producto?" y el usuario selecciona "Cancelar".
- **Entonces:** El cuadro de diálogo se cierra, no se realiza ninguna petición de red y la vista permanece inalterada.

### Escenario 3: Restricción de permisos y seguridad.

- **Dado:** Un usuario activo con el rol de Cliente o Auditor.
- **Cuando:** Interactúa con la aplicación o intenta forzar un método DELETE hacia el servidor.
- **Entonces:** La aplicación bloquea la solicitud a nivel de código y la interfaz nunca renderiza el botón de eliminación.

## Notas adicionales / Reglas de negocio

- Confirmación obligatoria: Nunca se debe disparar una petición destructiva (DELETE) directamente con un solo toque; es indispensable implementar un Alert Dialog nativo para confirmar la decisión.
- Comportamiento de la API: Al enviar la petición DELETE, la Fake Store API responderá devolviendo el objeto que supuestamente fue eliminado, pero en futuras peticiones GET el producto seguirá apareciendo. La aplicación debe manejar la respuesta correcta del servidor para considerar el flujo como exitoso.

---
*Fuente: documento original `US08 - Eliminar producto del sistema.docx` del paquete de User Stories.*
