package com.example.equiposixseven

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * US06 - Panel de inventario exclusivo para Administrador.
 * Permite crear productos con validación local antes de ejecutar POST /products.
 */
@Composable
fun PantallaInventario(
    padding: PaddingValues,
    sesion: UsuarioSesion,
    onAbrirProducto: (Int) -> Unit,
    onMensaje: (String) -> Unit
) {
    val contexto = LocalContext.current
    val alcance = rememberCoroutineScope()

    // Defensa adicional: aunque la navegación ya oculta esta sección, la pantalla valida el rol.
    if (sesion.rol != RolUsuario.ADMINISTRADOR) {
        Column(Modifier.padding(padding)) {
            EstadoError("Tu perfil no tiene permisos para administrar el inventario.")
        }
        return
    }

    var mostrarFormulario by remember { mutableStateOf(false) }
    var titulo by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var imagen by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var guardando by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Inventario",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        "Alta y mantenimiento del catálogo",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Button(onClick = { mostrarFormulario = !mostrarFormulario }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text(if (mostrarFormulario) "Cerrar" else "Nuevo", modifier = Modifier.padding(start = 6.dp))
                }
            }
        }

        // El formulario se muestra bajo demanda para no saturar la pantalla administrativa.
        if (mostrarFormulario) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Nuevo producto", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        CampoProducto("Título", titulo, { titulo = it }, Modifier.padding(top = 10.dp))
                        CampoProducto("Precio", precio, { precio = it })
                        CampoProducto("Descripción", descripcion, { descripcion = it })
                        CampoProducto("URL de imagen", imagen, { imagen = it })
                        CampoProducto("Categoría", categoria, { categoria = it })

                        error?.let {
                            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
                        }

                        Button(
                            onClick = {
                                val precioNumero = precio.toDoubleOrNull()
                                error = validarProducto(titulo, precioNumero, descripcion, imagen, categoria)
                                if (error != null) return@Button
                                if (!hayConexionDisponible(contexto)) {
                                    error = "No hay conexión para crear el producto."
                                    return@Button
                                }

                                alcance.launch {
                                    guardando = true
                                    try {
                                        val temporal = Producto(
                                            id = 0,
                                            titulo = titulo.trim(),
                                            precio = precioNumero!!,
                                            descripcion = descripcion.trim(),
                                            imagenUrl = imagen.trim(),
                                            categoria = categoria.trim()
                                        )
                                        val creado = ServicioApi.crearProducto(temporal, sesion.token)
                                        AlmacenAplicacion.agregarProducto(creado)
                                        titulo = ""
                                        precio = ""
                                        descripcion = ""
                                        imagen = ""
                                        categoria = ""
                                        error = null
                                        mostrarFormulario = false
                                        onMensaje("Producto creado. ID simulado: ${creado.id}")
                                    } catch (e: Exception) {
                                        error = e.message ?: "No fue posible crear el producto."
                                    } finally {
                                        guardando = false
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            enabled = !guardando
                        ) {
                            if (guardando) {
                                CircularProgressIndicator(
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text("Guardar producto")
                            }
                        }
                    }
                }
            }
        }

        item {
            Text(
                "Productos disponibles",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 6.dp)
            )
        }

        if (AlmacenAplicacion.productos.isEmpty()) {
            item {
                EstadoVacio(
                    titulo = "Catálogo sin cargar",
                    descripcion = "Abre primero el catálogo para descargar los productos o crea uno nuevo."
                )
            }
        } else {
            items(AlmacenAplicacion.productos, key = { it.id }) { producto ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(producto.titulo, fontWeight = FontWeight.SemiBold)
                            Text(
                                "${producto.categoria} · $${String.format(Locale.US, "%.2f", producto.precio)}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        OutlinedButton(onClick = { onAbrirProducto(producto.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                            Text("Gestionar", modifier = Modifier.padding(start = 4.dp))
                        }
                    }
                }
            }
        }
    }
}
