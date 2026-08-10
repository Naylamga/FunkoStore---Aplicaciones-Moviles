package com.example.funkostore_vistas

import android.content.Context
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

object ProductoStore {

    private val api get() = ApiClient.apiService

    suspend fun obtenerProductosActivos(): Result<List<Producto>> {
        return try {
            val response = api.getProducts(limit = 100)
            if (response.isSuccessful && response.body()?.success == true) {
                val productosApi = response.body()?.data ?: emptyList()
                val productos = productosApi.map { it.toProducto() }
                Result.success(productos)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Error al obtener productos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerProductoPorId(id: Int): Result<ProductoApi?> {
        return try {
            val response = api.getProduct(id)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.data)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Error al obtener producto"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerFranquicias(): Result<List<FranquiciaData>> {
        return try {
            val response = api.getFranchises()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.data ?: emptyList())
            } else {
                Result.failure(Exception("Error al obtener franquicias"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerProveedores(): Result<List<ProveedorData>> {
        return try {
            val response = api.getProviders()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.data ?: emptyList())
            } else {
                Result.failure(Exception("Error al obtener proveedores"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun crearProducto(
        nombre: String,
        descripcion: String,
        precio: Double,
        stock: Int,
        codigoBarra: String,
        idFranquicia: Int,
        idProveedor: Int,
        imagenPath: String?
    ): Result<ProductoApi> {
        return try {
            val nombrePart = nombre.toRequestBody("text/plain".toMediaTypeOrNull())
            val descripcionPart = descripcion.ifEmpty { null }
                ?.toRequestBody("text/plain".toMediaTypeOrNull())
            val precioPart = precio.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val stockPart = stock.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val codigoPart = codigoBarra.ifEmpty { null }
                ?.toRequestBody("text/plain".toMediaTypeOrNull())
            val franquiciaPart = idFranquicia.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val proveedorPart = idProveedor.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val imagenesPart = if (imagenPath != null && File(imagenPath).exists()) {
                val file = File(imagenPath)
                val requestBody = file.readBytes()
                    .toRequestBody("image/jpeg".toMediaTypeOrNull())
                listOf(MultipartBody.Part.createFormData("imagenes", file.name, requestBody))
            } else {
                emptyList()
            }

            val response = api.createProduct(
                nombreProducto = nombrePart,
                descripcion = descripcionPart,
                precio = precioPart,
                stock = stockPart,
                codigoBarra = codigoPart,
                idFranquicia = franquiciaPart,
                idProveedor = proveedorPart,
                imagenes = imagenesPart
            )
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Error al crear producto"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarProducto(
        id: Int,
        nombre: String,
        descripcion: String,
        precio: Double,
        stock: Int,
        codigoBarra: String,
        idFranquicia: Int,
        idProveedor: Int,
        imagenPath: String?
    ): Result<ProductoApi> {
        return try {
            val nombrePart = nombre.toRequestBody("text/plain".toMediaTypeOrNull())
            val descripcionPart = descripcion.ifEmpty { null }
                ?.toRequestBody("text/plain".toMediaTypeOrNull())
            val precioPart = precio.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val stockPart = stock.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val codigoPart = codigoBarra.ifEmpty { null }
                ?.toRequestBody("text/plain".toMediaTypeOrNull())
            val franquiciaPart = idFranquicia.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val proveedorPart = idProveedor.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val imagenesPart = if (imagenPath != null && File(imagenPath).exists()) {
                val file = File(imagenPath)
                val requestBody = file.readBytes()
                    .toRequestBody("image/jpeg".toMediaTypeOrNull())
                listOf(MultipartBody.Part.createFormData("imagenes", file.name, requestBody))
            } else {
                emptyList()
            }

            val response = api.updateProduct(
                id = id,
                nombreProducto = nombrePart,
                descripcion = descripcionPart,
                precio = precioPart,
                stock = stockPart,
                codigoBarra = codigoPart,
                idFranquicia = franquiciaPart,
                idProveedor = proveedorPart,
                imagenes = imagenesPart
            )
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Error al actualizar producto"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun darDeBajaProducto(id: Int): Result<Unit> {
        return try {
            val response = api.deleteProduct(id)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Error al eliminar producto"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun ProductoApi.toProducto(): Producto {
        return Producto(
            id = idProducto,
            nombre = nombreProducto,
            precio = precio,
            stock = stock,
            franquicia = nombreFranquicia ?: "",
            proveedor = nombreProveedor ?: "",
            urlImagen = urlImagen
        )
    }
}

data class Producto(
    val id: Int,
    val nombre: String,
    val precio: Double,
    val stock: Int,
    val franquicia: String,
    val proveedor: String,
    val urlImagen: String
)

data class OpcionSpinner(val id: Int, val nombre: String)
