package com.example.equiposixseven

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** Encabezado reutilizable con identidad visual UT y acceso rápido a cerrar sesión. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperiorUT(
    sesion: UsuarioSesion,
    onCerrarSesion: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Equipo SixSeven",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${nombreRol(sesion.rol)} · ${sesion.usuario}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.82f)
                )
            }
        },
        actions = {
            IconButton(onClick = onCerrarSesion) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = "Cerrar sesión",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

/** Barra inferior que cambia automáticamente según los permisos del rol activo. */
@Composable
fun BarraNavegacionUT(
    sesion: UsuarioSesion,
    seccionActual: SeccionAplicacion,
    onSeleccionar: (SeccionAplicacion) -> Unit
) {
    val opciones = buildList {
        add(OpcionNavegacion("Catálogo", Icons.Default.Home, SeccionAplicacion.CATALOGO))
        if (sesion.rol != RolUsuario.AUDITOR) {
            add(OpcionNavegacion("Carrito", Icons.Default.ShoppingCart, SeccionAplicacion.CARRITO))
        }
        if (sesion.rol == RolUsuario.ADMINISTRADOR) {
            add(OpcionNavegacion("Inventario", Icons.Default.AdminPanelSettings, SeccionAplicacion.INVENTARIO))
        }
        if (sesion.rol == RolUsuario.ADMINISTRADOR || sesion.rol == RolUsuario.AUDITOR) {
            add(OpcionNavegacion("Auditoría", Icons.Default.Assessment, SeccionAplicacion.AUDITORIA))
        }
    }

    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        opciones.forEach { opcion ->
            NavigationBarItem(
                selected = seccionActual == opcion.seccion,
                onClick = { onSeleccionar(opcion.seccion) },
                icon = { Icon(opcion.icono, contentDescription = opcion.etiqueta) },
                label = { Text(opcion.etiqueta) }
            )
        }
    }
}

/** Modelo interno de una opción de navegación. */
private data class OpcionNavegacion(
    val etiqueta: String,
    val icono: ImageVector,
    val seccion: SeccionAplicacion
)

/** Indicador de carga consistente para todas las peticiones de red. */
@Composable
fun EstadoCargando(mensaje: String = "Cargando información…") {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
        Spacer(Modifier.height(12.dp))
        Text(mensaje, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

/** Estado de error reutilizable con acción explícita de reintento. */
@Composable
fun EstadoError(
    mensaje: String,
    onReintentar: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "No pudimos completar la operación",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                text = mensaje,
                modifier = Modifier.padding(top = 6.dp),
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            if (onReintentar != null) {
                Button(
                    onClick = onReintentar,
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Text("Reintentar")
                }
            }
        }
    }
}

/** Tarjeta breve usada para estados vacíos o explicaciones importantes. */
@Composable
fun EstadoVacio(
    titulo: String,
    descripcion: String,
    accion: String? = null,
    onAccion: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("67", color = MaterialTheme.colorScheme.onSecondary, fontWeight = FontWeight.Black)
            }
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp)
            )
            Text(
                text = descripcion,
                modifier = Modifier.padding(top = 6.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (accion != null && onAccion != null) {
                Button(onClick = onAccion, modifier = Modifier.padding(top = 14.dp)) {
                    Text(accion)
                }
            }
        }
    }
}

/** Devuelve una etiqueta legible en español para el rol. */
fun nombreRol(rol: RolUsuario): String = when (rol) {
    RolUsuario.CLIENTE -> "Cliente"
    RolUsuario.ADMINISTRADOR -> "Administrador"
    RolUsuario.AUDITOR -> "Auditor"
}
