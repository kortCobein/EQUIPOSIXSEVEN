# US12 — Visualizar histórico de carritos globales

**Épica 5:** Auditorías  
**Formato:** Historia de usuario en Markdown para consulta directa en GitHub.

## Historia de usuario

- **Como:** Usuario con rol de Auditor (o Administrador).
- **Quiero:** Consumir el endpoint /carts global a través del método HTTP GET
- **Para:** Monitorear la actividad de compras de todos los clientes y auditar los carritos activos en el sistema.

## Criterios de aceptación

### Escenario 1: Listado general de operaciones.

- **Dado que:** El Auditor navega hacia el panel de monitoreo de carritos.
- **Cuando:** La aplicación obtiene la respuesta exitosa del servidor.
- **Entonces:** Se despliega una interfaz que enlista el ID del carrito, la fecha de creación y el ID del usuario propietario.

### Escenario 2: Bloqueo de ruta para perfiles no autorizados.

- **Dado:** Que un usuario con el rol de Cliente interactúa con la aplicación.
- **Cuando:** Intenta visualizar historiales de compra ajenos a su cuenta.
- **Entonces:** La aplicación oculta completamente esta sección del menú y restringe cualquier intento de acceso forzado.

### Escenario 3: Desglose de artículos por carrito.

- **Dado:** Que el Auditor visualiza la lista global de carritos.
- **Cuando:** Selecciona o expande un carrito en particular.
- **Entonces:** El sistema lee el arreglo interno products y muestra en pantalla los identificadores de los artículos contenidos junto con sus respectivas cantidades.

## Notas adicionales / Reglas de negocio

- Reto lógico (Cruce de datos): El endpoint /carts solo devuelve el productId. Para subir el nivel técnico de los alumnos, se les puede exigir que crucen este identificador con los datos del catálogo general (GET /products) para mostrar el título real del producto en lugar de solo su número de ID.
- Modo estricto de lectura: La interfaz no debe instanciar bajo ninguna circunstancia botones para alterar o eliminar el historial; es una pantalla puramente consultiva para cumplir con el perfil de Auditor.

---
*Fuente: documento original `US12 - Visualizar hist#U00f3rico de carritos globales.docx` del paquete de User Stories.*
