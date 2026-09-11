package com.example.equiposixseven

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Colores institucionales usados como base visual del proyecto. */
val UTAzul = Color(0xFF00245A)
val UTVerde = Color(0xFF009D81)
val UTVerdeClaro = Color(0xFFD9F4EE)
val UTFondo = Color(0xFFF3F7F9)
val UTSuperficie = Color(0xFFFFFFFF)
val UTTexto = Color(0xFF17212B)
val UTTextoSecundario = Color(0xFF5E6B78)

/**
 * Esquema Material 3 inspirado en la identidad UT: azul profundo, verde institucional,
 * superficies limpias y contraste alto para conservar una apariencia académica/profesional.
 */
private val ColoresUT = lightColorScheme(
    primary = UTAzul,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCE8FF),
    onPrimaryContainer = UTAzul,
    secondary = UTVerde,
    onSecondary = Color.White,
    secondaryContainer = UTVerdeClaro,
    onSecondaryContainer = Color(0xFF00382E),
    background = UTFondo,
    onBackground = UTTexto,
    surface = UTSuperficie,
    onSurface = UTTexto,
    surfaceVariant = Color(0xFFE6EDF1),
    onSurfaceVariant = UTTextoSecundario,
    error = Color(0xFFB3261E)
)

/** Aplica el tema institucional a toda la jerarquía Compose. */
@Composable
fun TemaUT(contenido: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColoresUT,
        typography = MaterialTheme.typography,
        content = contenido
    )
}
