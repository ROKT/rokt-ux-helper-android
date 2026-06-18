package com.rokt.roktux.imagehandler

import android.content.Context
import androidx.compose.runtime.Immutable
import coil3.ImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.CachePolicy
import okhttp3.Call
import okhttp3.Interceptor
import okhttp3.OkHttpClient

@Immutable
interface ImageHandlingStrategy {
    fun getImageLoader(context: Context): ImageLoader
}

@Immutable
class NetworkStrategy : ImageHandlingStrategy {
    override fun getImageLoader(context: Context): ImageLoader = ImageLoader.Builder(context)
        .diskCachePolicy(CachePolicy.DISABLED)
        .build()
}

@Immutable
class OkHttpClientStrategy(private val client: Call.Factory) : ImageHandlingStrategy {
    override fun getImageLoader(context: Context): ImageLoader = ImageLoader.Builder(context)
        .diskCachePolicy(CachePolicy.DISABLED)
        .components {
            add(OkHttpNetworkFetcherFactory(client))
        }
        .build()
}

@Immutable
class OkHttpInterceptorStrategy(private val interceptor: Interceptor) : ImageHandlingStrategy {
    override fun getImageLoader(context: Context): ImageLoader = ImageLoader.Builder(context)
        .diskCachePolicy(CachePolicy.DISABLED)
        .components {
            add(OkHttpNetworkFetcherFactory(OkHttpClient.Builder().addInterceptor(interceptor).build()))
        }
        .build()
}

@Immutable
class ImageLoaderStrategy(private val imageLoader: ImageLoader) : ImageHandlingStrategy {
    override fun getImageLoader(context: Context): ImageLoader = imageLoader.newBuilder()
        .components {
            imageLoader.components.interceptors.forEach { add(it) }
        }
        .build()
}
