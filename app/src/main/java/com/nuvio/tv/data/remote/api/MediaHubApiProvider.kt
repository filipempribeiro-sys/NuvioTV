package com.nuvio.tv.data.remote.api

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Single authenticated MEDIA•HUB API entry point for the Android application.
 *
 * The provider deliberately reads the current access token for every request,
 * instead of capturing it when Retrofit is created. This keeps a long-lived
 * client valid after QR sign-in, token refresh or sign-out without rebuilding
 * the networking graph.
 */
object MediaHubApiProvider {

    private const val DEFAULT_BASE_URL = "https://media-hub-api.onrender.com/"

    @Volatile
    private var api: MediaHubApi? = null

    fun get(context: Context): MediaHubApi {
        return api ?: synchronized(this) {
            api ?: create(context.applicationContext).also { api = it }
        }
    }

    private fun create(context: Context): MediaHubApi {
        val sessionStore = MediaHubSessionStore(context)

        val authInterceptor = Interceptor { chain ->
            val token = sessionStore.getAccessToken()
            val request = chain.request().newBuilder().apply {
                if (!token.isNullOrBlank()) {
                    header("Authorization", "Bearer $token")
                }
                header("Accept", "application/json")
            }.build()
            chain.proceed(request)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(DEFAULT_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MediaHubApi::class.java)
    }
}
