package com.rokt.modelmapper.utils

import com.rokt.modelmapper.uimodel.CreativeLink

fun CreativeLink.transformToAnchorTag(): String {
    return "<a href=\"%s\">%s</a>".format(this.url, this.title)
}
