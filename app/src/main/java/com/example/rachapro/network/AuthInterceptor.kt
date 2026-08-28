package com.example.rachapro.network

import com.example.rachapro.data.local.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(
        chain: Interceptor.Chain
    ): Response {

        val originalRequest =
            chain.request()

        val path =
            originalRequest.url.encodedPath

        val isPublicEndpoint =
            path == "/api/auth/login" ||
                    (
                            path == "/api/users" &&
                                    originalRequest.method == "POST"
                            )

        if (isPublicEndpoint) {
            return chain.proceed(originalRequest)
        }

        val token =
            runBlocking {
                sessionManager
                    .sessionState
                    .first()
                    .authToken
            }

        val request =
            if (token.isNullOrBlank()) {

                originalRequest

            } else {

                originalRequest
                    .newBuilder()
                    .header(
                        "Authorization",
                        "Bearer $token"
                    )
                    .build()
            }

        val response =
            chain.proceed(request)

        if (response.code == 401) {

            runBlocking {
                sessionManager.clearSession()
            }
        }

        return response
    }
}