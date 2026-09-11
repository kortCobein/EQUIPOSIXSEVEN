# Implementación US01–US12 y rediseño UX/UI UT

Esta versión refactoriza la aplicación integrada de Equipo SixSeven para cumplir de forma más completa las historias de usuario y facilitar su explicación en clase.

## Perfiles de prueba

La pantalla de acceso ya no pide nombre de usuario. Se selecciona un perfil y únicamente se escribe la contraseña común de demostración:

| Perfil | Usuario interno de Fake Store API | ID | Contraseña demo | Rol aplicado |
|---|---|---:|---|---|
| Administrador | `johnd` | 1 | `1234` | Administrador |
| Cliente | `donero` | 4 | `1234` | Cliente |
| Auditor | `kevinryan` | 3 | `1234` | Auditor |

La contraseña visible para los tres perfiles es **`1234`**. Las credenciales reales de Fake Store API permanecen encapsuladas internamente y únicamente se utilizan para obtener el token remoto. Después del login solo se persisten token, ID, username y rol.

## Cobertura funcional

- **US01:** selector de perfiles, contraseña común `1234`, POST `/auth/login`, validación de conexión, token y rol persistidos.
- **US02:** cierre de sesión elimina preferencias, rol/token en memoria y carrito local.
- **US03:** GET `/products`, estado loading/error/reintentar, lista eficiente e imágenes remotas.
- **US04:** GET `/products/categories` y GET `/products/category/{category}` con chips de filtro.
- **US05:** GET `/products/{id}`, detalle completo e interfaz condicionada por rol.
- **US06:** formulario de alta, validaciones y POST `/products`.
- **US07:** formulario precargado, PUT `/products/{id}` y actualización local.
- **US08:** confirmación obligatoria y DELETE `/products/{id}`.
- **US09:** cantidades, POST `/carts`, acumulación local sin duplicados y bloqueo para Auditor.
- **US10:** modificación y eliminación de artículos, PUT/DELETE de carrito y total en tiempo real.
- **US11:** GET `/users`, datos completos y acceso solo Administrador/Auditor.
- **US12:** GET `/carts`, expansión de productos y cruce de `productId` con el catálogo.

## Cambios de UX/UI

- Paleta UT: azul `#00245A` y verde `#009D81`.
- Navegación inferior adaptada a cada rol.
- Catálogo con búsqueda, imágenes y tarjetas.
- Estados de carga, error y vacío consistentes.
- Controles de carrito grandes y cómodos.
- Mensajes mediante Snackbar.
- Icono y Splash Screen propios.
- Todos los archivos Kotlin principales fueron renombrados al español y documentados con comentarios.

## Nota sobre Fake Store API

POST, PUT y DELETE se usan como simulaciones académicas. Fake Store API puede responder correctamente sin persistir los cambios de forma permanente; por eso la app refleja esos cambios también en su estado local durante la sesión.
