package com.rokt.networkhelper.data

import com.rokt.networkhelper.model.NetworkExperienceRequest
import com.rokt.networkhelper.network.RoktApiService
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.util.concurrent.TimeoutException
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.pow

internal interface RoktRepository {
    suspend fun experience(experienceRequest: NetworkExperienceRequest): Result<String>
    suspend fun postEvents(events: String): Result<Unit>
}

/**
 * Network Implementation of Repository that communicates with RoktBackend.
 */
internal class NetworkRoktRepository(private val roktApiService: RoktApiService) : RoktRepository {

    override suspend fun experience(experienceRequest: NetworkExperienceRequest): Result<String> = retry {
        roktApiService.experience(
            experienceRequest,
        ).string()
    }

    override suspend fun postEvents(events: String): Result<Unit> = retry {
        roktApiService.postEvents(
            events.toRequestBody("application/json".toMediaTypeOrNull()),
        )
    }
}

private suspend fun <T> retry(
    maxAttempts: Int = 3,
    delayMillis: Long = 100L,
    shouldRetry: (Throwable) -> Boolean = ::shouldRetry,
    block: suspend () -> T,
): Result<T> {
    repeat(maxAttempts) { attempt ->
        try {
            return Result.success(block())
        } catch (e: Exception) {
            if (e is CancellationException) throw e // Re-throw CancellationException
            if (!shouldRetry(e)) return Result.failure(e)
            if (attempt < maxAttempts - 1) {
                delay(delayMillis * 2.0.pow(attempt).toLong())
            }
        }
    }
    return Result.failure(Exception("Max retry attempts reached"))
}

fun shouldRetry(throwable: Throwable): Boolean {
    if (throwable is TimeoutException) {
        return true
    } else if (throwable is HttpException) {
        val errorCode = throwable.code()
        if (errorCode == HTTP_ERROR_INTERNAL ||
            errorCode == HTTP_ERROR_BAD_GATEWAY ||
            errorCode == HTTP_ERROR_SERVER_NOT_AVAILABLE
        ) {
            return true
        }
    }
    return false
}

const val HTTP_ERROR_INTERNAL = 500
const val HTTP_ERROR_BAD_GATEWAY = 502
const val HTTP_ERROR_SERVER_NOT_AVAILABLE = 503
