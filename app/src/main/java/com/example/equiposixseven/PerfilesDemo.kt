package com.example.equiposixseven

/**
 * Perfiles de prueba oficiales de la aplicación.
 *
 * Para la demostración, TODOS los perfiles usan la misma contraseña visible: 1234.
 * Las credenciales reales de Fake Store API permanecen encapsuladas aquí y solo se usan
 * internamente para obtener un token; el usuario nunca tiene que escribirlas.
 *
 * Reglas de rol solicitadas por US01:
 * - IDs 1 y 2 -> Administrador.
 * - ID 3 -> Auditor.
 * - IDs restantes -> Cliente.
 */
object PerfilesDemo {
    /** Contraseña única y sencilla que se escribe en la pantalla de acceso. */
    const val CLAVE_ACCESO = "1234"

    val disponibles = listOf(
        PerfilDemo(
            idUsuario = 1,
            nombreVisible = "Administrador",
            descripcion = "Gestiona catálogo y auditorías",
            usuarioApi = "johnd",
            claveApi = "m38rmF$",
            rol = RolUsuario.ADMINISTRADOR,
            avatarRes = R.drawable.avatar_admin
        ),
        PerfilDemo(
            idUsuario = 4,
            nombreVisible = "Cliente",
            descripcion = "Explora y compra en el catálogo",
            usuarioApi = "donero",
            claveApi = "ewedon",
            rol = RolUsuario.CLIENTE,
            avatarRes = R.drawable.avatar_cliente
        ),
        PerfilDemo(
            idUsuario = 3,
            nombreVisible = "Auditor",
            descripcion = "Monitorea usuarios y carritos",
            usuarioApi = "kevinryan",
            claveApi = "kev02937@",
            rol = RolUsuario.AUDITOR,
            avatarRes = R.drawable.avatar_auditor
        )
    )

    /** Convierte un ID remoto al rol definido por las reglas del proyecto. */
    fun rolPorId(idUsuario: Int): RolUsuario = when (idUsuario) {
        1, 2 -> RolUsuario.ADMINISTRADOR
        3 -> RolUsuario.AUDITOR
        else -> RolUsuario.CLIENTE
    }

    /** Retorna el avatar nativo generado por IA según el rol. */
    fun avatarPorRol(rol: RolUsuario): Int = when (rol) {
        RolUsuario.ADMINISTRADOR -> R.drawable.avatar_admin
        RolUsuario.CLIENTE -> R.drawable.avatar_cliente
        RolUsuario.AUDITOR -> R.drawable.avatar_auditor
    }

    /** Retorna el avatar nativo generado por IA según el ID del usuario. */
    fun avatarPorId(idUsuario: Int): Int = avatarPorRol(rolPorId(idUsuario))
}
