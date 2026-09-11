package com.example.equiposixseven

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var buscadorAbierto by remember { mutableStateOf(false) }
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
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        // Fila unificada de herramientas y buscador flotante superpuesto
        BarraHerramientasCatalogo(
            categoriaSeleccionada = categoriaSeleccionada,
            onSeleccionarCategoria = { cat ->
                categoriaSeleccionada = cat
                alcance.launch { cargar(cat) }
            },
            busqueda = busqueda,
            onCambiarBusqueda = { busqueda = it },
            buscadorAbierto = buscadorAbierto,
            onAlternarBuscador = { buscadorAbierto = it },
            cargando = cargando,
            onActualizar = { alcance.launch { cargar(categoriaSeleccionada) } }
        )

        Spacer(modifier = Modifier.height(14.dp))

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
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
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

/** Tarjeta visual con paneles blancos flotantes, sombra profunda y sin etiqueta de categoría. */
@Composable
private fun TarjetaProducto(
    producto: Producto,
    onClick: () -> Unit,
    onAgregarAlCarrito: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 14.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color(0xFF00245A).copy(alpha = 0.28f),
                ambientColor = Color(0xFF00245A).copy(alpha = 0.10f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color(0xFF0F172A)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
            pressedElevation = 14.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.15f)
                    .background(Color.White)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = producto.imagenUrl,
                    contentDescription = producto.titulo,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Text(
                    producto.titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "$${String.format(Locale.US, "%.2f", producto.precio)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF00245A),
                        fontWeight = FontWeight.Black
                    )
                    FilledIconButton(
                        onClick = onAgregarAlCarrito,
                        modifier = Modifier.size(36.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Color(0xFF009D81),
                            contentColor = Color.White
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

/** Barra unificada de filtros por icono, lupa superpuesta y botón de actualización. */
@Composable
private fun BarraHerramientasCatalogo(
    categoriaSeleccionada: String,
    onSeleccionarCategoria: (String) -> Unit,
    busqueda: String,
    onCambiarBusqueda: (String) -> Unit,
    buscadorAbierto: Boolean,
    onAlternarBuscador: (Boolean) -> Unit,
    cargando: Boolean,
    onActualizar: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        // Fila estándar: Filtros por iconos + Lupa + Actualizar
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Categorías en desplazamiento horizontal
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val categorias = listOf("Todos") + AlmacenAplicacion.categorias
                categorias.forEach { categoria ->
                    val seleccionada = categoriaSeleccionada == categoria
                    IconButton(
                        onClick = {
                            if (categoriaSeleccionada != categoria) {
                                onSeleccionarCategoria(categoria)
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .shadow(
                                elevation = if (seleccionada) 6.dp else 2.dp,
                                shape = RoundedCornerShape(14.dp),
                                spotColor = Color(0xFF00245A).copy(alpha = 0.2f)
                            )
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (seleccionada) Color(0xFF00245A)
                                else Color.White
                            )
                            .border(
                                width = 1.dp,
                                color = if (seleccionada) Color(0xFF00245A) else Color(0xFFE2E8F0),
                                shape = RoundedCornerShape(14.dp)
                            ),
                        enabled = !cargando
                    ) {
                        Icon(
                            imageVector = iconoParaCategoria(categoria),
                            contentDescription = categoria,
                            tint = if (seleccionada) Color.White else Color(0xFF00245A),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Botón Lupa de búsqueda (abre la barra por encima sin desplazar el catálogo)
            IconButton(
                onClick = { onAlternarBuscador(true) },
                modifier = Modifier
                    .size(44.dp)
                    .shadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(14.dp),
                        spotColor = Color(0xFF00245A).copy(alpha = 0.2f)
                    )
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar producto",
                    tint = Color(0xFF00245A),
                    modifier = Modifier.size(22.dp)
                )
            }

            // Botón Actualizar catálogo
            IconButton(
                onClick = onActualizar,
                enabled = !cargando,
                modifier = Modifier
                    .size(44.dp)
                    .shadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(14.dp),
                        spotColor = Color(0xFF00245A).copy(alpha = 0.2f)
                    )
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Actualizar catálogo",
                    tint = Color(0xFF00245A),
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Barra de búsqueda que se superpone por encima sin mover el catálogo
        AnimatedVisibility(
            visible = buscadorAbierto,
            enter = fadeIn() + expandHorizontally(expandFrom = Alignment.End),
            exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.End),
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(14.dp),
                        spotColor = Color(0xFF00245A).copy(alpha = 0.3f)
                    )
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFF00D1A7), RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color(0xFF00245A),
                    modifier = Modifier.size(20.dp)
                )
                BasicTextField(
                    value = busqueda,
                    onValueChange = onCambiarBusqueda,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    textStyle = TextStyle(
                        color = Color(0xFF0F172A),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        if (busqueda.isEmpty()) {
                            Text(
                                "Buscar producto…",
                                color = Color(0xFF94A3B8),
                                fontSize = 14.sp
                            )
                        }
                        innerTextField()
                    }
                )
                if (busqueda.isNotEmpty()) {
                    IconButton(
                        onClick = { onCambiarBusqueda("") },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Limpiar texto",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                IconButton(
                    onClick = {
                        onAlternarBuscador(false)
                        onCambiarBusqueda("")
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar búsqueda",
                        tint = Color(0xFF00245A),
                        modifier = Modifier.size(20.dp)
                    )
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
