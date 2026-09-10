package com.example.equiposixseven

import android.util.Patterns
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** US06 - Agregar nuevo producto al catálogo. */
@Composable
fun AdminScreen(onOpenProduct: (Int) -> Unit) {
    var title by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text("Administrar catálogo", style = MaterialTheme.typography.headlineSmall)
            Text("Agregar producto", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 16.dp))

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
            message?.let { Text(it) }

            Button(
                onClick = {
                    val parsedPrice = price.toDoubleOrNull()
                    error = when {
                        title.isBlank() -> "El título es obligatorio"
                        parsedPrice == null -> "El precio debe ser numérico"
                        parsedPrice < 0 -> "El precio no puede ser negativo"
                        description.isBlank() -> "La descripción es obligatoria"
                        imageUrl.isBlank() -> "La URL de imagen es obligatoria"
                        !Patterns.WEB_URL.matcher(imageUrl).matches() -> "Ingresa una URL válida"
                        category.isBlank() -> "La categoría es obligatoria"
                        else -> null
                    }

                    if (error == null) {
                        val created = AppStore.addProduct(
                            title.trim(),
                            parsedPrice!!,
                            description.trim(),
                            imageUrl.trim(),
                            category.trim()
                        )
                        message = "Producto creado correctamente. ID generado: ${created.id}"
                        title = ""
                        price = ""
                        description = ""
                        imageUrl = ""
                        category = ""
                    }
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Crear producto")
            }

            Text(
                "Productos existentes",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
            )
        }

        items(AppStore.products, key = { it.id }) { product ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(product.title)
                    Button(onClick = { onOpenProduct(product.id) }, modifier = Modifier.padding(top = 6.dp)) {
                        Text("Editar o eliminar")
                    }
                }
            }
        }
    }
}
