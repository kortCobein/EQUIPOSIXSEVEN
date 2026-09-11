plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.equiposixseven"
    compileSdk {
        version = release(35)
    }

    defaultConfig {
        applicationId = "com.example.equiposixseven"
        minSdk = 24
        targetSdk = 35
        versionCode = 2
        versionName = "2.0-ut"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // BOM de Compose: mantiene compatibles las versiones de UI, Foundation e iconos.
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Componentes usados por la nueva UX/UI.
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material:material-icons-extended")

    // Splash Screen nativa compatible desde Android 12 y versiones anteriores.
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Carga asíncrona de las imágenes remotas del catálogo.
    implementation("io.coil-kt:coil-compose:2.7.0")

    debugImplementation(libs.androidx.compose.ui.tooling)
}
