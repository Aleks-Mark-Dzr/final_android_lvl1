package com.example.skillcinema.network

import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.io.IOException

class CustomHttpLoggingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Логируем запрос
        logRequest(request)

        // Выполняем запрос
        val response = chain.proceed(request)

        // Логируем ответ
        logResponse(response)

        return response
    }

    private fun logRequest(request: okhttp3.Request) {
        try {
            println("=== HTTP REQUEST ===")
            println("URL: ${request.url}")
            println("Method: ${request.method}")
            println("Headers: ${request.headers}")

            if (request.body != null) {
                val buffer = Buffer()
                request.body?.writeTo(buffer)
                println("Body: ${buffer.readUtf8()}")
            }
        } catch (e: IOException) {
            println("Error logging request: ${e.message}")
        }
    }

    private fun logResponse(response: Response) {
        try {
            println("=== HTTP RESPONSE ===")
            println("Code: ${response.code}")
            println("Message: ${response.message}")
            println("Headers: ${response.headers}")

            if (response.body != null) {
                val source = response.body?.source()
                source?.request(Long.MAX_VALUE) // Буферизуем весь ответ
                val buffer = source?.buffer
                println("Body: ${buffer?.clone()?.readUtf8()}")
            }
        } catch (e: IOException) {
            println("Error logging response: ${e.message}")
        }
    }
}