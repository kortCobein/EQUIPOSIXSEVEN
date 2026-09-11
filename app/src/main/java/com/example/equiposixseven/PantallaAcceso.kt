package com.example.equiposixseven

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Pantalla de autenticación y selección de perfil.
 */
@Composable
fun PantallaAcceso(
    modoOscuro: Boolean = false,
    onAlternarModoOscuro: () -> Unit = {},
    onAccesoCorrecto: (UsuarioSesion) -> Unit
) {
    val contexto = LocalContext.current
    val alcance = rememberCoroutineScope()

    var perfilSeleccionado by remember { mutableStateOf(PerfilesDemo.disponibles.first()) }
    var clave by remember { mutableStateOf("") }
    var mostrarClave by remember { mutableStateOf(false) }
    var cargando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    BackHandler(enabled = false) { }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Fila superior con toggle de tema
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onAlternarModoOscuro) {
                Icon(
                    imageVector = if (modoOscuro) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = if (modoOscuro) "Modo claro" else "Modo oscuro",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text = "SIXSEVEN",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Black
        )

        Spacer(Modifier.height(24.dp))

        // Selector visual con fotos de perfil con IA
        PerfilesDemo.disponibles.forEach { perfil ->
            TarjetaPerfilDemo(
                perfil = perfil,
                seleccionado = perfilSeleccionado == perfil,
                onSeleccionar = {
                    perfilSeleccionado = perfil
                    clave = ""
                    error = null
                }
            )
            Spacer(Modifier.height(12.dp))
        }

        // Campo de contraseña
        OutlinedTextField(
            value = clave,
            onValueChange = {
                clave = it
                error = null
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { mostrarClave = !mostrarClave }) {
                    Icon(
                        imageVector = if (mostrarClave) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (mostrarClave) "Ocultar" else "Mostrar"
                    )
                }
            },
            visualTransformation = if (mostrarClave) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            enabled = !cargando,
            isError = error != null,
            shape = RoundedCornerShape(16.dp)
        )

        error?.let { mensaje ->
            Text(
                text = mensaje,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Button(
            onClick = {
                if (clave.isBlank()) {
                    error = "Ingresa tu contraseña."
                    return@Button
                }
                if (clave != PerfilesDemo.CLAVE_ACCESO) {
                    error = "Contraseña incorrecta."
                    return@Button
                }
                if (!hayConexionDisponible(contexto)) {
                    error = "Sin conexión a internet."
                    return@Button
                }

                alcance.launch {
                    cargando = true
                    error = null
                    try {
                        val sesion = ServicioApi.iniciarSesion(
                            perfilSeleccionado,
                            perfilSeleccionado.claveApi
                        )
                        onAccesoCorrecto(sesion)
                    } catch (e: Exception) {
                        error = e.message ?: "No fue posible iniciar sesión."
                    } finally {
                        cargando = false
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 22.dp)
                .height(54.dp),
            enabled = !cargando,
            shape = RoundedCornerShape(16.dp)
        ) {
            if (cargando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Ingresar"
                    )
                    Text("Ingresar", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/** Tarjeta de perfil con fotografía realista generada por IA. */
@Composable
private fun TarjetaPerfilDemo(
    perfil: PerfilDemo,
    seleccionado: Boolean,
    onSeleccionar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSeleccionar),
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionado) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (seleccionado) 4.dp else 1.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Image(
                painter = painterResource(perfil.avatarRes),
                contentDescription = perfil.nombreVisible,
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .border(
                        2.dp,
                        if (seleccionado) MaterialTheme.colorScheme.primary else Color.Transparent,
                        CircleShape
                    ),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = perfil.nombreVisible,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = perfil.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            RadioButton(selected = seleccionado, onClick = onSeleccionar)
        }
    }
}
