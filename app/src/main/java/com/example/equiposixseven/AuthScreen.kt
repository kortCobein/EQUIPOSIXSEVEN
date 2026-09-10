package com.example.equiposixseven

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

/** US01 - Login y asignación local de perfiles. */
@Composable
fun LoginScreen(onLogin: (AppUser) -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Iniciar sesión", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                error = null
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            label = { Text("Usuario") },
            singleLine = true
        )

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                error = null
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        error?.let {
            Text(
                text = it,
                modifier = Modifier.padding(top = 8.dp),
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = {
                val user = AppStore.login(username, password)
                if (user == null) {
                    error = "Usuario o contraseña incorrectos"
                } else {
                    // El perfil local queda asignado según el usuario autenticado.
                    onLogin(user)
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Entrar")
        }
    }
}
