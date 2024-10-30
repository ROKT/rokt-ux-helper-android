package com.rokt.demoapp.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.getOrElse
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.selects.onTimeout
import kotlinx.coroutines.selects.whileSelect

fun Uri.openInBrowser(context: Context, onError: (Throwable) -> Unit) {
    val browserIntent = Intent(Intent.ACTION_VIEW, this)
    try {
        context.startActivity(browserIntent)
    } catch (ex: ActivityNotFoundException) {
        Toast.makeText(context, ex.message, Toast.LENGTH_SHORT).show()
        onError(ex)
    }
}

fun <T> Flow<T>.chunk(intervalMs: Long, maxSize: Int): Flow<List<T>> = channelFlow {
    coroutineScope {
        val upstreamCollection = Job()
        val upstream = produce<T>(capacity = maxSize) {
            collect { element -> channel.send(element) }
            upstreamCollection.complete()
        }

        whileSelect {
            upstreamCollection.onJoin {
                val chunk = upstream.drainAll(maxSize = maxSize)
                if (chunk.isNotEmpty()) send(chunk)
                false
            }

            onTimeout(intervalMs) {
                val chunk = upstream.drainAll(maxSize = maxSize)
                if (chunk.isNotEmpty()) send(chunk)
                true
            }
        }
    }
}

private tailrec fun <T> ReceiveChannel<T>.drainAll(
    accumulator: MutableList<T> = mutableListOf(),
    maxSize: Int,
): List<T> = if (accumulator.size == maxSize) {
    accumulator
} else {
    val nextValue = tryReceive().getOrElse { error: Throwable? -> error?.let { throw (it) } ?: return accumulator }
    accumulator.add(nextValue)
    drainAll(accumulator, maxSize)
}

fun ArrayList<Pair<String, String>>.updateKeyAtIndex(index: Int, newKey: String): ArrayList<Pair<String, String>> {
    this[index] = newKey to this[index].second
    return this
}

fun ArrayList<Pair<String, String>>.updateValueAtIndex(index: Int, newValue: String): ArrayList<Pair<String, String>> {
    this[index] = this[index].first to newValue
    return this
}
