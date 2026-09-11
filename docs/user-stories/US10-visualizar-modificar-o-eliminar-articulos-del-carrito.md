# US10 — Visualizar modificar o eliminar articulos del carrito

**Épica 4:** Compras  
**Formato:** Historia de usuario en Markdown para consulta directa en GitHub.

## Historia de usuario

- **Como:** Usuario con rol de Cliente.
- **Quiero:** Acceder a la vista de mi carrito para visualizar los productos, alterar sus cantidades o eliminarlos (simulando los métodos PUT y DELETE en /carts/{id}).
- **Para:** Administrar mi pedido exacto y conocer el monto total antes de concluir la compra.

## Criterios de aceptación

### Escenario 1: Modificación de cantidad y recálculo.

- **Dado que:** El Cliente se encuentra en la pantalla de su carrito de compras.
- **Cuando:** Incrementa o decrementa la cantidad de un producto específico mediante los controles de la interfaz.
- **Entonces:** El sistema actualiza la cantidad local, recalcula el total a pagar en tiempo real y ejecuta la petición PUT correspondiente.

### Escenario 2: Remoción del producto.

- **Dado:** Un artículo previamente agregado al carrito del Cliente.
- **Cuando:** El usuario reduce la cantidad a cero o presiona el ícono de eliminar.
- **Entonces:** La aplicación remueve el producto de la memoria local, ajusta el monto total a pagar y envía la petición DELETE a la API.

### Escenario 3: Interfaz de carrito vacío.

- **Dado:** Que el Cliente elimina el último producto de su lista.
- **Cuando:** El carrito se queda sin elementos guardados.
- **Entonces:** El sistema desactiva el botón de "Proceder al pago" y muestra un mensaje gráfico indicando "Tu carrito está vacío, explora el catálogo".

## Notas adicionales / Reglas de negocio

- Las operaciones matemáticas (multiplicar precio por cantidad y sumar el subtotal) deben ejecutarse iterando sobre el arreglo de datos local del dispositivo, ya que la Fake Store API no actualizará los montos reales remotamente.
- Los alumnos deben asegurar que los precios mostrados en la interfaz estén redondeados a dos decimales para mantener consistencia visual.

---
*Fuente: documento original `US10 - Visualizar modificar o eliminar articulos del carrito.docx` del paquete de User Stories.*
