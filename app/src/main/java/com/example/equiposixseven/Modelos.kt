package com.example.equiposixseven

/**
 * Roles disponibles en la aplicación.
 * El rol se obtiene a partir del ID del usuario de Fake Store API.
 */
enum class RolUsuario {
    CLIENTE,
    ADMINISTRADOR,
    AUDITOR
}

/**
 * Perfil de demostración mostrado en el selector de acceso.
 * El usuario no escribe el nombre de usuario ni conoce la clave real de Fake Store API.
 * La aplicación usa esos datos únicamente de forma interna para obtener el token remoto.
 */
data class PerfilDemo(
    val idUsuario: Int,
    val nombreVisible: String,
    val descripcion: String,
    val usuarioApi: String,
    val claveApi: String,
    val rol: RolUsuario
)

/** Sesión persistida localmente después de una autenticación correcta. */
data class UsuarioSesion(
    val id: Int,
    val usuario: String,
    val rol: RolUsuario,
    val token: String
)

/** Modelo de producto utilizado por catálogo, detalle, inventario y carrito. */
data class Producto(
    val id: Int,
    val titulo: String,
    val precio: Double,
    val descripcion: String,
    val imagenUrl: String,
    val categoria: String
)

/** Artículo almacenado en el carrito local de la sesión activa. */
data class ItemCarrito(
    val productoId: Int,
    val titulo: String,
    val precio: Double,
    val imagenUrl: String,
    val cantidad: Int
)

/** Estructura anidada de nombre devuelta por GET /users. */
data class NombreUsuario(
    val nombre: String,
    val apellido: String
)

/** Coordenadas incluidas en la dirección de un usuario remoto. */
data class Geolocalizacion(
    val latitud: String,
    val longitud: String
)

/** Dirección completa del usuario remoto. */
data class DireccionUsuario(
    val ciudad: String,
    val calle: String,
    val numero: Int,
    val codigoPostal: String,
    val geolocalizacion: Geolocalizacion
)

/** Usuario completo descargado desde Fake Store API para US11. */
data class UsuarioRemoto(
    val id: Int,
    val correo: String,
    val usuario: String,
    val nombre: NombreUsuario,
    val direccion: DireccionUsuario,
    val telefono: String
)

/** Producto y cantidad incluidos dentro de un carrito remoto. */
data class ProductoCarritoRemoto(
    val productoId: Int,
    val cantidad: Int
)

/** Carrito remoto descargado desde GET /carts para US12. */
data class CarritoRemoto(
    val id: Int,
    val usuarioId: Int,
    val fecha: String,
    val productos: List<ProductoCarritoRemoto>
)

/** Secciones de navegación disponibles dentro de la aplicación. */
enum class SeccionAplicacion {
    CATALOGO,
    CARRITO,
    INVENTARIO,
    AUDITORIA
}

/**
 * Excepción controlada para errores HTTP o de respuesta.
 * Permite mostrar mensajes entendibles sin exponer detalles técnicos al usuario final.
 */
class ExcepcionApi(message: String) : Exception(message)
