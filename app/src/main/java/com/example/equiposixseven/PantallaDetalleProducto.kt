package com.example.equiposixseven

import android.util.Patterns
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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

/**
 * US05, US07, US08 y US09.
 *
 * Esta pantalla concentra el detalle remoto de producto y, según el rol, habilita:
 * - Cliente/Admin: cantidad + agregar al carrito.
 * - Admin: editar y eliminar.
 * - Auditor: solo lectura.
 */
@Composable
fun PantallaDetalleProducto(
    productoId: Int,
    sesion: UsuarioSesion,
    padding: PaddingValues,
    onVolver: () -> Unit,
    onProductoEliminado: () -> Unit,
    onMensaje: (String) -> Unit
) {
    val contexto = LocalContext.current
    val alcance = rememberCoroutineScope()

    var producto by remember(productoId) {
        mutableStateOf(AlmacenAplicacion.productos.firstOrNull { it.id == productoId })
    }
    var cargando by remember(productoId) { mutableStateOf(producto == null) }
    var error by remember(productoId) { mutableStateOf<String?>(null) }
    var cantidad by remember(productoId) { mutableIntStateOf(1) }
    var editando by remember(productoId) { mutableStateOf(false) }
    var guardando by remember(productoId) { mutableStateOf(false) }
    var agregando by remember(productoId) { mutableStateOf(false) }
    var eliminando by remember(productoId) { mutableStateOf(false) }
    var confirmarEliminacion by remember(productoId) { mutableStateOf(false) }

    /**
     * Carga el detalle desde GET /products/{id} cuando es un producto remoto.
     * Los productos recién creados en esta sesión pueden tener ID local y se leen del almacén.
     */
    suspend fun cargarDetalle() {
        val local = AlmacenAplicacion.productos.firstOrNull { it.id == productoId }
        if (productoId > 20 && local != null) {
            producto = local
            cargando = false
            return
        }
        if (!hayConexionDisponible(contexto)) {
            error = "No hay conexión a internet para consultar el producto."
            cargando = false
            return
        }

        cargando = true
        error = null
        try {
            producto = ServicioApi.obtenerProducto(productoId).also(AlmacenAplicacion::actualizarProducto)
        } catch (e: Exception) {
            error = e.message ?: "No fue posible cargar el producto."
        } finally {
            cargando = false
        }
    }

    LaunchedEffect(productoId) {
        if (producto == null) cargarDetalle()
    }
    BackHandler(onBack = onVolver)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        IconButton(onClick = onVolver) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
        }

        when {
            cargando -> EstadoCargando("Cargando detalle…")
            error != null -> EstadoError(error!!, onReintentar = { alcance.launch { cargarDetalle() } })
            producto == null -> EstadoVacio("Producto no disponible", "El artículo ya no existe o no pudo cargarse.")
            else -> {
                val actual = producto!!
                AsyncImage(
                    model = actual.imagenUrl,
                    contentDescription = actual.titulo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    contentScale = ContentScale.Fit
                )
                Text(
                    actual.categoria.uppercase(),
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    actual.titulo,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(top = 6.dp)
                )
                Text(
                    "$${String.format(Locale.US, "%.2f", actual.precio)}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(top = 10.dp)
                )
                Text(
                    actual.descripcion,
                    modifier = Modifier.padding(top = 12.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (sesion.rol != RolUsuario.AUDITOR) {
                    SelectorCantidad(cantidad) { cantidad = it }
                    Button(
                        onClick = {
                            if (!hayConexionDisponible(contexto)) {
                                onMensaje("Sin conexión: no se pudo sincronizar el carrito.")
                                return@Button
                            }
                            alcance.launch {
                                agregando = true
                                try {
                                    val copia = AlmacenAplicacion.carrito.toMutableList()
                                    val indice = copia.indexOfFirst { it.productoId == actual.id }
                                    if (indice >= 0) {
                                        val item = copia[indice]
                                        copia[indice] = item.copy(cantidad = item.cantidad + cantidad)
                                    } else {
                                        copia += ItemCarrito(actual.id, actual.titulo, actual.precio, actual.imagenUrl, cantidad)
                                    }
                                    val id = ServicioApi.crearCarrito(sesion.id, copia, sesion.token)
                                    AlmacenAplicacion.establecerIdCarritoRemoto(id)
                                    AlmacenAplicacion.agregarAlCarrito(actual, cantidad)
                                    onMensaje("Producto agregado al carrito.")
                                } catch (e: Exception) {
                                    onMensaje(e.message ?: "No fue posible agregar el producto.")
                                } finally {
                                    agregando = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !agregando
                    ) {
                        if (agregando) CircularProgressIndicator(strokeWidth = 2.dp) else Text("Agregar al carrito")
                    }
                }

                if (sesion.rol == RolUsuario.ADMINISTRADOR) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { editando = !editando },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                            Text(if (editando) "Cancelar" else "Editar", modifier = Modifier.padding(start = 6.dp))
                        }
                        OutlinedButton(
                            onClick = { confirmarEliminacion = true },
                            modifier = Modifier.weight(1f),
                            enabled = !eliminando
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null)
                            Text("Eliminar", modifier = Modifier.padding(start = 6.dp))
                        }
                    }

                    if (editando) {
                        FormularioEdicionCompacto(
                            producto = actual,
                            guardando = guardando,
                            onGuardar = { actualizado ->
                                if (!hayConexionDisponible(contexto)) {
                                    onMensaje("Sin conexión: no se pudo actualizar el producto.")
                                } else {
                                    alcance.launch {
                                        guardando = true
                                        try {
                                            val respuesta = ServicioApi.actualizarProducto(actualizado, sesion.token)
                                            AlmacenAplicacion.actualizarProducto(respuesta)
                                            producto = respuesta
                                            editando = false
                                            onMensaje("Producto actualizado correctamente.")
                                        } catch (e: Exception) {
                                            onMensaje(e.message ?: "No fue posible actualizar el producto.")
                                        } finally {
                                            guardando = false
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (confirmarEliminacion && producto != null) {
        AlertDialog(
            onDismissRequest = { confirmarEliminacion = false },
            title = { Text("Eliminar producto") },
            text = { Text("Esta acción quitará el artículo del catálogo y del carrito local.") },
            confirmButton = {
                TextButton(onClick = {
                    val id = producto!!.id
                    confirmarEliminacion = false
                    alcance.launch {
                        eliminando = true
                        try {
                            if (!hayConexionDisponible(contexto)) throw ExcepcionApi("No hay conexión para eliminar el producto.")
                            ServicioApi.eliminarProducto(id, sesion.token)
                            AlmacenAplicacion.eliminarProducto(id)
                            onMensaje("Producto eliminado.")
                            onProductoEliminado()
                        } catch (e: Exception) {
                            onMensaje(e.message ?: "No fue posible eliminar el producto.")
                        } finally {
                            eliminando = false
                        }
                    }
                }) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { confirmarEliminacion = false }) { Text("Cancelar") } }
        )
    }
}

/** Control grande y simple para ajustar cantidad antes de agregar al carrito. */
@Composable
private fun SelectorCantidad(cantidad: Int, onCambio: (Int) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Cantidad", fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { if (cantidad > 1) onCambio(cantidad - 1) }) {
                    Icon(Icons.Default.Remove, contentDescription = "Reducir")
                }
                Text("$cantidad", fontWeight = FontWeight.Black)
                IconButton(onClick = { onCambio(cantidad + 1) }) {
                    Icon(Icons.Default.Add, contentDescription = "Aumentar")
                }
            }
        }
    }
}

/** Formulario de edición reutilizando exactamente las mismas reglas que el alta de producto. */
@Composable
private fun FormularioEdicionCompacto(
    producto: Producto,
    guardando: Boolean,
    onGuardar: (Producto) -> Unit
) {
    var titulo by remember(producto.id) { mutableStateOf(producto.titulo) }
    var precio by remember(producto.id) { mutableStateOf(producto.precio.toString()) }
    var descripcion by remember(producto.id) { mutableStateOf(producto.descripcion) }
    var imagen by remember(producto.id) { mutableStateOf(producto.imagenUrl) }
    var categoria by remember(producto.id) { mutableStateOf(producto.categoria) }
    var error by remember(producto.id) { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(top = 16.dp)) {
        Text("Editar producto", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        CampoProducto("Título", titulo, { titulo = it })
        CampoProducto("Precio", precio, { precio = it })
        CampoProducto("Descripción", descripcion, { descripcion = it })
        CampoProducto("URL de imagen", imagen, { imagen = it })
        CampoProducto("Categoría", categoria, { categoria = it })
        error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp)) }

        Button(
            onClick = {
                val numero = precio.toDoubleOrNull()
                error = validarProducto(titulo, numero, descripcion, imagen, categoria)
                if (error == null) {
                    onGuardar(
                        producto.copy(
                            titulo = titulo.trim(),
                            precio = numero!!,
                            descripcion = descripcion.trim(),
                            imagenUrl = imagen.trim(),
                            categoria = categoria.trim()
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
            enabled = !guardando
        ) {
            Text(if (guardando) "Guardando…" else "Guardar cambios")
        }
    }
}

/** Campo estándar de producto compartido por alta y edición. */
@Composable
fun CampoProducto(
    etiqueta: String,
    valor: String,
    onCambio: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        modifier = modifier.fillMaxWidth().padding(top = 8.dp),
        singleLine = etiqueta != "Descripción"
    )
}

/** Reglas de validación comunes para US06 y US07. */
fun validarProducto(
    titulo: String,
    precio: Double?,
    descripcion: String,
    imagen: String,
    categoria: String
): String? = when {
    titulo.isBlank() -> "El título es obligatorio."
    precio == null -> "El precio debe ser numérico."
    precio < 0 -> "El precio no puede ser negativo."
    descripcion.isBlank() -> "La descripción es obligatoria."
    imagen.isBlank() -> "La URL de imagen es obligatoria."
    !Patterns.WEB_URL.matcher(imagen).matches() -> "Ingresa una URL de imagen válida."
    categoria.isBlank() -> "La categoría es obligatoria."
    else -> null
}
