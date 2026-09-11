package com.example.equiposixseven

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import java.util.Locale

/** US10 - Carrito personal con PUT/DELETE simulados y total calculado en tiempo real. */
@Composable
fun PantallaCarrito(
    padding: PaddingValues,
    sesion: UsuarioSesion,
    onExplorarCatalogo: () -> Unit,
    onMensaje: (String) -> Unit
) {
    val contexto = LocalContext.current
    val alcance = rememberCoroutineScope()
    var productoProcesando by remember { mutableStateOf<Int?>(null) }

    // Auditor no debe tener acceso al carrito ni siquiera por una llamada forzada al composable.
    if (sesion.rol == RolUsuario.AUDITOR) {
        Column(Modifier.padding(padding)) {
            EstadoError("El perfil Auditor es de solo lectura y no tiene carrito personal.")
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp)
    ) {
        Text(
            "Mi carrito",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Black
        )
        Text(
            "Ajusta cantidades antes de finalizar la simulación",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (AlmacenAplicacion.carrito.isEmpty()) {
            EstadoVacio(
                titulo = "Tu carrito está vacío",
                descripcion = "Explora el catálogo y agrega los productos que quieras probar.",
                accion = "Explorar catálogo",
                onAccion = onExplorarCatalogo
            )
            // US10 exige conservar visible el flujo de pago, pero inhabilitado sin artículos.
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                enabled = false
            ) {
                Text("Proceder al pago")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(AlmacenAplicacion.carrito, key = { it.productoId }) { item ->
                    TarjetaItemCarrito(
                        item = item,
                        procesando = productoProcesando == item.productoId,
                        onCambiarCantidad = { nuevaCantidad ->
                            if (!hayConexionDisponible(contexto)) {
                                onMensaje("Sin conexión: no se pudo sincronizar el cambio.")
                            } else {
                                alcance.launch {
                                    productoProcesando = item.productoId
                                    try {
                                        val copia = AlmacenAplicacion.carrito.map { actual ->
                                            if (actual.productoId == item.productoId) {
                                                actual.copy(cantidad = nuevaCantidad)
                                            } else actual
                                        }.filter { it.cantidad > 0 }

                                        val idCarrito = AlmacenAplicacion.idCarritoRemoto ?: 1
                                        if (nuevaCantidad <= 0) {
                                            ServicioApi.eliminarCarrito(idCarrito, sesion.token)
                                            AlmacenAplicacion.eliminarDelCarrito(item.productoId)
                                        } else {
                                            ServicioApi.actualizarCarrito(idCarrito, sesion.id, copia, sesion.token)
                                            AlmacenAplicacion.cambiarCantidad(item.productoId, nuevaCantidad)
                                        }
                                    } catch (e: Exception) {
                                        onMensaje(e.message ?: "No fue posible modificar el carrito.")
                                    } finally {
                                        productoProcesando = null
                                    }
                                }
                            }
                        },
                        onEliminar = {
                            if (!hayConexionDisponible(contexto)) {
                                onMensaje("Sin conexión: no se pudo eliminar el artículo.")
                            } else {
                                alcance.launch {
                                    productoProcesando = item.productoId
                                    try {
                                        val idCarrito = AlmacenAplicacion.idCarritoRemoto ?: 1
                                        ServicioApi.eliminarCarrito(idCarrito, sesion.token)
                                        AlmacenAplicacion.eliminarDelCarrito(item.productoId)
                                        onMensaje("Artículo eliminado del carrito.")
                                    } catch (e: Exception) {
                                        onMensaje(e.message ?: "No fue posible eliminar el artículo.")
                                    } finally {
                                        productoProcesando = null
                                    }
                                }
                            }
                        }
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        "Total",
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                    Text(
                        "$${String.format(Locale.US, "%.2f", AlmacenAplicacion.totalCarrito())}",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black
                    )
                    Button(
                        onClick = { onMensaje("Pago fuera del alcance de este sprint. El carrito está listo.") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        enabled = AlmacenAplicacion.carrito.isNotEmpty()
                    ) {
                        Text("Proceder al pago")
                    }
                }
            }
        }
    }
}

/** Tarjeta de carrito con controles grandes y accesibles para cantidad/eliminación. */
@Composable
private fun TarjetaItemCarrito(
    item: ItemCarrito,
    procesando: Boolean,
    onCambiarCantidad: (Int) -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = item.imagenUrl,
                contentDescription = item.titulo,
                modifier = Modifier.height(72.dp).weight(0.25f),
                contentScale = ContentScale.Fit
            )
            Column(modifier = Modifier.weight(0.75f)) {
                Text(item.titulo, fontWeight = FontWeight.SemiBold, maxLines = 2)
                Text(
                    "$${String.format(Locale.US, "%.2f", item.precio)} c/u",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { onCambiarCantidad(item.cantidad - 1) },
                        enabled = !procesando
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Reducir cantidad")
                    }
                    if (procesando) {
                        CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.height(20.dp))
                    } else {
                        Text("${item.cantidad}", fontWeight = FontWeight.Bold)
                    }
                    IconButton(
                        onClick = { onCambiarCantidad(item.cantidad + 1) },
                        enabled = !procesando
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Aumentar cantidad")
                    }
                    IconButton(onClick = onEliminar, enabled = !procesando) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar del carrito",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
                Text(
                    "Subtotal: $${String.format(Locale.US, "%.2f", item.precio * item.cantidad)}",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
