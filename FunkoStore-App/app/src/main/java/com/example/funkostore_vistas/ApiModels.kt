package com.example.funkostore_vistas

import com.google.gson.annotations.SerializedName

// ---- Responses genéricos ----
data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null
)

data class ApiListResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: List<T>? = null,
    val total: Int? = null,
    val page: Int? = null,
    val limit: Int? = null,
    val totalPages: Int? = null
)

// ---- Auth ----
data class LoginRequest(
    val email: String,
    @SerializedName("contraseña") val contrasena: String
)

data class RegisterRequest(
    val nombre: String,
    val apellido: String,
    val email: String,
    @SerializedName("contraseña") val contrasena: String,
    val telefono: String? = null,
    val direccion: String? = null
)

data class AuthData(
    val token: String? = null,
    val usuario: UsuarioData? = null,
    @SerializedName("requiereVerificacionFacial") val requiereVerificacionFacial: Boolean? = null
)

data class UsuarioData(
    val id: Int? = null,
    val nombre: String? = null,
    val apellido: String? = null,
    val email: String? = null,
    val rol: String? = null
)

data class ProfileData(
    @SerializedName("id_usuario") val idUsuario: Int? = null,
    val nombre: String? = null,
    val apellido: String? = null,
    val email: String? = null,
    val telefono: String? = null,
    val direccion: String? = null,
    @SerializedName("nombre_rol") val nombreRol: String? = null
)

// ---- Productos ----
data class ProductoApi(
    @SerializedName("id_producto") val idProducto: Int,
    @SerializedName("nombre_producto") val nombreProducto: String,
    val descripcion: String? = null,
    val precio: Double,
    val stock: Int,
    @SerializedName("codigo_barra") val codigoBarra: String? = null,
    @SerializedName("id_franquicia") val idFranquicia: Int? = null,
    @SerializedName("id_proveedor") val idProveedor: Int? = null,
    val estado: Boolean = true,
    @SerializedName("nombre_franquicia") val nombreFranquicia: String? = null,
    @SerializedName("nombre_categoria") val nombreCategoria: String? = null,
    @SerializedName("nombre_proveedor") val nombreProveedor: String? = null,
    val imagenes: List<ImagenData>? = null
) {
    val urlImagen: String
        get() = imagenes?.find { it.principal }?.urlImagen
            ?: imagenes?.firstOrNull()?.urlImagen
            ?: ""

    val nombre: String get() = nombreProducto
    val franquicia: String get() = nombreFranquicia ?: ""
    val proveedor: String get() = nombreProveedor ?: ""
}

data class ImagenData(
    @SerializedName("id_imagen") val idImagen: Int? = null,
    @SerializedName("id_producto") val idProducto: Int? = null,
    @SerializedName("url_imagen") val urlImagen: String,
    val principal: Boolean = false,
    @SerializedName("orden_imagen") val ordenImagen: Int? = null
)

data class ProductoCreateRequest(
    @SerializedName("nombre_producto") val nombreProducto: String,
    val descripcion: String? = null,
    val precio: Double,
    val stock: Int? = 0,
    @SerializedName("codigo_barra") val codigoBarra: String? = null,
    @SerializedName("id_franquicia") val idFranquicia: Int? = null,
    @SerializedName("id_proveedor") val idProveedor: Int? = null
)

// ---- Franquicias ----
data class FranquiciaData(
    @SerializedName("id_franquicia") val idFranquicia: Int,
    @SerializedName("nombre_franquicia") val nombreFranquicia: String,
    val descripcion: String? = null
)

// ---- Proveedores ----
data class ProveedorData(
    @SerializedName("id_proveedor") val idProveedor: Int,
    @SerializedName("nombre_proveedor") val nombreProveedor: String,
    val cuit: String? = null,
    val telefono: String? = null,
    val email: String? = null
)
