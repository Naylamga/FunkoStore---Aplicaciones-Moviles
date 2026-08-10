package com.example.funkostore_vistas

import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ---- Auth ----
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthData>>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<AuthData>>

    @GET("auth/profile")
    suspend fun getProfile(): Response<ApiResponse<ProfileData>>

    @PUT("auth/profile")
    suspend fun updateProfile(@Body body: Map<String, String>): Response<ApiResponse<ProfileData>>

    // ---- Productos ----
    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 100,
        @Query("search") search: String? = null,
        @Query("franquicia") franquicia: Int? = null,
        @Query("categoria") categoria: Int? = null
    ): Response<ApiListResponse<ProductoApi>>

    @GET("products/{id}")
    suspend fun getProduct(@Path("id") id: Int): Response<ApiResponse<ProductoApi>>

    @Multipart
    @POST("products")
    suspend fun createProduct(
        @Part("nombre_producto") nombreProducto: okhttp3.RequestBody,
        @Part("descripcion") descripcion: okhttp3.RequestBody?,
        @Part("precio") precio: okhttp3.RequestBody,
        @Part("stock") stock: okhttp3.RequestBody?,
        @Part("codigo_barra") codigoBarra: okhttp3.RequestBody?,
        @Part("id_franquicia") idFranquicia: okhttp3.RequestBody?,
        @Part("id_proveedor") idProveedor: okhttp3.RequestBody?,
        @Part imagenes: List<okhttp3.MultipartBody.Part>
    ): Response<ApiResponse<ProductoApi>>

    @Multipart
    @PUT("products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Int,
        @Part("nombre_producto") nombreProducto: okhttp3.RequestBody,
        @Part("descripcion") descripcion: okhttp3.RequestBody?,
        @Part("precio") precio: okhttp3.RequestBody,
        @Part("stock") stock: okhttp3.RequestBody?,
        @Part("codigo_barra") codigoBarra: okhttp3.RequestBody?,
        @Part("id_franquicia") idFranquicia: okhttp3.RequestBody?,
        @Part("id_proveedor") idProveedor: okhttp3.RequestBody?,
        @Part imagenes: List<okhttp3.MultipartBody.Part>
    ): Response<ApiResponse<ProductoApi>>

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): Response<ApiResponse<Unit>>

    // ---- Franquicias ----
    @GET("franchises")
    suspend fun getFranchises(): Response<ApiListResponse<FranquiciaData>>

    // ---- Proveedores ----
    @GET("providers")
    suspend fun getProviders(): Response<ApiListResponse<ProveedorData>>
}
