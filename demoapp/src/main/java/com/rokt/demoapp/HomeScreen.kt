package com.rokt.demoapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rokt.demoapp.util.DemoButton

/**
 * Entry screen listing the experiences the sample can render, mirroring `HomeView` on iOS.
 */
@Composable
fun HomeScreen(onLaunch: (SampleDestination) -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.ic_rokt_logo),
                contentDescription = stringResource(R.string.content_description_rokt_logo),
                modifier = Modifier
                    .padding(top = 64.dp)
                    .width(180.dp),
            )
            Text(
                text = stringResource(R.string.home_tagline),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 24.dp),
            )

            SampleDestination.entries.forEach { destination ->
                DemoButton(
                    text = stringResource(destination.labelRes),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onLaunch(destination) },
                )
            }

            Text(
                text = stringResource(R.string.footer_copyright),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 48.dp, bottom = 24.dp),
            )
        }
    }
}

/**
 * The experiences the sample renders.
 *
 * Each carries the bundled response and the location passed to RoktUX. `RoktLayout` renders the
 * container the layout asks for — overlay or bottom sheet — so the host composes it in place
 * rather than wrapping it in a dialog of its own.
 */
enum class SampleDestination(val labelRes: Int, val asset: String, val location: String) {
    Compose(R.string.button_compose, ExperienceAssets.TARGETED, ExperienceAssets.TARGETED_LOCATION),
    ComposeOverlay(R.string.button_compose_overlay, ExperienceAssets.OVERLAY, ""),
    ComposeBottomSheet(R.string.button_compose_bottom_sheet, ExperienceAssets.BOTTOM_SHEET, ""),
    ViewSystem(R.string.button_view_system, ExperienceAssets.TARGETED, ExperienceAssets.TARGETED_LOCATION),
}
