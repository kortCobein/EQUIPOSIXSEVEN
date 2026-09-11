package com.example.equiposixseven

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Cliente HTTP pequeño y explícito para Fake Store API.
 *
 * Se usa HttpURLConnection para que el equipo pueda explicar el flujo completo sin depender
 * de una capa automática de Retrofit: URL, método, JSON, código HTTP, parseo y manejo de error.
 */
object ServicioApi {
    private const val BASE_URL = "https://fakestoreapi.com"

    /** US01: autentica el perfil seleccionado y devuelve una sesión con su token. */
    suspend fun iniciarSesion(perfil: PerfilDemo, clave: String): UsuarioSesion = withContext(Dispatchers.IO) {
        val cuerpo = JSONObject()
            .put("username", perfil.usuarioApi)
            .put("password", clave)

        val respuesta = solicitar("/auth/login", "POST", cuerpo)
        val token = JSONObject(respuesta).optString("token")
        if (token.isBlank()) throw ExcepcionApi("La API no devolvió un token de sesión válido.")

        UsuarioSesion(
            id = perfil.idUsuario,
            usuario = perfil.usuarioApi,
            rol = PerfilesDemo.rolPorId(perfil.idUsuario),
            token = token
        )
    }

    /** US03: obtiene el catálogo general. */
    suspend fun obtenerProductos(): List<Producto> = withContext(Dispatchers.IO) {
        val arreglo = JSONArray(solicitar("/products", "GET"))
        List(arreglo.length()) { indice -> productoDesdeJson(arreglo.getJSONObject(indice)) }
    }

    /** US04: obtiene las categorías remotas disponibles. */
    suspend fun obtenerCategorias(): List<String> = withContext(Dispatchers.IO) {
        val arreglo = JSONArray(solicitar("/products/categories", "GET"))
        List(arreglo.length()) { indice -> arreglo.optString(indice) }.filter { it.isNotBlank() }
    }

    /** US04: obtiene únicamente los productos de una categoría. */
    suspend fun obtenerProductosPorCategoria(categoria: String): List<Producto> = withContext(Dispatchers.IO) {
        val codificada = URLEncoder.encode(categoria, StandardCharsets.UTF_8.toString()).replace("+", "%20")
        val arreglo = JSONArray(solicitar("/products/category/$codificada", "GET"))
        List(arreglo.length()) { indice -> productoDesdeJson(arreglo.getJSONObject(indice)) }
    }

    /** US05: consulta el detalle remoto de un producto. */
    suspend fun obtenerProducto(id: Int): Producto = withContext(Dispatchers.IO) {
        productoDesdeJson(JSONObject(solicitar("/products/$id", "GET")))
    }

    /** US06: simula la creación remota y devuelve el objeto respondido por la API. */
    suspend fun crearProducto(producto: Producto, token: String): Producto = withContext(Dispatchers.IO) {
        val respuesta = JSONObject(solicitar("/products", "POST", productoAJson(producto), token))
        producto.copy(id = respuesta.optInt("id", producto.id))
    }

    /** US07: envía PUT y devuelve el producto actualizado para reflejarlo localmente. */
    suspend fun actualizarProducto(producto: Producto, token: String): Producto = withContext(Dispatchers.IO) {
        solicitar("/products/${producto.id}", "PUT", productoAJson(producto), token)
        producto
    }

    /** US08: envía DELETE para simular la eliminación en Fake Store API. */
    suspend fun eliminarProducto(id: Int, token: String) = withContext(Dispatchers.IO) {
        solicitar("/products/$id", "DELETE", token = token)
    }

    /**
     * US09: crea un carrito remoto con el estado local actual.
     * La API es de práctica y no persiste permanentemente el resultado.
     */
    suspend fun crearCarrito(
        usuarioId: Int,
        items: List<ItemCarrito>,
        token: String
    ): Int = withContext(Dispatchers.IO) {
        val cuerpo = carritoAJson(usuarioId, items)
        val respuesta = JSONObject(solicitar("/carts", "POST", cuerpo, token))
        respuesta.optInt("id", 1)
    }

    /** US10: simula PUT del carrito después de cambiar cantidades. */
    suspend fun actualizarCarrito(
        carritoId: Int,
        usuarioId: Int,
        items: List<ItemCarrito>,
        token: String
    ) = withContext(Dispatchers.IO) {
        solicitar("/carts/$carritoId", "PUT", carritoAJson(usuarioId, items), token)
    }

    /** US10: simula DELETE del carrito remoto al remover un artículo. */
    suspend fun eliminarCarrito(carritoId: Int, token: String) = withContext(Dispatchers.IO) {
        solicitar("/carts/$carritoId", "DELETE", token = token)
    }

    /** US11: descarga todos los usuarios incluyendo objetos anidados. */
    suspend fun obtenerUsuarios(): List<UsuarioRemoto> = withContext(Dispatchers.IO) {
        val arreglo = JSONArray(solicitar("/users", "GET"))
        List(arreglo.length()) { indice -> usuarioDesdeJson(arreglo.getJSONObject(indice)) }
    }

