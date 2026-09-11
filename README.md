<div align="center">

<img src="docs/banner_equipo67.svg" alt="Equipo 67 - Desarrollo de Aplicaciones Móviles" width="100%" style="max-width: 1024px; border-radius: 14px; box-shadow: 0 8px 32px rgba(0, 203, 248, 0.25);">

</div>

# Equipo Six Seven — US01 a US12 · versión UT

Aplicación Android desarrollada en **Kotlin + Jetpack Compose** que integra las historias de usuario US01–US12 en una sola experiencia. La versión actual completa los flujos que antes estaban simulados únicamente en memoria y aplica un rediseño visual inspirado en la identidad de la **Universidad Tecnológica de San Juan del Río**.

## Qué cambió

- Consumo real de **Fake Store API** para autenticación, catálogo, categorías, detalle, productos, usuarios y carritos.
- Estados de **carga, error, reintento y vacío** en las pantallas que dependen de red.
- Imágenes remotas del catálogo mediante Coil.
- Sesión persistente y limpieza completa al cerrar sesión.
- Navegación y acciones distintas según el rol.
- Catálogo con búsqueda, filtros y tarjetas visuales.
- Carrito con controles de cantidad, subtotal y total en tiempo real.
- Panel de auditoría con usuarios y carritos expandibles.
- **Icono propio y Splash Screen**.
- Paleta UT: azul `#00245A` y verde `#009D81`.
- Archivos Kotlin renombrados al español y comentados para facilitar su explicación.

## Perfiles de prueba

El login ya no pide nombre de usuario. Selecciona el perfil y escribe únicamente la contraseña común de demostración:

| Perfil | Usuario interno | ID | Contraseña demo | Acceso |
|---|---|---:|---|---|
| Administrador | `johnd` | 1 | `1234` | catálogo, carrito, inventario y auditoría |
| Cliente | `donero` | 4 | `1234` | catálogo y carrito |
| Auditor | `kevinryan` | 3 | `1234` | catálogo y auditoría en solo lectura |

> Para los tres perfiles la contraseña visible es **`1234`**. Las credenciales reales de Fake Store API se mantienen encapsuladas dentro de la aplicación únicamente para obtener el token remoto; el usuario nunca tiene que escribirlas. La app no guarda la contraseña de demostración y persiste solamente token, ID, username y rol.

## Historias implementadas

- **US01:** Login y asignación local de perfiles.
- **US02:** Cierre de sesión y limpieza de credenciales.
- **US03:** Visualizar catálogo general de productos.
- **US04:** Filtrar productos por categoría.
- **US05:** Ver detalle del producto con interfaz dinámica.
- **US06:** Agregar nuevo producto al catálogo.
- **US07:** Editar información de un artículo.
- **US08:** Eliminar producto del sistema.
- **US09:** Añadir artículos al carrito personal.
- **US10:** Visualizar, modificar o eliminar artículos del carrito.
- **US11:** Listar todos los usuarios registrados.
- **US12:** Visualizar histórico de carritos globales.

La documentación detallada de cada historia está en [`docs/user-stories/`](docs/user-stories/).

## Código principal

Ruta: `app/src/main/java/com/example/equiposixseven/`

- `ActividadPrincipal.kt`: Activity, tema y Splash Screen.
- `AplicacionSixSeven.kt`: sesión, navegación y cierre de sesión.
- `PantallaAcceso.kt`: selector de perfiles y autenticación.
- `PantallaCatalogo.kt`: catálogo, búsqueda y filtros.
- `PantallaDetalleProducto.kt`: detalle, carrito, edición y eliminación.
- `PantallaInventario.kt`: alta y gestión de productos.
- `PantallaCarrito.kt`: administración del carrito.
- `PantallaAuditoria.kt`: usuarios e histórico global de carritos.
- `ServicioApi.kt`: comunicación HTTP con Fake Store API.
- `GestorSesion.kt`: persistencia y limpieza de sesión.
- `AlmacenAplicacion.kt`: estado compartido de la app.
- `Modelos.kt`: modelos de dominio y API.
- `TemaUT.kt` / `ComponentesUT.kt`: identidad visual y componentes reutilizables.

## Documentación técnica

- [Implementación US01–US12 y UX/UI UT](docs/IMPLEMENTACION_US_Y_UX.md)
- [Historias de usuario en Markdown](docs/user-stories/README.md)
- [Trazabilidad de contribuciones](CONTRIBUTORS.md)
- [Notas de la integración original](docs/INTEGRATION_NOTES.md)

## Nota sobre Fake Store API

Fake Store API está diseñada para práctica. Los endpoints `POST`, `PUT` y `DELETE` pueden responder correctamente sin persistir los cambios de manera permanente en el servidor. Por eso la app sincroniza la petición y también refleja la operación en el estado local durante la sesión.
