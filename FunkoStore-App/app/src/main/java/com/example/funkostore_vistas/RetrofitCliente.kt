package com.example.funkostore_vistas

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiServiceSimple {
    @POST
    fun enviarProducto(
        @Url endpoint: String,
        @Body producto: Producto
    ): Call<RespuestaApi>
}

object RetrofitCliente {
    fun crearServicio(): ApiServiceSimple {
        val retrofit = Retrofit.Builder()
            .baseUrl(ConfiguracionApi.obtenerUrlBase())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(ApiServiceSimple::class.java)
    }
}