    /** US12: descarga el histórico global de carritos. */
    suspend fun obtenerCarritos(): List<CarritoRemoto> = withContext(Dispatchers.IO) {
        val arreglo = JSONArray(solicitar("/carts", "GET"))
        List(arreglo.length()) { indice -> carritoDesdeJson(arreglo.getJSONObject(indice)) }
    }

    /** Convierte un producto del dominio al JSON esperado por la API. */
    private fun productoAJson(producto: Producto): JSONObject = JSONObject()
        .put("title", producto.titulo)
        .put("price", producto.precio)
        .put("description", producto.descripcion)
        .put("image", producto.imagenUrl)
        .put("category", producto.categoria)

    /** Convierte el carrito local al formato de POST/PUT de Fake Store API. */
    private fun carritoAJson(usuarioId: Int, items: List<ItemCarrito>): JSONObject {
        val productos = JSONArray()
        items.forEach { item ->
            productos.put(
                JSONObject()
                    .put("productId", item.productoId)
                    .put("quantity", item.cantidad)
            )
        }

        return JSONObject()
            .put("userId", usuarioId)
            .put("date", "2026-09-10")
            .put("products", productos)
    }

    /** Mapea el JSON de producto al modelo de Kotlin. */
    private fun productoDesdeJson(json: JSONObject): Producto = Producto(
        id = json.optInt("id"),
        titulo = json.optString("title", "Producto sin título"),
        precio = json.optDouble("price", 0.0),
        descripcion = json.optString("description", "Sin descripción"),
        imagenUrl = json.optString("image"),
        categoria = json.optString("category", "Sin categoría")
    )

    /** Mapea la estructura completa y anidada de un usuario remoto. */
    private fun usuarioDesdeJson(json: JSONObject): UsuarioRemoto {
        val nombreJson = json.optJSONObject("name") ?: JSONObject()
        val direccionJson = json.optJSONObject("address") ?: JSONObject()
        val geoJson = direccionJson.optJSONObject("geolocation") ?: JSONObject()

        return UsuarioRemoto(
            id = json.optInt("id"),
            correo = json.optString("email"),
            usuario = json.optString("username"),
            nombre = NombreUsuario(
                nombre = nombreJson.optString("firstname"),
                apellido = nombreJson.optString("lastname")
            ),
            direccion = DireccionUsuario(
                ciudad = direccionJson.optString("city"),
                calle = direccionJson.optString("street"),
                numero = direccionJson.optInt("number"),
                codigoPostal = direccionJson.optString("zipcode"),
                geolocalizacion = Geolocalizacion(
                    latitud = geoJson.optString("lat"),
                    longitud = geoJson.optString("long")
                )
            ),
            telefono = json.optString("phone")
        )
    }

    /** Mapea un carrito remoto y su arreglo interno de productos. */
    private fun carritoDesdeJson(json: JSONObject): CarritoRemoto {
        val productosJson = json.optJSONArray("products") ?: JSONArray()
        val productos = List(productosJson.length()) { indice ->
            val item = productosJson.getJSONObject(indice)
            ProductoCarritoRemoto(
                productoId = item.optInt("productId"),
                cantidad = item.optInt("quantity")
            )
        }

        return CarritoRemoto(
            id = json.optInt("id"),
            usuarioId = json.optInt("userId"),
            fecha = json.optString("date"),
            productos = productos
        )
    }

    /**
     * Ejecuta una petición HTTP y centraliza timeouts, headers y errores.
     * Cualquier código fuera de 2xx se transforma en ExcepcionApi para la UI.
     */
    private fun solicitar(
        ruta: String,
        metodo: String,
        cuerpo: JSONObject? = null,
        token: String? = null
    ): String {
        val conexion = (URL("$BASE_URL$ruta").openConnection() as HttpURLConnection).apply {
            requestMethod = metodo
            connectTimeout = 10_000
            readTimeout = 15_000
            setRequestProperty("Accept", "application/json")
            setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            if (!token.isNullOrBlank()) setRequestProperty("Authorization", "Bearer $token")
            doInput = true
        }

        try {
            if (cuerpo != null) {
                conexion.doOutput = true
                conexion.outputStream.bufferedWriter(Charsets.UTF_8).use { escritor ->
                    escritor.write(cuerpo.toString())
                }
            }

            val codigo = conexion.responseCode
            val flujo = if (codigo in 200..299) conexion.inputStream else conexion.errorStream
            val texto = flujo?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()

            if (codigo !in 200..299) {
                val mensaje = when (codigo) {
                    400, 401 -> "Credenciales o datos inválidos. Verifica la información e intenta de nuevo."
                    404 -> "El recurso solicitado ya no está disponible."
                    in 500..599 -> "El servidor no pudo responder. Intenta nuevamente en unos momentos."
                    else -> "No fue posible completar la operación (HTTP $codigo)."
                }
                throw ExcepcionApi(mensaje)
            }

            return texto
        } catch (error: ExcepcionApi) {
            throw error
        } catch (error: Exception) {
            throw ExcepcionApi("No se pudo conectar con Fake Store API. Revisa tu conexión a internet.")
        } finally {
            conexion.disconnect()
        }
    }
}
