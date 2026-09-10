# Notas de integración

## Estrategia

Se tomó la estructura moderna de la rama `Main` como referencia de configuración: Kotlin, Android y Jetpack Compose. Las funcionalidades se reconstruyeron dentro de un solo `applicationId` (`com.example.equiposixseven`) y comparten modelos y estado local.

No se copiaron doce proyectos Android completos dentro del repositorio final. Hacerlo habría dejado múltiples `MainActivity`, manifiestos, temas, paquetes y versiones de Gradle incompatibles entre sí.

## Decisiones por historia

### US01 y US02
Las dos ramas contienen la misma `MainActivity` de login en su punta: captura usuario/contraseña, pero no implementa una sesión completa ni un logout funcional. La integración conserva el login solicitado y completa el cierre de sesión de US02 con estado local, eliminando la sesión activa al volver a la pantalla de acceso.

### US03, US04 y US05
La rama `US3-4-5` tuvo un `LoginApp.zip` en un commit intermedio y el commit posterior lo eliminó. La punta actual no contiene implementación verificable del catálogo. Por ello la integración implementa estas tres historias a partir de sus títulos: listado general, filtro por categoría y detalle del producto, sin atribuir código inexistente a la rama.

### US06
La rama original usa Compose + Retrofit/FakeStore API e incluye validación de título, precio, descripción, URL de imagen y categoría. La integración conserva esos campos, las validaciones principales y la generación de ID, pero escribe en el repositorio local compartido para que US03-US10 operen sobre el mismo catálogo.

### US07
Se conserva la regla verificable de que solo el rol `ADMINISTRADOR` puede editar, la precarga de datos del producto, la validación de campos y el mensaje de actualización simulada.

### US08
Se conserva la restricción a administrador y el diálogo de confirmación antes de eliminar. Esta rama no comparte ancestro Git con `Main`, por lo que no era apta para un merge convencional.

### US09
Se conserva la regla verificable `AUDITOR` = sin permiso para añadir al carrito y la selección de cantidad positiva.

### US10
Se conserva la visualización del carrito, modificación de cantidades, eliminación de artículos, estado vacío y cálculo del total. Una cantidad que baja a cero elimina el artículo, igual que en el repositorio original.

### US11 y US12
Se integran como pantalla de auditoría: listado de usuarios registrados e histórico global de carritos. Estas historias fueron declaradas por Kort como su responsabilidad; el upload visible de la rama `kort` fue realizado desde otra cuenta, dato que se conserva separadamente en `CONTRIBUTORS.md`.

## Alcance

Esta integración busca que las 12 historias puedan demostrarse dentro de una sola app. No añade backend, persistencia, registro real de usuarios, pagos, checkout, analítica, permisos adicionales ni funcionalidades que no sean necesarias para representar las historias existentes.
