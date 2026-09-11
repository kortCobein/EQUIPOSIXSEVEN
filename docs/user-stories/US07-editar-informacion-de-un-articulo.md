# US07 — Editar información de un artículo

**Épica 3:** Inventario  
**Formato:** Historia de usuario en Markdown para consulta directa en GitHub.

## Historia de usuario

- **Como:** Usuario con rol de Administrador.
- **Quiero:** Modificar la información de un artículo existente consumiendo el endpoint /products/{id} mediante el método HTTP PUT
- **Para:** Actualizar detalles como el precio, título o descripción en el inventario del sistema.

## Criterios de aceptación

### Escenario 1: Actualización exitosa del producto.

- **Dado que:** El Administrador modifica campos en el formulario de edición con datos válidos.
- **Cuando:** Presiona el botón para guardar los cambios y la API responde correctamente.
- **Entonces:** El sistema muestra un mensaje de "Producto actualizado (Simulación)", cierra el formulario y refleja los cambios visualmente en la pantalla de detalle.

### Escenario 2: Validación de campos vacíos o incorrectos.

- **Dado:** Que el Administrador presiona el botón "Editar" en la vista de detalle de un producto.
- **Cuando:** Se abre la pantalla de edición.
- **Entonces:** Todos los campos del formulario (título, precio, descripción, categoría) aparecen pre-cargados con la información actual del artículo.

### Escenario 3: Restricción de permisos y seguridad.

- **Dado:** Un usuario activo con el rol de Cliente o Auditor.
- **Cuando:** Intenta interceptar la navegación o forzar una petición PUT hacia el servidor.
- **Entonces:** La aplicación bloquea la solicitud a nivel de red y deniega el acceso a la interfaz de edición.

## Notas adicionales / Reglas de negocio

- La Fake Store API procesará la petición PUT y devolverá un código 200 con el objeto actualizado en formato JSON, pero los cambios no afectarán la base de datos real del servidor.
- El botón de guardado debe deshabilitarse y mostrar un indicador de carga mientras la solicitud HTTP está en curso para evitar envíos duplicados.
- Solo se permite la modificación de datos si el precio mantiene un formato numérico y no hay campos de texto vacíos.

---
*Fuente: documento original `US07 - Editar informaci#U00f3n de un art#U00edculo - copia.docx` del paquete de User Stories.*
