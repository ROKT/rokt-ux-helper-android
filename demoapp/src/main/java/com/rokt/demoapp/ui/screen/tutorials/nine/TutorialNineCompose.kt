package com.rokt.demoapp.ui.screen.tutorials.nine

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rokt.demoapp.R
import com.rokt.demoapp.ui.common.BackButton
import com.rokt.demoapp.ui.common.Heading
import com.rokt.demoapp.ui.common.LargeSpace
import com.rokt.demoapp.ui.common.LoadingPage
import com.rokt.demoapp.ui.common.SmallSpace
import com.rokt.demoapp.ui.common.SubHeading
import com.rokt.demoapp.ui.common.error.RoktError
import com.rokt.demoapp.ui.screen.layouts.HEADER_TOP_PADDING
import com.rokt.demoapp.ui.state.UiContent
import com.rokt.roktux.RoktLayout
import com.rokt.roktux.RoktUx
import com.rokt.roktux.RoktUxConfig
import com.rokt.roktux.imagehandler.NetworkStrategy

@Composable
fun TutorialNineCompose(backPressed: () -> Unit, viewModel: TutorialNineViewModel = hiltViewModel()) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White, RectangleShape),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
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
            // Load the fonts from the font resources
            val fontFamily = remember {
                val latoNormal = Font(resId = R.font.lato_regular, weight = FontWeight.Normal)
                val latoBold = Font(resId = R.font.lato_bold, weight = FontWeight.Bold)
                FontFamily(latoNormal, latoBold)
            }
            when {
                state.value.loading -> {
                    LoadingPage()
                }

                state.value.hasData -> {
                    when (val content = state.value.data) {
                        is UiContent.ExperienceContent -> {
                            RoktLayout(
                                experienceResponse = content.experienceResponse,
                                location = content.location,
                                onUxEvent = {
                                    println("RoktEvent: UxEvent Received $it")
                                    viewModel.handleUXEvent(event = it)
                                },
                                onPlatformEvent = {
                                    println("RoktEvent: onPlatformEvent received ${it.toJsonString()} $it")
                                    viewModel.handlePlatformEvent(it.toJsonString())
                                },
                                roktUxConfig = RoktUxConfig.builder().composeFontMap(mapOf("latofont" to fontFamily))
                                    .imageHandlingStrategy(NetworkStrategy()).build(),
                            )
                        }

                        is UiContent.PaymentSuccessContent -> {
                            Column(
                                Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Column(
                                    Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    LargeSpace()
                                    SubHeading(text = content.message)
                                }
                            }
                        }

                        is UiContent.PaymentFailureContent -> {
                            Column(
                                Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Column(
                                    Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    LargeSpace()
                                    SubHeading(text = content.message)
                                }
                            }
                        }

                        else -> {
                            RoktError(errorType = state.value.error)
                        }
                    }
                }

                else -> {
                    RoktError(errorType = state.value.error)
                }
            }
        }
    }
}
