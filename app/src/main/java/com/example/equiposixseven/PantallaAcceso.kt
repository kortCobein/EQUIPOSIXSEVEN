package com.example.equiposixseven

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * US01 - Acceso simplificado para demostración.
 * El usuario elige un perfil y únicamente escribe la contraseña común: 1234.
 * Las credenciales reales de Fake Store API se usan internamente y nunca se muestran.
 */
@Composable
fun PantallaAcceso(onAccesoCorrecto: (UsuarioSesion) -> Unit) {
    val contexto = LocalContext.current
    val alcance = rememberCoroutineScope()

    var perfilSeleccionado by remember { mutableStateOf(PerfilesDemo.disponibles.first()) }
    var clave by remember { mutableStateOf("") }
    var mostrarClave by remember { mutableStateOf(false) }
    var cargando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Al no usar un stack de navegación, el gesto atrás desde login no revela pantallas protegidas.
    BackHandler(enabled = false) { }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "SIXSEVEN",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Black
        )
        Text(
            text = "Demo académica · estilo UT",
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Selecciona el perfil que deseas probar. Todos usan la contraseña 1234.",
            modifier = Modifier.padding(top = 10.dp, bottom = 20.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // 1) Selector visual: evita que el usuario tenga que memorizar usernames de prueba.
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
            Spacer(Modifier.height(10.dp))
        }

        // 2) Único dato manual del acceso: la contraseña común del demo.
        OutlinedTextField(
            value = clave,
            onValueChange = {
                clave = it
                error = null
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { mostrarClave = !mostrarClave }) {
                    Icon(
                        imageVector = if (mostrarClave) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (mostrarClave) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            },
            visualTransformation = if (mostrarClave) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            enabled = !cargando,
            isError = error != null
        )

        Text(
            text = "Contraseña de prueba: ${PerfilesDemo.CLAVE_ACCESO}",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        error?.let { mensaje ->
            Text(
                text = mensaje,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                color = MaterialTheme.colorScheme.error
            )
        }

        // 3) Primero se valida la contraseña común 1234 y después se autentica el perfil en la API.
        Button(
            onClick = {
                if (clave.isBlank()) {
                    error = "Escribe la contraseña del perfil seleccionado."
                    return@Button
                }
                if (clave != PerfilesDemo.CLAVE_ACCESO) {
                    error = "Contraseña incorrecta. Para la demo utiliza 1234."
                    return@Button
                }
                if (!hayConexionDisponible(contexto)) {
                    error = "No hay conexión a internet. Conéctate para validar el perfil."
                    return@Button
                }

                alcance.launch {
                    cargando = true
                    error = null
                    try {
                        // La clave real de Fake Store API permanece oculta para el usuario.
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
                .padding(top = 18.dp)
                .height(54.dp),
            enabled = !cargando,
            shape = RoundedCornerShape(16.dp)
        ) {
            if (cargando) {
                CircularProgressIndicator(
                    modifier = Modifier.height(22.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Entrar como ${perfilSeleccionado.nombreVisible}")
            }
        }
    }
}

/** Tarjeta accesible que reemplaza el campo manual de nombre de usuario. */
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
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = if (seleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(perfil.nombreVisible, fontWeight = FontWeight.Bold)
                Text(
                    perfil.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            RadioButton(selected = seleccionado, onClick = onSeleccionar)
        }
    }
}
