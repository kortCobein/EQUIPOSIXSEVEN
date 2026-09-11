# US11 — Listar todos los usuarios registrados

**Épica 5:** Auditorías  
**Formato:** Historia de usuario en Markdown para consulta directa en GitHub.

## Historia de usuario

- **Como:** Usuario con rol de Administrador o Auditor.
- **Quiero:** Consumir el endpoint /users mediante el método GET
- **Para:** Visualizar el directorio completo de cuentas registradas en el sistema y auditar su información principal.

## Criterios de aceptación

### Escenario 1: Renderizado de la lista de usuarios.

- **Dado que:** Un Administrador o Auditor ingresa a la sección de control de usuarios.
- **Cuando:** La aplicación descarga exitosamente los datos desde la API.
- **Entonces:** Se muestra una lista interactiva que incluye el nombre completo, correo electrónico, teléfono y nombre de usuario de cada cuenta.

### Escenario 2: Restricción de acceso estricta para Clientes.

- **Dado:** Que un usuario con el rol de Cliente se encuentra navegando en la aplicación.
- **Cuando:** Visualiza el menú principal o intenta forzar el acceso a rutas protegidas
- **Entonces:** El sistema omite renderizar el botón de "Usuarios" en la interfaz y bloquea cualquier intento de navegación mediante enlaces profundos.

### Escenario 3: Manejo de carga e interrupción de red.

- **Dado:** El inicio de la petición hacia el servidor de la Fake Store API.
- **Cuando:** La descarga de la lista se encuentra en curso o sufre una interrupción.
- **Entonces:** La interfaz despliega un indicador visual de carga y, en caso de fallo, emite una alerta permitiendo reintentar la conexión.

## Notas adicionales / Reglas de negocio

- La respuesta del endpoint /users incluye objetos anidados complejos, como la dirección con coordenadas geográficas y calles. Se requiere diseñar un modelo de datos estructurado que soporte esta anidación para prevenir caídas de la aplicación por referencias nulas al mapear el JSON.
- Esta historia abarca exclusivamente permisos de lectura. Las herramientas de edición, creación o eliminación de perfiles de usuario quedan fuera del alcance funcional de esta pantalla.

---
*Fuente: documento original `US11 - Listar todos los usuarios registrados.docx` del paquete de User Stories.*
