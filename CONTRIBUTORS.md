# Trazabilidad de historias de usuario

Este archivo registra **qué cuenta de GitHub realizó el upload/commit visible en el repositorio original**. Ese dato no demuestra por sí solo quién desarrolló intelectualmente cada historia. Cuando existe una responsabilidad declarada por el equipo, se anota por separado.

| US | Historia | Rama original | Cuenta que realizó el upload | Commit original | Responsable declarado / nota |
|---|---|---|---|---|---|
| US01 | Login y asignación local de perfiles | `US01-Login` | `D4N0-D0T` (autor de commit: `0zcrr777`) | `ff9e02169dc096987628d1fe738554ec4ea9242b` | No inferido |
| US02 | Cierre de sesión y limpieza de credenciales | `US02-Logout` | `D4N0-D0T` (autor de commit: `0zcrr777`) | `85e1061fb562f772438342cb241f5a67f6596ae5` | No inferido |
| US03 | Visualizar catálogo general de productos | `US3-4-5` | `D4N0-D0T` (autor de commit: `0zcrr777`) | `a1b22b50542c5e8a78392974c1924f66e7eff81d` | Rama compartida US03-US05 |
| US04 | Filtrar productos por categoría | `US3-4-5` | `D4N0-D0T` (autor de commit: `0zcrr777`) | `a1b22b50542c5e8a78392974c1924f66e7eff81d` | Rama compartida US03-US05 |
| US05 | Ver detalle del producto con interfaz dinámica | `US3-4-5` | `D4N0-D0T` (autor de commit: `0zcrr777`) | `a1b22b50542c5e8a78392974c1924f66e7eff81d` | Rama compartida US03-US05 |
| US06 | Agregar nuevo producto al catálogo | `US06---Agregar-nuevo-producto-al-catalogo` | `ErickRafael793` | `f9024412437abfee8354256041ab0ef3fd12d813` | No inferido |
| US07 | Editar información de un artículo | `US07-Editar-informacion-de-un-articulo-copia` | `ErickRafael793` | `cf7738e0b74b00847233870d06add137506fc1d4` | No inferido |
| US08 | Eliminar producto del sistema | `US08-Eliminar-producto-del-sistema` | `ErickRafael793` | `dbad2c155d5112308c7fdcca8e60656146608cfc` | Historial Git independiente |
| US09 | Añadir artículos al carrito personal | `US09-Anadir-articulos-al-carrito-personal` | `EduardoJosephadt-design` (autor de commit: `Joseph`) | `f96d6a35c7222998175fc8506006b98befb28230` | Historial Git independiente |
| US10 | Visualizar, modificar o eliminar artículos del carrito | `US10-Gestion-de-carrito-personal` | `EduardoJosephadt-design` (autor de commit: `Joseph`) | `22277f73adae184bf52acfc8f3f30ec8b8a6a40a` | Historial Git independiente |
| US11 | Listar todos los usuarios registrados | `kort` | `josephadtggiti25-netizen` (autor de commit: `JOSE3PHH_07`) | `872b980a5acc47151ef3a1c0850eed9879db93a0` | **Responsable declarado: Kort** |
| US12 | Visualizar histórico de carritos globales | `kort` | `josephadtggiti25-netizen` (autor de commit: `JOSE3PHH_07`) | `872b980a5acc47151ef3a1c0850eed9879db93a0` | **Responsable declarado: Kort** |

## Criterio usado

- **Cuenta que realizó el upload:** usuario asociado por GitHub al commit visible de la rama.
- **Autor de commit:** nombre guardado dentro del commit, cuando difiere del login de GitHub.
- **Responsable declarado:** solo se escribe cuando existe una declaración explícita; no se deduce por nombre de rama o cuenta.
- El repositorio integrado conserva estos SHA como referencia documental. Los commits nuevos de integración no intentan suplantar la identidad de los autores originales.
