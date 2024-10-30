package com.rokt.modelmapper.utils

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

const val CURRENT_POSITION_PLACEHOLDER = "%^CURRENT_OFFER^%"
const val TOTAL_OFFERS_PLACEHOLDER = "%^TOTAL_OFFERS^%"
const val FIRST_OFFER_INDEX = 0
const val DEFAULT_VIEWABLE_ITEMS = 1
const val ROKT_ICONS_FONT_FAMILY = "rokt-icons"

val roktDateFormat
    get() = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
