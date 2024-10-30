package com.rokt.networkhelper.network

import com.rokt.networkhelper.model.NetworkExperienceRequest
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

internal interface RoktApiService {
    @POST("v2/partner/experiences")
    suspend fun experience(@Body experienceRequest: NetworkExperienceRequest): ResponseBody

    @POST("v2/partner/events")
    suspend fun postEvents(@Body body: RequestBody)
}
