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

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource

/** Encabezado reutilizable con avatar con IA, toggle de tema oscuro y acceso a cerrar sesión. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperiorUT(
    sesion: UsuarioSesion,
    modoOscuro: Boolean,
    onAlternarModoOscuro: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val avatarRes = PerfilesDemo.avatarPorRol(sesion.rol)

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Image(
                    painter = painterResource(avatarRes),
                    contentDescription = sesion.usuario,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, MaterialTheme.colorScheme.secondary, CircleShape),
                    contentScale = ContentScale.Crop
                )
                Column {
                    Text(
                        text = "SixSeven",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${nombreRol(sesion.rol)} · ${sesion.usuario}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onAlternarModoOscuro) {
                Icon(
                    imageVector = if (modoOscuro) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = if (modoOscuro) "Modo claro" else "Modo oscuro",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
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

/**
 * Fondo con cuadrícula tecnológica, matriz de puntos y cruces en intersecciones
 * inspirado en el diseño del sistema visual de Portal UT.
 */
@Composable
fun FondoCuadriculaTecnologica(
    modifier: Modifier = Modifier,
    esOscuro: Boolean = false,
    contenido: @Composable BoxScope.() -> Unit
) {
    val density = LocalDensity.current
    val colorLinea = if (esOscuro) Color(0xFF00D1A7) else Color(0xFF00245A)
    val colorPunto = if (esOscuro) Color(0xFF38BDF8) else Color(0xFF009D81)
    val colorFondo = if (esOscuro) Color(0xFF0A1128) else Color(0xFFF4F7F9)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorFondo)
            .drawBehind {
                val ladoCelda = with(density) { 56.dp.toPx() }
                val radioPunto = with(density) { 1.1.dp.toPx() }
                val semiLargoCruz = with(density) { 3.5.dp.toPx() }
                val puntosPorEje = 4

                val lineaColor = colorLinea.copy(alpha = if (esOscuro) 0.08f else 0.07f)
                val puntoColor = colorPunto.copy(alpha = if (esOscuro) 0.24f else 0.16f)
                val cruzColor = colorLinea.copy(alpha = if (esOscuro) 0.16f else 0.14f)

                val columnas = (size.width / ladoCelda).toInt() + 1
                val filas = (size.height / ladoCelda).toInt() + 1

                // 1. Líneas de la cuadrícula
                for (c in 0..columnas) {
                    val x = c * ladoCelda
                    drawLine(lineaColor, Offset(x, 0f), Offset(x, size.height), 1f)
                }
                for (f in 0..filas) {
                    val y = f * ladoCelda
                    drawLine(lineaColor, Offset(0f, y), Offset(size.width, y), 1f)
                }

                // 2. Matriz de puntos dentro de cada celda
                for (f in 0 until filas) {
                    val origenY = f * ladoCelda
                    for (c in 0 until columnas) {
                        val origenX = c * ladoCelda
                        for (py in 1..puntosPorEje) {
                            val y = origenY + (ladoCelda * py / (puntosPorEje + 1))
                            if (y >= size.height) continue
                            for (px in 1..puntosPorEje) {
                                val x = origenX + (ladoCelda * px / (puntosPorEje + 1))
                                if (x >= size.width) continue
                                drawCircle(puntoColor, radioPunto, Offset(x, y))
                            }
                        }
                    }
                }

                // 3. Cruces técnicas en las intersecciones
                for (f in 0..filas) {
                    val y = f * ladoCelda
                    for (c in 0..columnas) {
                        val x = c * ladoCelda
                        drawLine(cruzColor, Offset(x - semiLargoCruz, y), Offset(x + semiLargoCruz, y), 1.5f)
                        drawLine(cruzColor, Offset(x, y - semiLargoCruz), Offset(x, y + semiLargoCruz), 1.5f)
                    }
                }
            }
    ) {
        contenido()
    }
}

/** Barra inferior flotante en cápsula con selector deslizante animado estilo Portal UT. */
@Composable
fun BarraNavegacionFlotanteUT(
    sesion: UsuarioSesion,
    seccionActual: SeccionAplicacion,
    modoOscuro: Boolean,
    onSeleccionar: (SeccionAplicacion) -> Unit
) {
    val opciones = remember(sesion.rol) {
        buildList {
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
    }

    val totalElementos = opciones.size
    val indiceActual = opciones.indexOfFirst { it.seccion == seccionActual }.coerceAtLeast(0)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(30.dp),
                    spotColor = Color(0xFF00245A).copy(alpha = 0.28f),
                    ambientColor = Color(0xFF00245A).copy(alpha = 0.12f)
                )
                .clip(RoundedCornerShape(30.dp))
                .background(if (modoOscuro) Color(0xFF101C33) else Color.White)
                .border(
                    width = 1.dp,
                    color = if (modoOscuro) Color(0xFF1E2F52) else Color(0xFFE2E8F0),
                    shape = RoundedCornerShape(30.dp)
                )
                .padding(4.dp)
        ) {
            val anchoPestana = maxWidth / totalElementos
            val desplazamientoAnimado by animateDpAsState(
                targetValue = anchoPestana * indiceActual,
                animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
                label = "nav_slide"
            )

            // Pastilla indicadora deslizante con animación suave
            Box(
                modifier = Modifier
                    .offset(x = desplazamientoAnimado)
                    .width(anchoPestana)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        if (modoOscuro) Color(0xFF00D1A7) else Color(0xFF00245A)
                    )
            )

            // Íconos interactivos
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                opciones.forEach { opcion ->
                    val seleccionado = opcion.seccion == seccionActual
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { onSeleccionar(opcion.seccion) }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = opcion.icono,
                            contentDescription = opcion.etiqueta,
                            tint = when {
                                seleccionado && modoOscuro -> Color(0xFF0A1128)
                                seleccionado && !modoOscuro -> Color.White
                                modoOscuro -> Color(0xFF94A3B8)
                                else -> Color(0xFF64748B)
                            },
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
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
