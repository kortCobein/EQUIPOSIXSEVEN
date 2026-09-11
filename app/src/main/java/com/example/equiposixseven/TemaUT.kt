package com.example.equiposixseven

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Colores institucionales y acentos principales (Azul y Verde) */
val UTAzul = Color(0xFF00245A)
val UTAzulClaro = Color(0xFF1E5BB0)
val UTAzulCielo = Color(0xFF38BDF8)
val UTVerde = Color(0xFF009D81)
val UTVerdeClaro = Color(0xFFD9F4EE)
val UTVerdeNeon = Color(0xFF00D1A7)

// Colores Modo Claro
val UTFondo = Color(0xFFF4F7F9)
val UTSuperficie = Color(0xFFFFFFFF)
val UTSuperficieVariante = Color(0xFFE8EEF3)
val UTTexto = Color(0xFF0F172A)
val UTTextoSecundario = Color(0xFF475569)

// Colores Modo Oscuro (Azul medianoche profundo y pizarra)
val UTNocheFondo = Color(0xFF0A1128)
val UTNocheSuperficie = Color(0xFF121E36)
val UTNocheSuperficieVariante = Color(0xFF1B2A4A)
val UTNocheTexto = Color(0xFFF8FAFC)
val UTNocheTextoSecundario = Color(0xFF94A3B8)

private val ColoresUTClaro = lightColorScheme(
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
    surfaceVariant = UTSuperficieVariante,
    onSurfaceVariant = UTTextoSecundario,
    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

private val ColoresUTOscuro = darkColorScheme(
    primary = UTAzulCielo,
    onPrimary = Color(0xFF002F6C),
    primaryContainer = Color(0xFF0B3B78),
    onPrimaryContainer = Color(0xFFD6E4FF),
    secondary = UTVerdeNeon,
    onSecondary = Color(0xFF00382B),
    secondaryContainer = Color(0xFF005140),
    onSecondaryContainer = Color(0xFF70F8D5),
    background = UTNocheFondo,
    onBackground = UTNocheTexto,
    surface = UTNocheSuperficie,
    onSurface = UTNocheTexto,
    surfaceVariant = UTNocheSuperficieVariante,
    onSurfaceVariant = UTNocheTextoSecundario,
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

/** Aplica el tema institucional con soporte completo para modo oscuro interactivo. */
@Composable
fun TemaUT(
    modoOscuro: Boolean = false,
    contenido: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (modoOscuro) ColoresUTOscuro else ColoresUTClaro,
        typography = MaterialTheme.typography,
        content = contenido
    )
}
