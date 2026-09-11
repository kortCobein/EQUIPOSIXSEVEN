package com.example.equiposixseven

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Estado compartido de la aplicación.
 * Centraliza catálogo, carrito y datos de auditoría para evitar estados duplicados entre pantallas.
 */
object AlmacenAplicacion {
    val productos = mutableStateListOf<Producto>()
    val carrito = mutableStateListOf<ItemCarrito>()
    val usuariosAuditoria = mutableStateListOf<UsuarioRemoto>()
    val carritosAuditoria = mutableStateListOf<CarritoRemoto>()

    var categorias by mutableStateOf<List<String>>(emptyList())
        private set

    var idCarritoRemoto by mutableStateOf<Int?>(null)
        private set

    /** Reemplaza el catálogo para evitar mezclar resultados de filtros diferentes. */
    fun reemplazarProductos(nuevos: List<Producto>) {
        productos.clear()
        productos.addAll(nuevos)
    }

    /** Guarda las categorías descargadas por US04. */
    fun reemplazarCategorias(nuevas: List<String>) {
        categorias = nuevas.distinct()
    }

    /** Inserta un producto creado localmente después de la simulación POST. */
    fun agregarProducto(producto: Producto) {
        productos.removeAll { it.id == producto.id }
        productos.add(0, producto)
    }

    /** Refleja inmediatamente en memoria el resultado de una edición PUT. */
    fun actualizarProducto(producto: Producto) {
        val indice = productos.indexOfFirst { it.id == producto.id }
        if (indice >= 0) productos[indice] = producto else productos.add(producto)
    }

    /** Elimina el producto y cualquier referencia local dentro del carrito. */
    fun eliminarProducto(productoId: Int) {
        productos.removeAll { it.id == productoId }
        carrito.removeAll { it.productoId == productoId }
    }

    /** Agrega un producto o acumula su cantidad si ya estaba en el carrito. */
    fun agregarAlCarrito(producto: Producto, cantidad: Int) {
        if (cantidad <= 0) return
        val indice = carrito.indexOfFirst { it.productoId == producto.id }

        if (indice >= 0) {
            val actual = carrito[indice]
            carrito[indice] = actual.copy(cantidad = actual.cantidad + cantidad)
        } else {
            carrito.add(
                ItemCarrito(
                    productoId = producto.id,
                    titulo = producto.titulo,
                    precio = producto.precio,
                    imagenUrl = producto.imagenUrl,
                    cantidad = cantidad
                )
            )
        }
    }

    /** Cambia una cantidad local; cero o menos remueve el artículo. */
    fun cambiarCantidad(productoId: Int, nuevaCantidad: Int) {
        val indice = carrito.indexOfFirst { it.productoId == productoId }
        if (indice < 0) return

        if (nuevaCantidad <= 0) {
            carrito.removeAt(indice)
        } else {
            carrito[indice] = carrito[indice].copy(cantidad = nuevaCantidad)
        }
    }

    /** Elimina un artículo concreto del carrito. */
    fun eliminarDelCarrito(productoId: Int) {
        carrito.removeAll { it.productoId == productoId }
    }

    /** Guarda el ID simulado devuelto por POST /carts para PUT/DELETE posteriores. */
    fun establecerIdCarritoRemoto(id: Int?) {
        idCarritoRemoto = id
    }

    /** Calcula el total del carrito recorriendo el estado local como exige US10. */
    fun totalCarrito(): Double = carrito.sumOf { it.precio * it.cantidad }

    /** Actualiza el directorio remoto usado por US11. */
    fun reemplazarUsuariosAuditoria(usuarios: List<UsuarioRemoto>) {
        usuariosAuditoria.clear()
        usuariosAuditoria.addAll(usuarios)
    }

    /** Actualiza el histórico remoto usado por US12. */
    fun reemplazarCarritosAuditoria(carritos: List<CarritoRemoto>) {
        carritosAuditoria.clear()
        carritosAuditoria.addAll(carritos)
    }

    /**
     * Limpieza profunda de datos ligados a una sesión.
     * Evita que el siguiente usuario vea el carrito o auditorías del usuario anterior.
     */
    fun limpiarDatosSesion() {
        carrito.clear()
        usuariosAuditoria.clear()
        carritosAuditoria.clear()
        idCarritoRemoto = null
    }
}
