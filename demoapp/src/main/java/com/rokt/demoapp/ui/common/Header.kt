package com.rokt.roktdemo.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rokt.demoapp.ui.common.BackButton
import com.rokt.demoapp.ui.common.HeaderTextButton

@Composable
fun RoktHeader(content: @Composable RowScope.() -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(99.dp)
            .background(
                MaterialTheme.colorScheme.secondary,
            ),
    ) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            content()
        }
    }
}

@Composable
fun PreDefinedHeader(onBackPressed: () -> Unit) {
    RoktHeader {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            BackButton(backPressed = onBackPressed)
            HeaderTextButton("EXIT", { onBackPressed.invoke() })
        }
    }
}
