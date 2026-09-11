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
            descripcion = "Gestiona catálogo y consulta auditorías.",
            usuarioApi = "johnd",
            claveApi = "m38rmF$",
            rol = RolUsuario.ADMINISTRADOR
        ),
        PerfilDemo(
            idUsuario = 4,
            nombreVisible = "Cliente",
            descripcion = "Explora productos y administra su carrito.",
            usuarioApi = "donero",
            claveApi = "ewedon",
            rol = RolUsuario.CLIENTE
        ),
        PerfilDemo(
            idUsuario = 3,
            nombreVisible = "Auditor",
            descripcion = "Consulta usuarios y carritos en modo lectura.",
            usuarioApi = "kevinryan",
            claveApi = "kev02937@",
            rol = RolUsuario.AUDITOR
        )
    )

    /** Convierte un ID remoto al rol definido por las reglas del proyecto. */
    fun rolPorId(idUsuario: Int): RolUsuario = when (idUsuario) {
        1, 2 -> RolUsuario.ADMINISTRADOR
        3 -> RolUsuario.AUDITOR
        else -> RolUsuario.CLIENTE
    }
}
