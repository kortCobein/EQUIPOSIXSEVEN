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
import androidx.compose.ui.graphics.Color
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

    var modoOscuro by remember { mutableStateOf(false) }
    var sesion by remember { mutableStateOf(gestorSesion.leer()) }
    var seccion by remember { mutableStateOf(SeccionAplicacion.CATALOGO) }
    var productoSeleccionado by remember { mutableStateOf<Int?>(null) }

    /** Muestra mensajes transitorios desde cualquier pantalla sin duplicar Snackbars. */
    val mostrarMensaje: (String) -> Unit = { mensaje ->
        alcance.launch { snackbar.showSnackbar(mensaje) }
    }

    TemaUT(modoOscuro = modoOscuro) {
        FondoCuadriculaTecnologica(esOscuro = modoOscuro) {
            if (sesion == null) {
                PantallaAcceso(
                    modoOscuro = modoOscuro,
                    onAlternarModoOscuro = { modoOscuro = !modoOscuro },
                    onAccesoCorrecto = { nuevaSesion ->
                        gestorSesion.guardar(nuevaSesion)
                        sesion = nuevaSesion
                        seccion = SeccionAplicacion.CATALOGO
                        productoSeleccionado = null
                    }
                )
            } else {
                val usuario = sesion!!

                Scaffold(
                    containerColor = Color.Transparent,
                    topBar = {
                        BarraSuperiorUT(
                            sesion = usuario,
                            modoOscuro = modoOscuro,
                            onAlternarModoOscuro = { modoOscuro = !modoOscuro },
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
                            BarraNavegacionFlotanteUT(
                                sesion = usuario,
                                seccionActual = seccion,
                                modoOscuro = modoOscuro,
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
        }
    }
}
