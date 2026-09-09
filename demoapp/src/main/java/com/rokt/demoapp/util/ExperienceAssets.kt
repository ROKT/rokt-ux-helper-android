package com.rokt.demoapp.util

import android.content.Context

/**
 * Reads a bundled experience response from `assets/`.
 *
 * The sample app renders canned responses only — it never calls the Rokt API, so no
 * credentials or network permission are needed to run it.
 *
 * @param fileName asset name without the `.json` extension, e.g. `experience`.
 */
fun Context.getExperienceResponse(fileName: String): String =
    runCatching { assets.open("$fileName.json").bufferedReader().use { it.readText() } }
        .getOrElse { error("Sample app: missing experience asset `$fileName.json`") }
