package com.example.equiposixseven

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/** Comprueba conectividad antes de iniciar una petición solicitada explícitamente por el usuario. */
fun hayConexionDisponible(context: Context): Boolean {
    val gestor = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val red = gestor.activeNetwork ?: return false
    val capacidades = gestor.getNetworkCapabilities(red) ?: return false

    return capacidades.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
        capacidades.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}
