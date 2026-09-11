package com.example.equiposixseven

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * US11 y US12.
 * Panel estrictamente de lectura para Administrador/Auditor con usuarios y carritos globales.
 */
@Composable
fun PantallaAuditoria(
    padding: PaddingValues,
    sesion: UsuarioSesion
) {
    val contexto = LocalContext.current
    val alcance = rememberCoroutineScope()

    if (sesion.rol == RolUsuario.CLIENTE) {
        Column(Modifier.padding(padding)) {
            EstadoError("El perfil Cliente no tiene permiso para consultar auditorías globales.")
        }
        return
    }

    var seccionUsuarios by remember { mutableStateOf(true) }
    var cargando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var carritoExpandido by remember { mutableIntStateOf(-1) }

    /** Descarga usuarios, carritos y catálogo para poder cruzar productId con títulos. */
    suspend fun cargarAuditoria() {
        if (!hayConexionDisponible(contexto)) {
            error = "No hay conexión para consultar la auditoría."
            return
        }

        cargando = true
        error = null
        try {
            val usuarios = ServicioApi.obtenerUsuarios()
            val carritos = ServicioApi.obtenerCarritos()
            val productos = ServicioApi.obtenerProductos()
            AlmacenAplicacion.reemplazarUsuariosAuditoria(usuarios)
            AlmacenAplicacion.reemplazarCarritosAuditoria(carritos)
            AlmacenAplicacion.reemplazarProductos(productos)
        } catch (e: Exception) {
            error = e.message ?: "No fue posible descargar la auditoría."
        } finally {
            cargando = false
        }
    }

    LaunchedEffect(Unit) { cargarAuditoria() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp)
    ) {
        Text(
            "Auditoría",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Black
        )
        Text(
            "Consulta de solo lectura",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Dos vistas de lectura dentro del mismo módulo: usuarios y carritos globales.
        Row(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = seccionUsuarios,
                onClick = { seccionUsuarios = true },
                label = { Text("Usuarios") }
            )
            FilterChip(
                selected = !seccionUsuarios,
                onClick = { seccionUsuarios = false },
                label = { Text("Carritos") }
            )
        }

        when {
            cargando -> EstadoCargando("Descargando datos de auditoría…")
            error != null -> EstadoError(error!!, onReintentar = { alcance.launch { cargarAuditoria() } })
            seccionUsuarios -> ListaUsuariosAuditoria()
            else -> ListaCarritosAuditoria(
                expandido = carritoExpandido,
                onExpandir = { id -> carritoExpandido = if (carritoExpandido == id) -1 else id }
            )
        }
    }
}

/** US11: nombre completo, correo, teléfono y username de cada cuenta. */
@Composable
private fun ListaUsuariosAuditoria() {
    if (AlmacenAplicacion.usuariosAuditoria.isEmpty()) {
        EstadoVacio("Sin usuarios", "La API no devolvió cuentas para mostrar.")
        return
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        items(AlmacenAplicacion.usuariosAuditoria, key = { it.id }) { usuario ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                    Column {
                        Text(
                            "${usuario.nombre.nombre.replaceFirstChar { it.uppercase() }} ${usuario.nombre.apellido.replaceFirstChar { it.uppercase() }}",
                            fontWeight = FontWeight.Bold
                        )
                        Text("@${usuario.usuario}", color = MaterialTheme.colorScheme.primary)
                        Text(usuario.correo, style = MaterialTheme.typography.bodySmall)
                        Text(usuario.telefono, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

/** US12: lista carritos y permite expandir cada uno para ver productos y cantidades. */
@Composable
private fun ListaCarritosAuditoria(
    expandido: Int,
    onExpandir: (Int) -> Unit
) {
    if (AlmacenAplicacion.carritosAuditoria.isEmpty()) {
        EstadoVacio("Sin carritos", "No hay operaciones globales disponibles para auditar.")
        return
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        items(AlmacenAplicacion.carritosAuditoria, key = { it.id }) { carrito ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandir(carrito.id) },
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Carrito #${carrito.id}", fontWeight = FontWeight.Bold)
                            Text("Usuario ID: ${carrito.usuarioId}")
                            Text("Fecha: ${carrito.fecha.take(10)}")
                        }
                        Icon(
                            if (expandido == carrito.id) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Expandir carrito"
                        )
                    }

                    if (expandido == carrito.id) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            carrito.productos.forEach { item ->
                                val titulo = AlmacenAplicacion.productos
                                    .firstOrNull { it.id == item.productoId }
                                    ?.titulo
                                    ?: "Producto #${item.productoId}"
                                Text("• $titulo — cantidad ${item.cantidad}")
                            }
                        }
                    }
                }
            }
        }
    }
}
