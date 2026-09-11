package com.example.equiposixseven

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

/**
 * Punto central de navegación y sesión de la aplicación.
 * No utiliza un back stack externo: al cerrar sesión se destruye el estado protegido y se vuelve al login.
 */
@Composable
fun AplicacionSixSeven() {
    val contexto = LocalContext.current
    val gestorSesion = remember { GestorSesion(contexto) }
    val snackbar = remember { SnackbarHostState() }
    val alcance = rememberCoroutineScope()

    var sesion by remember { mutableStateOf(gestorSesion.leer()) }
    var seccion by remember { mutableStateOf(SeccionAplicacion.CATALOGO) }
    var productoSeleccionado by remember { mutableStateOf<Int?>(null) }

    /** Muestra mensajes transitorios desde cualquier pantalla sin duplicar Snackbars. */
    val mostrarMensaje: (String) -> Unit = { mensaje ->
        alcance.launch { snackbar.showSnackbar(mensaje) }
    }

    if (sesion == null) {
        PantallaAcceso { nuevaSesion ->
            gestorSesion.guardar(nuevaSesion)
            sesion = nuevaSesion
            seccion = SeccionAplicacion.CATALOGO
            productoSeleccionado = null
        }
        return
    }

    val usuario = sesion!!

    Scaffold(
        topBar = {
            BarraSuperiorUT(
                sesion = usuario,
                onCerrarSesion = {
                    // US02: se elimina persistencia, memoria sensible y estado de navegación.
                    gestorSesion.limpiar()
                    AlmacenAplicacion.limpiarDatosSesion()
                    productoSeleccionado = null
                    seccion = SeccionAplicacion.CATALOGO
                    sesion = null
                }
            )
        },
        bottomBar = {
            if (productoSeleccionado == null) {
                BarraNavegacionUT(
                    sesion = usuario,
                    seccionActual = seccion,
                    onSeleccionar = { destino ->
                        seccion = destino
                        productoSeleccionado = null
                    }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { relleno ->
        val idProducto = productoSeleccionado
        if (idProducto != null) {
            PantallaDetalleProducto(
                productoId = idProducto,
                sesion = usuario,
                padding = relleno,
                onVolver = { productoSeleccionado = null },
                onProductoEliminado = {
                    productoSeleccionado = null
                    seccion = SeccionAplicacion.CATALOGO
                },
                onMensaje = mostrarMensaje
            )
        } else {
            when (seccion) {
                SeccionAplicacion.CATALOGO -> PantallaCatalogo(
                    padding = relleno,
                    onAbrirProducto = { productoSeleccionado = it },
                    onMensaje = mostrarMensaje
                )

                SeccionAplicacion.CARRITO -> PantallaCarrito(
                    padding = relleno,
                    sesion = usuario,
                    onExplorarCatalogo = { seccion = SeccionAplicacion.CATALOGO },
                    onMensaje = mostrarMensaje
                )

                SeccionAplicacion.INVENTARIO -> PantallaInventario(
                    padding = relleno,
                    sesion = usuario,
                    onAbrirProducto = { productoSeleccionado = it },
                    onMensaje = mostrarMensaje
                )

                SeccionAplicacion.AUDITORIA -> PantallaAuditoria(
                    padding = relleno,
                    sesion = usuario
                )
            }
        }
    }
}
