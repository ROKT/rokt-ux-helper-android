package com.rokt.demoapp.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

class AssetRepository @Inject constructor(@ApplicationContext private val context: Context) {
    @Throws(IOException::class)
    fun getAssetContent(fileName: String): InputStream = context.assets.open(fileName)
}
