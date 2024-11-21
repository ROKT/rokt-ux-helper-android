package com.rokt.demoapp.ui.screen.tutorials.one

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rokt.demoapp.R
import com.rokt.demoapp.ui.common.BackButton
import com.rokt.demoapp.ui.common.Heading
import com.rokt.demoapp.ui.common.LoadingPage
import com.rokt.demoapp.ui.common.SmallSpace
import com.rokt.demoapp.ui.common.error.RoktError
import com.rokt.demoapp.ui.screen.layouts.HEADER_TOP_PADDING
import com.rokt.demoapp.ui.screen.tutorials.TutorialViewModel
import com.rokt.demoapp.ui.state.UiContent
import com.rokt.roktux.RoktLayout
import com.rokt.roktux.RoktUx
import com.rokt.roktux.RoktUxConfig
import com.rokt.roktux.imagehandler.NetworkStrategy

@Composable
fun TutorialOneCompose(backPressed: () -> Unit, viewModel: TutorialViewModel = hiltViewModel()) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White, RectangleShape),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Box(Modifier.padding(PaddingValues(start = 3.dp, top = HEADER_TOP_PADDING.dp))) {
                BackButton(backPressed, MaterialTheme.colorScheme.secondary)
            }
            Heading(text = stringResource(id = R.string.menu_button_tutorials))
            SmallSpace()
            val context = LocalContext.current
            val integrationInfo = remember {
                RoktUx.getIntegrationConfig(context).toJsonString()
            }
            val state = viewModel.state.collectAsState()
            LaunchedEffect(key1 = integrationInfo) {
                viewModel.handleInitialLoad(integrationInfo)
            }
            when {
                state.value.loading -> {
                    LoadingPage()
                }

                state.value.hasData -> {
                    val content = state.value.data
                    if (content is UiContent.ExperienceContent) {
                        RoktLayout(
                            experienceResponse = content.experienceResponse,
                            location = content.location,
                            onUxEvent = { println("RoktEvent: UxEvent Received $it") },
                            onPlatformEvent = { println("RoktEvent: onPlatformEvents received $it") },
                            roktUxConfig = RoktUxConfig.builder()
                                .imageHandlingStrategy(NetworkStrategy()).build(),
                        )
                    }
                }

                else -> {
                    RoktError(errorType = state.value.error)
                }
            }
        }
    }
}
