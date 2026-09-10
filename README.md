# Equipo Six Seven — integración US01 a US12

Proyecto Android en **Kotlin + Jetpack Compose** que integra en una sola aplicación las historias de usuario desarrolladas en el repositorio original `ErickRafael793/Equipo67`.

## Objetivo de esta integración

Las ramas originales fueron creadas con estructuras distintas; algunas son proyectos Android completos independientes y otras parten de versiones diferentes de la base. Por eso esta versión no hace un `merge` mecánico: conserva el comportamiento verificable de cada historia y lo organiza dentro de una sola aplicación.

## Historias integradas

- **US01:** Login y asignación local de perfiles.
- **US02:** Cierre de sesión y limpieza de credenciales de sesión.
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

## Perfiles de prueba

| Perfil | Usuario | Contraseña | Acceso principal |
|---|---|---|---|
| Cliente | `cliente` | `1234` | catálogo y carrito |
| Administrador | `admin` | `1234` | catálogo, carrito y administración de productos |
| Auditor | `auditor` | `1234` | catálogo y auditoría |

Los datos son locales para mantener la integración ejecutable y autocontenida. Las ramas US06-US08 originales incluían simulaciones/consumo de FakeStore API; aquí las operaciones CRUD comparten un único estado local para que las historias funcionen juntas sin depender de una API externa.

## Estructura

El código principal está en `app/src/main/java/com/example/equiposixseven/`:

- `AuthScreen.kt`: US01.
- `EquipoSixSevenApp.kt`: navegación y US02.
- `CatalogScreen.kt`: US03, US04, US05, US07, US08 y US09.
- `AdminScreen.kt`: US06.
- `CartScreen.kt`: US10.
- `AuditScreen.kt`: US11 y US12.
- `AppStore.kt`: datos compartidos para que las historias no queden aisladas.

## Trazabilidad

La procedencia de cada historia, rama y commit original está documentada en [`CONTRIBUTORS.md`](CONTRIBUTORS.md). La cuenta que hizo un upload en GitHub no se presenta automáticamente como autora intelectual del trabajo.

## Nota de integración

Esta rama integrada fue reconstruida a nivel de código porque no todas las ramas originales pueden fusionarse directamente. Por ejemplo, US08, US09 y US10 fueron publicadas con historiales independientes, mientras que la punta de `US3-4-5` ya no conserva el ZIP que había sido cargado previamente. Los detalles están en [`docs/INTEGRATION_NOTES.md`](docs/INTEGRATION_NOTES.md).
