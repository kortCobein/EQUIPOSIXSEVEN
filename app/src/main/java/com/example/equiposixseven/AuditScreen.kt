package com.example.equiposixseven

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Locale

/**
 * US11 - Listar todos los usuarios registrados.
 * US12 - Visualizar histórico de carritos globales.
 */
@Composable
fun AuditScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text("Auditoría", style = MaterialTheme.typography.headlineSmall)
            Text(
                "Usuarios registrados",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
        }

        items(AppStore.users, key = { "user-${it.id}" }) { user ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(user.username, style = MaterialTheme.typography.titleMedium)
                    Text("ID: ${user.id}")
                    Text("Perfil: ${user.role.name}")
                }
            }
        }

        item {
            Text(
                "Histórico global de carritos",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
            )
        }

        items(AppStore.cartHistory, key = { "history-${it.id}" }) { history ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Carrito #${history.id}", style = MaterialTheme.typography.titleMedium)
                    Text("Usuario: ${history.username}")
                    Text("Artículos: ${history.itemCount}")
                    Text("Total: $${String.format(Locale.US, "%.2f", history.total)}")
                }
            }
        }
    }
}
