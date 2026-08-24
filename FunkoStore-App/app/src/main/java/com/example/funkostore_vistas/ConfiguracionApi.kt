package com.example.funkostore_vistas

data class Producto(
    val producto: String,
    val marca: String,
    val descripcion: String
)

data class RespuestaApi(
    val mensaje: String? = null,
    val error: String? = null
)

object ConfiguracionApi {
    var ip: String = ""
    var puerto: String = ""
    var endpoint: String = ""

    fun obtenerUrlBase(): String {
        return "http://$ip:$puerto/"
    }

    fun configuracionCompleta(): Boolean {
        return ip.isNotBlank() &&
            puerto.isNotBlank() &&
            endpoint.isNotBlank()
    }
}
