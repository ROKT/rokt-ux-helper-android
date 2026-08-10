package com.rokt.networkhelper.network

import com.rokt.networkhelper.model.NetworkOffersRequest
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

internal interface RoktApiService {
    @POST("v2/sessions/offers")
    suspend fun offers(@Body request: NetworkOffersRequest): ResponseBody

    // The RoktUXHelper platform-event payload is already in the v2/sessions/events
    // shape, so it is forwarded verbatim.
    @POST("v2/sessions/events")
    suspend fun postEvents(@Body body: RequestBody)
}
