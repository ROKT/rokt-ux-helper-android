package com.rokt.demoapp.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rokt.demoapp.ui.theme.RoktPink

@Composable
fun LoadingPage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            strokeWidth = 3.dp,
            color = RoktPink,
        )
    }
}
