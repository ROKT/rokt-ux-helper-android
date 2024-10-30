package com.rokt.demoapp.ui.common

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.rokt.demoapp.R

@Composable
fun RoktBackground() {
    Image(
        painter = painterResource(id = R.drawable.ic_home_background),
        contentDescription = stringResource(R.string.content_description_rokt_bg),
        contentScale = ContentScale.FillBounds,
    )
}
