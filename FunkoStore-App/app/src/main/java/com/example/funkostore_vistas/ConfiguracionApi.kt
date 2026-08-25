package com.example.funkostore_vistas

object ConfiguracionApi {
    var ip: String = "10.0.2.2"
    var puerto: String = "3000"
    /** Prefijo de la API, ej: "api" → http://ip:puerto/api/ */
    var endpoint: String = "api"

    fun obtenerUrlBase(): String {
        val path = endpoint.trim().trim('/')
        return if (path.isEmpty()) {
            "http://$ip:$puerto/"
        } else {
            "http://$ip:$puerto/$path/"
        }
    }

    fun obtenerUrlSocket(): String {
        return "http://$ip:$puerto"
    }

    fun configuracionCompleta(): Boolean {
        return ip.isNotBlank() && puerto.isNotBlank()
    }
}
