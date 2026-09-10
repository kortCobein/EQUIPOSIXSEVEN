package com.example.equiposixseven

import androidx.compose.runtime.mutableStateListOf

/**
 * Fuente local compartida por todas las historias integradas.
 * Evita que cada US mantenga su propio proyecto/datos aislados.
 */
object AppStore {

    // US01: perfiles locales para autenticar y asignar rol.
    val users = listOf(
        AppUser(1, "cliente", "1234", UserRole.CLIENTE),
        AppUser(2, "admin", "1234", UserRole.ADMINISTRADOR),
        AppUser(3, "auditor", "1234", UserRole.AUDITOR)
    )

    // US03: catálogo general compartido.
    val products = mutableStateListOf(
        Product(
            1,
            "Mochila Fjallraven",
            109.95,
            "Mochila resistente para uso diario.",
            "https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_.jpg",
            "Accesorios"
        ),
        Product(
            2,
            "Playera Casual",
            22.30,
            "Playera casual de manga corta.",
            "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879_.jpg",
            "Ropa"
        ),
        Product(
            3,
            "SSD 1 TB",
            109.00,
            "Unidad de estado sólido de 1 TB.",
            "https://fakestoreapi.com/img/61U7T1koQqL._AC_SX679_.jpg",
            "Electrónica"
        )
    )

    // US09 y US10: un único carrito personal para la sesión de demostración.
    val cart = mutableStateListOf<CartItem>()

    // US12: histórico global visible para el perfil auditor.
    val cartHistory = mutableStateListOf(
        CartHistory(1, "cliente01", 3, 164.55),
        CartHistory(2, "cliente02", 1, 109.00),
        CartHistory(3, "cliente03", 4, 241.20)
    )

    fun login(username: String, password: String): AppUser? {
        return users.firstOrNull {
            it.username.equals(username.trim(), ignoreCase = true) && it.password == password
        }
    }

    // US06: agrega un producto al catálogo local y genera un ID.
    fun addProduct(
        title: String,
        price: Double,
        description: String,
        imageUrl: String,
        category: String
    ): Product {
        val nextId = (products.maxOfOrNull { it.id } ?: 0) + 1
        val product = Product(nextId, title, price, description, imageUrl, category)
        products.add(product)
        return product
    }

    // US07: actualiza la información del artículo seleccionado.
    fun updateProduct(updated: Product) {
        val index = products.indexOfFirst { it.id == updated.id }
        if (index >= 0) products[index] = updated
    }

    // US08: elimina el producto y también lo retira del carrito si estaba agregado.
    fun deleteProduct(productId: Int) {
        products.removeAll { it.id == productId }
        cart.removeAll { it.productId == productId }
    }

    // US09: añade o acumula la cantidad de un artículo en el carrito.
    fun addToCart(product: Product, quantity: Int) {
        if (quantity <= 0) return
        val index = cart.indexOfFirst { it.productId == product.id }
        if (index >= 0) {
            val current = cart[index]
            cart[index] = current.copy(quantity = current.quantity + quantity)
        } else {
            cart.add(CartItem(product.id, product.title, product.price, quantity))
        }
    }

    // US10: modifica cantidad; cantidad cero elimina el artículo.
    fun changeCartQuantity(productId: Int, newQuantity: Int) {
        val index = cart.indexOfFirst { it.productId == productId }
        if (index < 0) return
        if (newQuantity <= 0) {
            cart.removeAt(index)
        } else {
            cart[index] = cart[index].copy(quantity = newQuantity)
        }
    }

    // US10: eliminación explícita de un artículo del carrito.
    fun removeFromCart(productId: Int) {
        cart.removeAll { it.productId == productId }
    }

    fun cartTotal(): Double = cart.sumOf { it.price * it.quantity }
}
