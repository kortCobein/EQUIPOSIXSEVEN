package com.example.equiposixseven

enum class UserRole {
    CLIENTE,
    ADMINISTRADOR,
    AUDITOR
}

data class AppUser(
    val id: Int,
    val username: String,
    val password: String,
    val role: UserRole
)

data class Product(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val imageUrl: String,
    val category: String
)

data class CartItem(
    val productId: Int,
    val title: String,
    val price: Double,
    val quantity: Int
)

data class CartHistory(
    val id: Int,
    val username: String,
    val itemCount: Int,
    val total: Double
)

enum class AppSection {
    CATALOG,
    CART,
    ADMIN,
    AUDIT
}
