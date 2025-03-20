package com.rokt.roktux.imagehandler

import android.content.Context
import androidx.compose.runtime.Immutable
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.CachePolicy
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
        .components {
            add(SvgDecoder.Factory())
            add(DataUriFetcher.Factory())
        }.build()
}

@Immutable
class OkHttpClientStrategy(private val client: Call.Factory) : ImageHandlingStrategy {
    override fun getImageLoader(context: Context): ImageLoader = ImageLoader.Builder(context)
        .callFactory(client)
        .diskCachePolicy(CachePolicy.DISABLED)
        .components {
            add(SvgDecoder.Factory())
            add(DataUriFetcher.Factory())
        }.build()
}

@Immutable
class OkHttpInterceptorStrategy(private val interceptor: Interceptor) : ImageHandlingStrategy {
    override fun getImageLoader(context: Context): ImageLoader = ImageLoader.Builder(context)
        .callFactory(OkHttpClient.Builder().addInterceptor(interceptor).build())
        .diskCachePolicy(CachePolicy.DISABLED)
        .components {
            add(SvgDecoder.Factory())
            add(DataUriFetcher.Factory())
        }.build()
}

@Immutable
class ImageLoaderStrategy(private val imageLoader: ImageLoader) : ImageHandlingStrategy {
    override fun getImageLoader(context: Context): ImageLoader = imageLoader.newBuilder()
        .components {
            imageLoader.components.interceptors.forEach { add(it) }
            add(SvgDecoder.Factory())
            add(DataUriFetcher.Factory())
        }.build()
}
