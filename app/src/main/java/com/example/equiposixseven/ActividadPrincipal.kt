package com.example.equiposixseven

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

/**
 * Activity única de la aplicación.
 * Instala la Splash Screen nativa y entrega el control de UI a Jetpack Compose.
 */
class ActividadPrincipal : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            TemaUT {
                AplicacionSixSeven()
            }
        }
    }
}
