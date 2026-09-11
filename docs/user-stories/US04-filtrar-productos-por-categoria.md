# US04 — Filtrar productos por categoría

**Épica 2:** Catálogo  
**Formato:** Historia de usuario en Markdown para consulta directa en GitHub.

## Historia de usuario

- **Como:** Usuario autenticado.
- **Quiero:** Seleccionar una categoría específica desde la interfaz de usuario.
- **Para:** Visualizar únicamente los productos correspondientes a dicha clasificación.

## Criterios de aceptación

### Escenario 1: Obtención de categorías disponibles.

- **Dado que:** El usuario ingresa a la vista del catálogo
- **Cuando:** La interfaz principal se inicializa.
- **Entonces:** El sistema consume el método GET en el endpoint /products/categories y muestra los resultados devueltos como opciones de filtrado interactivo.

### Escenario 2: Aplicación del filtro.

- **Dado:** Que el usuario visualiza las opciones de categorías en pantalla.
- **Cuando:** Selecciona una categoría específica.
- **Entonces:** El sistema consume el método GET en /products/category/{category}, presenta un indicador de carga y renderiza exclusivamente los artículos de esa selección.

### Escenario 3: Remoción del filtro.

- **Dado:** Que el catálogo se encuentra actualmente filtrado.
- **Cuando:** El usuario desactiva el filtro activo o selecciona una opción de "Ver todos".
- **Entonces:** El sistema consume nuevamente el endpoint general /products y restablece la vista completa del catálogo.

## Notas adicionales / Reglas de negocio

- Diseño de interfaz: Las opciones de filtro por categoría deben implementarse mediante un componente visual accesible (como pestañas deslizables, chips o un menú desplegable) que no bloquee la visibilidad de los productos.
- Gestión de memoria: Al transicionar entre el catálogo general y una vista filtrada, el sistema debe limpiar el arreglo de datos anterior en la memoria local para prevenir que se muestren datos incorrectos mientras finaliza la nueva petición HTTP.
- Consistencia de carga: Cualquier cambio de categoría debe disparar el estado de carga visual (spinner/esqueleto) establecido en los estándares de la aplicación para notificar al usuario que la petición está en proceso.

---
*Fuente: documento original `US04 - Filtrar productos por categor#U00eda.docx` del paquete de User Stories.*
