package com.rokt.networkhelper.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.rokt.networkhelper.BuildConfig
import com.rokt.networkhelper.model.ExperienceRequest
import com.rokt.networkhelper.model.IntegrationInfo
import com.rokt.networkhelper.model.NetworkExperienceRequest
import com.rokt.networkhelper.network.RoktApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

/**
 * Dependency Injection container at the application level.
 */
internal interface RoktNetworkHelper {
    suspend fun experience(roktTagId: String, experienceRequest: ExperienceRequest): Result<String>
    suspend fun postEvents(events: String): Result<Unit>
}

object RoktNetwork : RoktNetworkHelper {

    private const val BASE_URL = BuildConfig.BASE_URL // Update value in local.properties

    private var roktTagId: String? = null
    private lateinit var roktRepository: RoktRepository

    private val json by lazy {
        Json {
            ignoreUnknownKeys = true
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun initialize() {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .cache(null)
            .addInterceptor(RoktRequestInterceptor(roktTagId))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                json.asConverterFactory(requireNotNull("application/json".toMediaTypeOrNull())),
            )
            .build()

        val service = retrofit.create(RoktApiService::class.java)
        roktRepository = NetworkRoktRepository(service)
    }

    override suspend fun experience(roktTagId: String, experienceRequest: ExperienceRequest): Result<String> {
        if (this.roktTagId != roktTagId || !::roktRepository.isInitialized) {
            this.roktTagId = roktTagId
            initialize()
        }
        val decodedIntegrationInfo = runCatching {
            json.decodeFromString<IntegrationInfo>(experienceRequest.integrationInfo)
        }.onFailure {
            return Result.failure(IllegalArgumentException("Integration Info is not valid: ${it.message}"))
        }.getOrNull() ?: return Result.failure(IllegalArgumentException("Integration Info is not valid"))
        return roktRepository.experience(
            NetworkExperienceRequest(
                pageIdentifier = experienceRequest.pageIdentifier,
                attributes = experienceRequest.attributes,
                integration = decodedIntegrationInfo,
                sessionId = experienceRequest.sessionId,
            ),
        )
    }

    override suspend fun postEvents(events: String): Result<Unit> = withContext(Dispatchers.IO) {
        if (!::roktRepository.isInitialized) {
            return@withContext Result.failure(IllegalStateException("RoktRepository is not initialized"))
        }
        roktRepository.postEvents(events)
    }

    private class RoktRequestInterceptor(private val roktTagId: String?) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder()
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("rokt-pub-id", BuildConfig.ROKT_PUB_ID) // Update value in local.properties
                .addHeader("rokt-secret", BuildConfig.ROKT_SECRET) // Update value in local.properties
                .addHeader("rokt-tag-id", roktTagId ?: "")
                .addHeader(
                    "rokt-client-unique-id",
                    BuildConfig.ROKT_CLIENT_UNIQUE_ID,
                ) // For troubleshooting across Partner and Rokt systems
                .build()
            return chain.proceed(request)
        }
    }
}
