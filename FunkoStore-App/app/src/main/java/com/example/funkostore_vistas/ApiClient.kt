package com.example.funkostore_vistas

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private var token: String? = null
    private var retrofit: Retrofit? = null
    private var cachedService: ApiService? = null

    fun setToken(jwt: String?) {
        token = jwt
    }

    fun resetClient() {
        retrofit = null
        cachedService = null
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private fun buildClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val builder = original.newBuilder()
                token?.let {
                    builder.addHeader("Authorization", "Bearer $it")
                }
                chain.proceed(builder.build())
            }
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    val apiService: ApiService
        get() {
            if (cachedService == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(ConfiguracionApi.obtenerUrlBase())
                    .client(buildClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                cachedService = retrofit!!.create(ApiService::class.java)
            }
            return cachedService!!
        }
}
