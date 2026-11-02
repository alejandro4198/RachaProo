package com.example.rachapro.network

import com.example.rachapro.data.local.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val DEFAULT_BASE_URL = "http://10.0.2.2:8080/"

    fun create(
        sessionManager: SessionManager,
        baseUrl: String = DEFAULT_BASE_URL
    ): ApiService {
        val normalizedUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"

        val loggingInterceptor =
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

        val authInterceptor =
            AuthInterceptor(
                sessionManager = sessionManager
            )

        val httpClient =
            OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .connectTimeout(3, TimeUnit.SECONDS)
                .build()

        return Retrofit.Builder()
            .baseUrl(normalizedUrl)
            .client(httpClient)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(ApiService::class.java)
    }
}