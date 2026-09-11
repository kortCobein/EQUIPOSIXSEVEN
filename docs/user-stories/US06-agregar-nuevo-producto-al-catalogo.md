# US06 — Agregar nuevo producto al catálogo

**Épica 3:** Inventario  
**Formato:** Historia de usuario en Markdown para consulta directa en GitHub.

## Historia de usuario

- **Como:** Usuario con rol de Administrador.
- **Quiero:** Llenar un formulario y consumir el endpoint /products mediante el método HTTP
- **Para:** Registrar un nuevo artículo en el inventario del sistema.

## Criterios de aceptación

### Escenario 1: Creación exitosa del producto.

- **Dado que:** El Administrador ingresa datos válidos en todos los campos del formulario (título, precio, descripción, URL de imagen y categoría).
- **Cuando:** Presiona el botón de guardar y se envía la petición a la API.
- **Entonces:** El sistema procesa la respuesta exitosa, muestra una alerta de confirmación con el nuevo ID generado y limpia el formulario.

### Escenario 2: Validación de campos vacíos o incorrectos.

- **Dado:** El formulario de creación de nuevo producto.
- **Cuando:** El Administrador intenta enviar los datos dejando campos obligatorios en blanco o ingresando letras en el campo de precio.
- **Entonces:** La aplicación detiene la petición de red y resalta en rojo los campos que requieren corrección

### Escenario 3: Manejo de error de consulta.

- **Dado:** Un usuario autenticado con rol de Cliente o Auditor.
- **Cuando:** Intenta navegar hacia la pantalla de creación de productos mediante un enlace profundo o alteración de estado.
- **Entonces:** El sistema bloquea el acceso y lo redirige inmediatamente a la pantalla del catálogo principal.

## Notas adicionales / Reglas de negocio

- Simulación de API: Es indispensable recordar que la Fake Store API responderá con el objeto creado y un nuevo ID, pero el producto no se guardará realmente en su servidor. La lista general (GET /products) no mostrará este nuevo artículo.
- Validaciones Frontend: La validación de los datos (precio numérico, URL con formato correcto) debe ocurrir estrictamente de manera local antes de ejecutar cualquier solicitud HTTP para optimizar recursos.

---
*Fuente: documento original `US06 - Agregar nuevo producto al cat#U00e1logo.docx` del paquete de User Stories.*
