package com.example.equiposixseven

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Locale

/**
 * US03 - Visualizar catálogo general.
 * US04 - Filtrar productos por categoría.
 */
@Composable
fun CatalogScreen(
    onOpenProduct: (Int) -> Unit,
    userRole: UserRole
) {
    var selectedCategory by remember { mutableStateOf("Todas") }
    val categories = listOf("Todas") + AppStore.products.map { it.category }.distinct()
    val visibleProducts = if (selectedCategory == "Todas") {
        AppStore.products
    } else {
        AppStore.products.filter { it.category == selectedCategory }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Catálogo", style = MaterialTheme.typography.headlineSmall)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                if (category == selectedCategory) {
                    Button(onClick = { selectedCategory = category }) {
                        Text(category)
                    }
                } else {
                    OutlinedButton(onClick = { selectedCategory = category }) {
                        Text(category)
                    }
                }
            }
        }

        if (visibleProducts.isEmpty()) {
            Text("No hay productos en esta categoría.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(visibleProducts, key = { it.id }) { product ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(product.title, style = MaterialTheme.typography.titleMedium)
                            Text("$${String.format(Locale.US, "%.2f", product.price)}")
                            Text(product.category)
                            Button(
                                onClick = { onOpenProduct(product.id) },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("Ver detalle")
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * US05 - Detalle dinámico del producto.
 * US07 - Edición para administrador.
 * US08 - Eliminación con confirmación para administrador.
 * US09 - Agregar artículos al carrito; auditor no puede agregar.
 */
@Composable
fun ProductDetailScreen(
    productId: Int,
    userRole: UserRole,
    onBack: () -> Unit
) {
    val product = AppStore.products.firstOrNull { it.id == productId }

    if (product == null) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("El producto ya no existe.")
            Button(onClick = onBack, modifier = Modifier.padding(top = 8.dp)) {
                Text("Volver")
            }
        }
        return
    }

    var quantity by remember(product.id) { mutableIntStateOf(1) }
    var message by remember(product.id) { mutableStateOf<String?>(null) }
    var editing by remember(product.id) { mutableStateOf(false) }
    var showDeleteDialog by remember(product.id) { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        OutlinedButton(onClick = onBack) { Text("Volver") }
        Text(
            product.title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text("Precio: $${String.format(Locale.US, "%.2f", product.price)}")
        Text("Categoría: ${product.category}")
        Text(product.description, modifier = Modifier.padding(top = 8.dp))
        Text("Imagen: ${product.imageUrl}", modifier = Modifier.padding(top = 8.dp))

        if (userRole != UserRole.AUDITOR) {
            Row(
                modifier = Modifier.padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = { if (quantity > 1) quantity-- }) {
                    Text("-")
                }
                Text("Cantidad: $quantity", modifier = Modifier.padding(top = 12.dp))
                OutlinedButton(onClick = { quantity++ }) {
                    Text("+")
                }
            }

            Button(
                onClick = {
                    AppStore.addToCart(product, quantity)
                    message = "Producto añadido al carrito"
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Añadir al carrito")
            }
        }

        if (userRole == UserRole.ADMINISTRADOR) {
            Row(
                modifier = Modifier.padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = { editing = !editing }) {
                    Text(if (editing) "Cancelar edición" else "Editar")
                }
                OutlinedButton(onClick = { showDeleteDialog = true }) {
                    Text("Eliminar")
                }
            }
        }

        message?.let {
            Text(it, modifier = Modifier.padding(top = 12.dp))
        }

        if (editing && userRole == UserRole.ADMINISTRADOR) {
            EditProductForm(
                product = product,
                onSaved = {
                    editing = false
                    message = "Producto actualizado (Simulación)"
                }
            )
        }
    }

    if (showDeleteDialog && userRole == UserRole.ADMINISTRADOR) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar producto") },
            text = { Text("¿Deseas eliminar este producto del catálogo?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        AppStore.deleteProduct(product.id)
                        showDeleteDialog = false
                        onBack()
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun EditProductForm(product: Product, onSaved: () -> Unit) {
    var title by remember(product.id) { mutableStateOf(product.title) }
    var price by remember(product.id) { mutableStateOf(product.price.toString()) }
    var description by remember(product.id) { mutableStateOf(product.description) }
    var imageUrl by remember(product.id) { mutableStateOf(product.imageUrl) }
    var category by remember(product.id) { mutableStateOf(product.category) }
    var error by remember(product.id) { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(top = 16.dp)) {
        Text("Editar producto", style = MaterialTheme.typography.titleLarge)
        ProductFields(
            title = title,
            onTitle = { title = it },
            price = price,
            onPrice = { price = it },
            description = description,
            onDescription = { description = it },
            imageUrl = imageUrl,
            onImageUrl = { imageUrl = it },
            category = category,
            onCategory = { category = it }
        )

        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Button(
            onClick = {
                val parsedPrice = price.toDoubleOrNull()
                error = when {
                    title.isBlank() -> "El título no puede estar vacío"
                    parsedPrice == null -> "El precio debe ser un número válido"
                    parsedPrice < 0 -> "El precio no puede ser negativo"
                    description.isBlank() -> "La descripción no puede estar vacía"
                    imageUrl.isBlank() -> "La URL de la imagen no puede estar vacía"
                    category.isBlank() -> "La categoría no puede estar vacía"
                    else -> null
                }

                if (error == null) {
                    AppStore.updateProduct(
                        product.copy(
                            title = title.trim(),
                            price = parsedPrice!!,
                            description = description.trim(),
                            imageUrl = imageUrl.trim(),
                            category = category.trim()
                        )
                    )
                    onSaved()
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Guardar cambios")
        }
    }
}

@Composable
fun ProductFields(
    title: String,
    onTitle: (String) -> Unit,
    price: String,
    onPrice: (String) -> Unit,
    description: String,
    onDescription: (String) -> Unit,
    imageUrl: String,
    onImageUrl: (String) -> Unit,
    category: String,
    onCategory: (String) -> Unit
) {
    OutlinedTextField(title, onTitle, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
    OutlinedTextField(price, onPrice, label = { Text("Precio") }, modifier = Modifier.fillMaxWidth())
    OutlinedTextField(description, onDescription, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
    OutlinedTextField(imageUrl, onImageUrl, label = { Text("URL de imagen") }, modifier = Modifier.fillMaxWidth())
    OutlinedTextField(category, onCategory, label = { Text("Categoría") }, modifier = Modifier.fillMaxWidth())
}
