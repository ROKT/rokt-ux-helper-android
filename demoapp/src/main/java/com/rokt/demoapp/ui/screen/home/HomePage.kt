package com.rokt.demoapp.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rokt.demoapp.R
import com.rokt.demoapp.ui.common.ButtonDark
import com.rokt.demoapp.ui.common.ButtonLight
import com.rokt.demoapp.ui.common.DEFAULT_SPACE
import com.rokt.demoapp.ui.common.DefaultSpace
import com.rokt.demoapp.ui.common.LARGE_SPACE
import com.rokt.demoapp.ui.common.LargeSpace
import com.rokt.demoapp.ui.screen.MainActions
import com.rokt.demoapp.ui.theme.DefaultFontFamily
import com.rokt.demoapp.ui.theme.RoktAndroidSdkTheme

@Composable
fun HomePage(actions: MainActions) {
    val viewModel: HomeViewModel = hiltViewModel()

    RoktAndroidSdkTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.White, RectangleShape),
        ) {
            HomeLogo()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(PaddingValues(0.dp, 0.dp, 0.dp, LARGE_SPACE.dp)),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                HomeButtons(viewModel, actions = actions)
                Footer()
                LargeSpace()
            }

            SettingsCog(
                modifier = Modifier
                    .padding(top = 35.dp)
                    .align(Alignment.TopEnd),
                actions = actions,
            )
        }
    }
}

@Composable
private fun HomeLogo() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.6F),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_rokt_logo),
                contentDescription = stringResource(R.string.content_description_rokt_logo),
                contentScale = ContentScale.FillBounds,
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth(0.8F)
                    .padding(PaddingValues(top = DEFAULT_SPACE.dp)),
                text = stringResource(R.string.content_description_rokt_title),
                fontFamily = DefaultFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun HomeButtons(viewModel: HomeViewModel, actions: MainActions) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth(0.9F)
            .background(Color.White, RectangleShape)
            .padding(PaddingValues(top = LARGE_SPACE.dp, bottom = DEFAULT_SPACE.dp)),
        verticalArrangement = Arrangement.SpaceAround,
    ) {
        ButtonDark(
            text = stringResource(R.string.menu_button_tutorials),
            onClick = actions.tutorialLibraryClicked,
        )
        DefaultSpace()
        ButtonDark(
            text = stringResource(id = R.string.menu_button_layouts_demo),
            onClick = actions.demoLayoutsClicked,
        )

        DefaultSpace()
        ButtonDark(
            text = stringResource(id = R.string.menu_button_custom_layouts),
            onClick = actions.customBuilderClicked,
        )

        DefaultSpace()
        ButtonLight(
            text = stringResource(R.string.menu_button_contact),
            onClick = {
                viewModel.contactUsClicked(context)
            },
        )
    }
}

@Composable
private fun Footer() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FooterText(stringResource(R.string.footer_copyright))
        FooterText(stringResource(R.string.footer_text_version))
    }
}

@Composable
private fun FooterText(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.tertiary,
        fontSize = 14.sp,
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal,
        lineHeight = 16.sp,
    )
}

@Composable
private fun SettingsCog(modifier: Modifier, actions: MainActions) {
    TextButton(
        modifier = modifier.then(Modifier.padding(10.dp)),
        onClick = actions.settingsClicked,
    ) {
        Image(
            imageVector = Icons.Default.Settings,
            contentDescription = stringResource(id = R.string.header_settings),
        )
    }
}
