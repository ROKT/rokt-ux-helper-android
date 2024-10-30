package com.rokt.roktux.imagehandler

import android.net.Uri
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.decode.DataSource
import coil.decode.ImageSource
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.fetch.SourceResult
import coil.request.Options
import okio.ByteString.Companion.decodeBase64
import okio.buffer
import okio.source
import java.io.ByteArrayInputStream

/**
 * Coil's [Fetcher] component that adds data uri support
 */
class DataUriFetcher(
    private val data: Uri,
    private val options: Options,
) : Fetcher {

    @OptIn(ExperimentalCoilApi::class)
    override suspend fun fetch(): FetchResult {
        // Eg: data:image/jpeg;base64,<base64 data>
        val base64Data = data.toString().substringAfter(',').decodeBase64()
        val stream = ByteArrayInputStream(base64Data?.toByteArray())
        return SourceResult(
            source = ImageSource(
                source = stream.source().buffer(),
                context = options.context,
                metadata = null,
            ),
            dataSource = DataSource.MEMORY,
            mimeType = data.getMimeType(),
        )
    }

    class Factory : Fetcher.Factory<Uri> {

        // Create and return a [Fetcher] instance only when the URI format and mime type are supported
        override fun create(data: Uri, options: Options, imageLoader: ImageLoader): Fetcher? =
            if (isSupportedDataUri(data)) {
                DataUriFetcher(data, options)
            } else {
                null
            }

        private fun isSupportedDataUri(data: Uri): Boolean =
            (data.scheme == "data" && isFormatSupported(data.getMimeType()))

        private fun isFormatSupported(mimeType: String): Boolean = mimeType in SUPPORTED_FORMATS
    }

    companion object {
        private val SUPPORTED_FORMATS = arrayOf(
            "image/png",
            "image/jpg",
            "image/jpeg",
            "image/svg+xml",
        )

        private fun Uri.getMimeType(): String = this.toString().substringAfter(':').substringBefore(';')
    }
}
