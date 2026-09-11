package com.example.equiposixseven

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Man
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Woman
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * US03 y US04.
 * Catálogo remoto con imágenes, búsqueda local, categorías, loading, error y reintento.
 */
@Composable
fun PantallaCatalogo(
    padding: PaddingValues,
    onAbrirProducto: (Int) -> Unit,
    onMensaje: (String) -> Unit
) {
    val contexto = LocalContext.current
    val alcance = rememberCoroutineScope()

    var categoriaSeleccionada by remember { mutableStateOf("Todos") }
    var busqueda by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    /** Descarga el catálogo general o la categoría activa y reemplaza el arreglo anterior. */
    suspend fun cargar(categoria: String = categoriaSeleccionada) {
        if (!hayConexionDisponible(contexto)) {
            error = "No hay conexión a internet."
            return
        }

        cargando = true
        error = null
        try {
            val productos = if (categoria == "Todos") {
                ServicioApi.obtenerProductos()
            } else {
                ServicioApi.obtenerProductosPorCategoria(categoria)
            }
            AlmacenAplicacion.reemplazarProductos(productos)

            if (AlmacenAplicacion.categorias.isEmpty()) {
                AlmacenAplicacion.reemplazarCategorias(ServicioApi.obtenerCategorias())
            }
        } catch (e: Exception) {
            error = e.message ?: "No fue posible cargar el catálogo."
        } finally {
            cargando = false
        }
    }

    // US03: el catálogo se solicita al entrar por primera vez a la pantalla.
    LaunchedEffect(Unit) {
        if (AlmacenAplicacion.productos.isEmpty()) {
            cargar("Todos")
        } else if (AlmacenAplicacion.categorias.isEmpty() && hayConexionDisponible(contexto)) {
            runCatching { ServicioApi.obtenerCategorias() }
                .onSuccess(AlmacenAplicacion::reemplazarCategorias)
        }
    }

    val productosVisibles = AlmacenAplicacion.productos.filter { producto ->
        busqueda.isBlank() ||
            producto.titulo.contains(busqueda, ignoreCase = true) ||
            producto.categoria.contains(busqueda, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Catálogo",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Black
            )
            IconButton(
                onClick = { alcance.launch { cargar(categoriaSeleccionada) } },
                enabled = !cargando
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Actualizar catálogo")
            }
        }

        // Búsqueda local sin peticiones redundantes
        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            label = { Text("Buscar producto") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        // Barra de filtros con iconos elegantes sin textos recargados
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val categorias = listOf("Todos") + AlmacenAplicacion.categorias
            categorias.forEach { categoria ->
                val seleccionada = categoriaSeleccionada == categoria
                IconButton(
                    onClick = {
                        if (categoriaSeleccionada != categoria) {
                            categoriaSeleccionada = categoria
                            alcance.launch { cargar(categoria) }
                        }
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (seleccionada) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                    enabled = !cargando
                ) {
                    Icon(
                        imageVector = iconoParaCategoria(categoria),
                        contentDescription = categoria,
                        tint = if (seleccionada) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        when {
            cargando -> EstadoCargando("Consultando Fake Store API…")
            error != null -> EstadoError(
                mensaje = error!!,
                onReintentar = { alcance.launch { cargar(categoriaSeleccionada) } }
            )
            productosVisibles.isEmpty() -> EstadoVacio(
                titulo = "Sin coincidencias",
                descripcion = "Prueba otra búsqueda o cambia el filtro de categoría."
            )
            else -> LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 158.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(productosVisibles, key = { it.id }) { producto ->
                    TarjetaProducto(
                        producto = producto,
                        onClick = { onAbrirProducto(producto.id) },
                        onAgregarAlCarrito = {
                            AlmacenAplicacion.agregarAlCarrito(producto, 1)
                            onMensaje("${producto.titulo.take(24)}… agregado al carrito")
                        }
                    )
                }
            }
        }
    }
}

/** Tarjeta visual con botón directo para añadir al carrito con un solo toque. */
@Composable
private fun TarjetaProducto(
    producto: Producto,
    onClick: () -> Unit,
    onAgregarAlCarrito: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            AsyncImage(
                model = producto.imagenUrl,
                contentDescription = producto.titulo,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.2f)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)),
                contentScale = ContentScale.Fit
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    producto.categoria.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 1
                )
                Text(
                    producto.titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "$${String.format(Locale.US, "%.2f", producto.precio)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Black
                    )
                    FilledIconButton(
                        onClick = onAgregarAlCarrito,
                        modifier = Modifier.size(36.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart,
                            contentDescription = "Agregar al carrito",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

/** Retorna un icono representativo según la categoría. */
private fun iconoParaCategoria(categoria: String): ImageVector = when (categoria.lowercase(Locale.ROOT)) {
    "todos" -> Icons.Default.GridView
    "electronics" -> Icons.Default.Devices
    "jewelery" -> Icons.Default.Diamond
    "men's clothing" -> Icons.Default.Man
    "women's clothing" -> Icons.Default.Woman
    else -> Icons.Default.Category
}
