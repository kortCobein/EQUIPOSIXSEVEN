package com.example.equiposixseven

import android.content.Context

/**
 * Administra la sesión persistente del dispositivo.
 *
 * Para este proyecto académico se usa SharedPreferences porque permite explicar con claridad
 * cómo se conserva y elimina una sesión. La contraseña nunca se guarda; únicamente se almacena
 * el token devuelto por la API y los datos mínimos del perfil.
 */
class GestorSesion(context: Context) {
    private val preferencias = context.getSharedPreferences("sesion_sixseven", Context.MODE_PRIVATE)

    /** Guarda los datos mínimos necesarios para restaurar una sesión. */
    fun guardar(sesion: UsuarioSesion) {
        preferencias.edit()
            .putInt(CLAVE_ID, sesion.id)
            .putString(CLAVE_USUARIO, sesion.usuario)
            .putString(CLAVE_ROL, sesion.rol.name)
            .putString(CLAVE_TOKEN, sesion.token)
            .apply()
    }

    /** Recupera la sesión previa, o null cuando no existe una sesión válida. */
    fun leer(): UsuarioSesion? {
        val id = preferencias.getInt(CLAVE_ID, -1)
        val usuario = preferencias.getString(CLAVE_USUARIO, null)
        val rolTexto = preferencias.getString(CLAVE_ROL, null)
        val token = preferencias.getString(CLAVE_TOKEN, null)

        if (id < 0 || usuario.isNullOrBlank() || rolTexto.isNullOrBlank() || token.isNullOrBlank()) {
            return null
        }

        val rol = runCatching { RolUsuario.valueOf(rolTexto) }.getOrNull() ?: return null
        return UsuarioSesion(id = id, usuario = usuario, rol = rol, token = token)
    }

    /**
     * Borra de forma completa el token, ID y rol local para cumplir US02.
     * Después de esta llamada no queda información de autenticación persistida.
     */
    fun limpiar() {
        preferencias.edit().clear().apply()
    }

    private companion object {
        const val CLAVE_ID = "usuario_id"
        const val CLAVE_USUARIO = "usuario_nombre"
        const val CLAVE_ROL = "usuario_rol"
        const val CLAVE_TOKEN = "token_acceso"
    }
}
