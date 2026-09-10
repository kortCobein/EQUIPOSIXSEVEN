package com.example.equiposixseven

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Locale

/** US10 - Visualizar, modificar o eliminar artículos del carrito personal. */
@Composable
fun CartScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Mi carrito", style = MaterialTheme.typography.headlineSmall)

        if (AppStore.cart.isEmpty()) {
            Text("El carrito está vacío.", modifier = Modifier.padding(top = 16.dp))
            return
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(AppStore.cart, key = { it.productId }) { item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(item.title, style = MaterialTheme.typography.titleMedium)
                        Text("$${String.format(Locale.US, "%.2f", item.price)} c/u")

                        Row(
                            modifier = Modifier.padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    AppStore.changeCartQuantity(item.productId, item.quantity - 1)
                                }
                            ) {
                                Text("-")
                            }

                            Text("Cantidad: ${item.quantity}", modifier = Modifier.padding(top = 12.dp))

                            OutlinedButton(
                                onClick = {
                                    AppStore.changeCartQuantity(item.productId, item.quantity + 1)
                                }
                            ) {
                                Text("+")
                            }
                        }

                        Button(
                            onClick = { AppStore.removeFromCart(item.productId) },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Eliminar del carrito")
                        }
                    }
                }
            }
        }

        Text(
            text = "Total: $${String.format(Locale.US, "%.2f", AppStore.cartTotal())}",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}
