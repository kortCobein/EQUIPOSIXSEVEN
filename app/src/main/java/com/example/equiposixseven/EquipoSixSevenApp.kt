package com.example.equiposixseven

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EquipoSixSevenApp() {
    var currentUser by remember { mutableStateOf<AppUser?>(null) }
    var section by remember { mutableStateOf(AppSection.CATALOG) }
    var selectedProductId by remember { mutableStateOf<Int?>(null) }

    if (currentUser == null) {
        LoginScreen(
            onLogin = { user ->
                currentUser = user
                section = AppSection.CATALOG
                selectedProductId = null
            }
        )
        return
    }

    val user = currentUser!!

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Equipo Six Seven", style = MaterialTheme.typography.titleLarge)
                Text("${user.username} · ${user.role.name}")
            }

            // US02: cerrar sesión limpia el usuario actual y regresa al login.
            OutlinedButton(
                onClick = {
                    currentUser = null
                    selectedProductId = null
                    section = AppSection.CATALOG
                }
            ) {
                Text("Cerrar sesión")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NavButton("Catálogo", section == AppSection.CATALOG && selectedProductId == null) {
                section = AppSection.CATALOG
                selectedProductId = null
            }

            if (user.role != UserRole.AUDITOR) {
                NavButton("Carrito", section == AppSection.CART) {
                    section = AppSection.CART
                    selectedProductId = null
                }
            }

            if (user.role == UserRole.ADMINISTRADOR) {
                NavButton("Administrar", section == AppSection.ADMIN) {
                    section = AppSection.ADMIN
                    selectedProductId = null
                }
            }

            if (user.role == UserRole.AUDITOR) {
                NavButton("Auditoría", section == AppSection.AUDIT) {
                    section = AppSection.AUDIT
                    selectedProductId = null
                }
            }
        }

        if (selectedProductId != null) {
            ProductDetailScreen(
                productId = selectedProductId!!,
                userRole = user.role,
                onBack = { selectedProductId = null }
            )
        } else {
            when (section) {
                AppSection.CATALOG -> CatalogScreen(
                    onOpenProduct = { selectedProductId = it },
                    userRole = user.role
                )

                AppSection.CART -> CartScreen()
                AppSection.ADMIN -> AdminScreen(onOpenProduct = { selectedProductId = it })
                AppSection.AUDIT -> AuditScreen()
            }
        }
    }
}

@Composable
private fun NavButton(label: String, selected: Boolean, onClick: () -> Unit) {
    if (selected) {
        Button(onClick = onClick) { Text(label) }
    } else {
        OutlinedButton(onClick = onClick) { Text(label) }
    }
}
