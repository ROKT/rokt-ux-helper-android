package com.rokt.networkhelper.data

import android.os.Build
import com.rokt.networkhelper.BuildConfig
import com.rokt.networkhelper.model.ExperienceRequest
import com.rokt.networkhelper.model.NetworkOffersRequest
import com.rokt.networkhelper.model.OffersChannel
import com.rokt.networkhelper.model.OffersPage
import com.rokt.networkhelper.network.RoktApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.Locale
import java.util.concurrent.TimeUnit

private const val CHANNEL_TYPE_S2S = "s2s"
private const val OS_TYPE_ANDROID = "Android"
private const val PLATFORM_TYPE_MOBILE = "Mobile"

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
            .addInterceptor(RoktRequestInterceptor())
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
        return roktRepository.offers(
            NetworkOffersRequest(
                channel = OffersChannel(type = CHANNEL_TYPE_S2S),
                page = OffersPage(
                    pageIdentifier = experienceRequest.pageIdentifier,
                    packageName = experienceRequest.packageName,
                ),
                attributes = experienceRequest.attributes,
            ),
        )
    }

    override suspend fun postEvents(events: String): Result<Unit> = withContext(Dispatchers.IO) {
        if (!::roktRepository.isInitialized) {
            return@withContext Result.failure(IllegalStateException("RoktRepository is not initialized"))
        }
        // The RoktUXHelper payload is already in the v2/sessions/events shape.
        roktRepository.postEvents(events)
    }

    /**
     * Adds the `v2/sessions/offers` (S2S) headers. Auth is HTTP Basic over the
     * publisher id / secret; account id comes from local.properties. Device /
     * os / locale describe the requesting device. `User-Agent` and `Connection`
     * are added by OkHttp; `Content-Type` by the Retrofit converter.
     */
    private class RoktRequestInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder()
                .header("Accept", "application/json")
                .header(
                    "Authorization",
                    // Update ROKT_PUB_ID / ROKT_SECRET in local.properties.
                    Credentials.basic(BuildConfig.ROKT_PUB_ID, BuildConfig.ROKT_SECRET),
                )
                .header("rokt-account-id", BuildConfig.ROKT_ACCOUNT_ID) // Update value in local.properties
                .header("rokt-device-model", Build.MODEL)
                .header("rokt-os-type", OS_TYPE_ANDROID)
                .header("rokt-os-version", Build.VERSION.RELEASE.orEmpty())
                .header("rokt-platform-type", PLATFORM_TYPE_MOBILE)
                .header("rokt-ui-locale", Locale.getDefault().toLanguageTag())
                .build()
            return chain.proceed(request)
        }
    }
}
